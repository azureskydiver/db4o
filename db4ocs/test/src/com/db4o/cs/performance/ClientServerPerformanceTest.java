package com.db4o.cs.performance;

import com.db4o.ObjectContainer;
import com.db4o.cs.server.ClientServerTest;
import com.db4o.cs.server.protocol.protocol1.SetOperationHandler;
import com.db4o.query.Query;
import model.Person;

import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Results 2006-11-25:
 * OLD:
 * run 1:
 * avg open duration: 23 - avg close duration: 0
 * set 100,000 duration old: 16703
 * query 100,000 duration old: 28906
 * update 100,000 duration old: 44391
 * run 2:
 * opened 1000 times, avg open duration: 26 - avg close duration: 0
 * set 100,000 duration: 15594
 * query 100,000 duration:27235 - actualSize:100000
 * update 100,000 duration:42828 actualSize:200000
 * <p/>
 * NEW objectStream protocol:
 * opened 1000 times, avg open duration: 1 - avg close duration: 0
 * set 100,000 duration old: 27531
 * query 100,000 duration old: 24688
 * update 100,000 duration old: 41375
 * <p/>
 * NEW protocol1:
 * run1:
 * opened 1000 times, avg open duration: 1 - avg close duration: 0
 * set 100,000 duration: 22128
 * query 100,000 duration:24707 - actualSize:100000
 * update 100,000 duration:39022 actualSize:200000
 * run2 with caching ReflectClass's and ReflectField's:
 * opened 1000 times, avg open duration: 2 - avg close duration: 0
 * set 100,000 duration: 24438
 * query 100,000 duration:22172 - actualSize:100000
 * update 100,000 duration:42672 actualSize:200000
 * <p/>
 * Testing over the Internet from China to US:
 * opened 100 times, avg open duration: 1318 - avg close duration: 0
 * set 20,000 duration: 156555 = 7.8 ms per
 * query 20,000 duration:131188 = 6.6ms per
 * update 20,000 duration:351816 = 17.6 ms per
 * <p/>
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 2:00:19 AM
 */
public class ClientServerPerformanceTest extends OldVsNew implements Runnable {
	private static final int COUNT = 1000;

	public static void main(String[] args) throws IOException {
		ClientServerPerformanceTest ocs = new ClientServerPerformanceTest();
		ocs.run();
	}

