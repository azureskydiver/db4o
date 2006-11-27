package com.db4o.cs.performance;

import com.db4o.ObjectContainer;
import com.db4o.query.Query;
import model.Person;

import java.io.IOException;
import java.util.List;
import java.util.Iterator;

/**
 * Results 2006-11-25:
 * OLD:
 run 1:
 avg open duration: 23 - avg close duration: 0
set 100,000 duration old: 16703
query 100,000 duration old: 28906
update 100,000 duration old: 44391
 run 2:
 opened 1000 times, avg open duration: 26 - avg close duration: 0
set 100,000 duration: 15594
query 100,000 duration:27235 - actualSize:100000
update 100,000 duration:42828 actualSize:200000

 NEW objectStream protocol:
 opened 1000 times, avg open duration: 1 - avg close duration: 0
set 100,000 duration old: 27531
query 100,000 duration old: 24688
update 100,000 duration old: 41375

 NEW protocol1:
run1:
 opened 1000 times, avg open duration: 1 - avg close duration: 0
set 100,000 duration: 22128
query 100,000 duration:24707 - actualSize:100000
update 100,000 duration:39022 actualSize:200000
 run2 with caching ReflectClass's and ReflectField's:
opened 1000 times, avg open duration: 2 - avg close duration: 0
set 100,000 duration: 24438
query 100,000 duration:22172 - actualSize:100000
update 100,000 duration:42672 actualSize:200000


 * User: treeder
 * Date: Oct 31, 2006
 * Time: 2:00:19 AM
 */
public class ClientServerPerformanceTest extends OldVsNew {

	public static void main(String[] args) throws IOException {
		ClientServerPerformanceTest ocs = new ClientServerPerformanceTest();
		ocs.start();
	}

	public void start() throws IOException {

		testOpening();
		pause();
		testSet();
		pause();
		testQuery();
		pause();
		testUpdate();

	}

	private void pause() {
		try {
			Thread.sleep(10000); // let it commit
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Will just open a connection a bunch of times and get the average time
	 */
	public void testOpening() throws IOException {
		long totalOpenDuration = 0;
		long totalCloseDuration = 0;
		int counter = 0;
		int numTimes = 1000;
		for (int i = 0; i < numTimes; i++) {
			long start = System.currentTimeMillis();
			ObjectContainer oc = openConnection();
			long end = System.currentTimeMillis();
			long duration = end - start;
			totalOpenDuration += duration;

			start = System.currentTimeMillis();
			oc.close();
			end = System.currentTimeMillis();
			duration = end - start;
			totalCloseDuration += duration;

			counter++;
		}
		System.out.println("opened " + numTimes + " times, avg open duration: " + (totalOpenDuration / counter) + " - avg close duration: " + (totalCloseDuration / counter));
	}

	public void testSet() throws IOException {
		ObjectContainer oc = openConnection();
		long start = System.currentTimeMillis();
		insertPersons(oc, 100000);
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("set 100,000 duration: " + duration);
		oc.close();
	}

	public static void insertPersons(ObjectContainer oc, int count) {
		for (int i = 0; i < count; i++) {
			Person p = new Person();
			p.setName("name" + i);
			oc.set(p);
		}
		oc.commit();
	}

	public void testQuery() throws IOException {
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
		System.out.println("query 100,000 duration:" + duration + " - actualSize:" + counter);
		oc.close();
	}

	public void testUpdate() throws IOException {
		ObjectContainer oc = openConnection();
		long start = System.currentTimeMillis();
		Query q = oc.query();
		List results = q.execute();
		int counter = 0;
		for (Iterator iterator = results.iterator(); iterator.hasNext();) {
			Person p = (Person) iterator.next();
			p.setName("Updated name " + counter++);
			oc.set(p);
			counter++;
		}
		oc.commit();
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("update 100,000 duration:" + duration + " actualSize:" + counter);
		oc.close();
	}


}