	public void run() {
		try {
//		testOpening();
//		pause();
//			testSet();
//			testDelete();
			testCommit();
//		pause();
//		testSetCollection();
//		pause();
//		testQuery();
//		pause();
//		testUpdate();
//		pause();
//			testSetComplex();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void pause() {
		try {
			Thread.sleep(10000); // let it commit
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Will just open a connection a bunch of times and get the average time.
	 * Tested with Andrew 2006-11-26 across the internet:
	 * open duration: 33781
	 * close duration: 1219
	 * open duration: 33593
	 * close duration: 1329
	 * open duration: 35640
	 * close duration: 1219
	 * open duration: 31750
	 * close duration: 1125
	 * OUCH!  33 seconds just to open a connection!
	 * <p/>
	 * Andrew tried to connect to me and saw ~60 seconds.
	 */
	public void testOpening() throws IOException {
		long totalOpenDuration = 0;
		long totalCloseDuration = 0;
		int counter = 0;
		int numTimes = 100;
		System.out.println("Running testOpening " + numTimes + " times...");
		for (int i = 0; i < numTimes; i++) {
			long start = System.currentTimeMillis();
			ObjectContainer oc = openConnection();
			long end = System.currentTimeMillis();
			long duration = end - start;
			totalOpenDuration += duration;
			//System.out.println("open duration: " + duration);

			start = System.currentTimeMillis();
			oc.close();
			end = System.currentTimeMillis();
			duration = end - start;
			//System.out.println("close duration: " + duration);
			totalCloseDuration += duration;

			counter++;
			//if(counter % 10 == 0) System.out.println("connected " + counter + " times.");
		}
		System.out.println("opened " + numTimes + " times, total open duration: " + totalOpenDuration + ", total close duration: " + totalCloseDuration + ", avg open duration: " + (totalOpenDuration / counter) + " - avg close duration: " + (totalCloseDuration / counter));
	}

	/**
	 * Tests with Andrew: testSet for 1000 objects: 148934ms
	 *
	 * @throws IOException
	 */
	public void testSet() throws IOException {
		ObjectContainer oc = openConnection();
		long start = System.currentTimeMillis();
		insertPersons(oc, COUNT, false);
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("set " + COUNT + " duration: " + duration);
		oc.close();
	}

	public void testSetCollection() throws IOException {
		System.out.println("testSetCollection");
		ObjectContainer oc = openConnection();
		long start = System.currentTimeMillis();
		List x = new ArrayList();
		addPersonsToList(x, COUNT);
		oc.set(x);
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("set " + COUNT + " duration: " + duration);
		oc.close();
	}

	private void addPersonsToList(List x, int count) {
		for (int i = 0; i < count; i++) {
			Person p = new Person();
			p.setName("name" + i);
			x.add(p);
		}
	}


	public static void insertPersons(ObjectContainer oc, int count, boolean commitForEach) {
		for (int i = 0; i < count; i++) {
			Person p = new Person();
			p.setName("name" + i);
			oc.set(p);
			if (commitForEach) oc.commit();
			if (i % 100 == 0) System.out.println("set " + i + " people.");
		}
		if (!commitForEach) oc.commit();
	}

	public void testQuery() throws IOException {
		testSet();
		pause();
		
		ObjectContainer oc = openConnection();
		long start = System.currentTimeMillis();
		Query q = oc.query();
		List results = q.execute();
		int counter = 0;
		for (Iterator iterator = results.iterator(); iterator.hasNext();) {
			Person p = (Person) iterator.next();
			counter++;
		}
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("query " + COUNT + " duration:" + duration + " - actualSize:" + counter);
		oc.close();
	}

	public void testUpdate() throws IOException {
		testSet();
		pause();

		ObjectContainer oc = openConnection();
		long start = System.currentTimeMillis();
		Query q = oc.query();
		List results = q.execute();
		int counter = 0;
		for (Iterator iterator = results.iterator(); iterator.hasNext();) {
			Person p = (Person) iterator.next();
			p.setName("Updated name " + counter);
			oc.set(p);
			counter++;
		}
		oc.commit();
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("update " + COUNT + " duration:" + duration + " actualSize:" + counter);
		oc.close();
	}

	public void testDelete() throws IOException {
		System.out.println("testDelete");
		testSet();
		pause();

		ObjectContainer oc = openConnection();
		long start = System.currentTimeMillis();
		Query q = oc.query();
		List results = q.execute();
		int i = 0;
		for (Iterator iterator = results.iterator(); iterator.hasNext();) {
			Person p = (Person) iterator.next();
			oc.delete(p);
			i++;
			if (i % 100 == 0)
				System.out.println("deleted " + i + " people.");
		}
		System.out.println("before commit duration: " + (System.currentTimeMillis() - start));
		oc.commit();
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("delete " + COUNT + " duration:" + duration + " actualSize:" + i);
		oc.close();
	}

	public void testCommit() throws IOException {
		ObjectContainer oc = openConnection();
		long start = System.currentTimeMillis();
		insertPersons(oc, COUNT, true);
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("commit " + COUNT + " duration: " + duration);
		/*
		With new c/s, this goes uber quick through the first 500 or so objects, then degrades rapidly
		 */
		oc.close();
	}

	public void testSetComplex() throws IOException {
		System.out.println("testSetComplex");
		ObjectContainer oc = openConnection();
		int complexCount = 20;
		long start = System.currentTimeMillis();
		int totalCount = ClientServerTest.persistHierarchy(oc, complexCount);
		oc.commit();
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("set " + totalCount + " duration: " + duration);
		oc.close();
	}

}
