package com.db4o.cs.performance;

import com.db4o.ObjectServer;
import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.cs.client.Db4oClient;
import com.db4o.cs.server.Db4oServer;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * This test creates 14 producer threads and 14 consumer threads.
 * Each producer connects and sets a single object 10 times.
 * Each consumer connects, gets a single object, then deletes the object 10 times.
 *
 * <strong>Results 2006-11-25:</strong>
 * All results in milliseconds. <br/>
 * <br/>
 * Old:<br/>
run 1:
 produced 140 items, avg duration: 6595.878571428571<br/>
consumed 140 items, avg duration: 6669.971428571429<br/>
 run 2:<br/>
 produced 140 items, avg duration: 2716.614285714286<br/>
consumed 140 items, avg duration: 2824.9785714285713<br/>
 run 3:<br/>
 produced 140 items, avg duration: 2325.0857142857144<br/>
consumed 140 items, avg duration: 2441.15<br/>
 <br/>
 New objectStream protocol: <br/>
produced 140 items, avg duration: 9.471428571428572<br/>
consumed 140 items, avg duration: 2379.114285714286<br/>
<br/>
New protocol1:<br/>
run 1:<br/>
 produced 140 items, avg duration: 8.6<br/>
consumed 140 items, avg duration: 1438.642857142857<br/>
 run 2 with cached ReflectClass and ReflectField (expected results since this test opens a new connection for every set):<br/>
produced 140 items, avg duration: 8.692857142857143<br/>
consumed 140 items, avg duration: 1456.8857142857144<br/>

 
 * User: treeder
 * Date: Nov 23, 2006
 * Time: 4:18:11 PM
 */
public class ProducerConsumerTest extends OldVsNew {

	public static int NUMBER_TO_PRODUCE = 10;

	// STATS
	static long totalProducedDuration = 0;
	static long totalConsumedDuration = 0;
	static int produced = 0;
	static int consumed = 0;
	static ThreadMonitor monitor = new ThreadMonitor();


	public static void main(String[] args) {
		System.out.println("running helloworld");
		try {
			thread(new HelloWorldServer(args), true);
			Thread.sleep(1000);

			thread(new HelloWorldProducer(), false);
			thread(new HelloWorldProducer(), false);
			Thread.sleep(1000);

			thread(new HelloWorldConsumer(), false);
			thread(new HelloWorldConsumer(), false);

			thread(new HelloWorldProducer(), false);
			thread(new HelloWorldProducer(), false);
			thread(new HelloWorldConsumer(), false);
			Thread.sleep(1000);
			thread(new HelloWorldConsumer(), false);
			thread(new HelloWorldProducer(), false);
			thread(new HelloWorldConsumer(), false);
			thread(new HelloWorldProducer(), false);
			Thread.sleep(1000);
			thread(new HelloWorldConsumer(), false);
			thread(new HelloWorldProducer(), false);
			thread(new HelloWorldConsumer(), false);
			thread(new HelloWorldConsumer(), false);
			thread(new HelloWorldProducer(), false);
			thread(new HelloWorldProducer(), false);
			Thread.sleep(1000);
			thread(new HelloWorldProducer(), false);
			thread(new HelloWorldConsumer(), false);
			thread(new HelloWorldConsumer(), false);
			thread(new HelloWorldProducer(), false);
			thread(new HelloWorldConsumer(), false);
			thread(new HelloWorldProducer(), false);
			thread(new HelloWorldConsumer(), false);
			thread(new HelloWorldProducer(), false);
			thread(new HelloWorldConsumer(), false);
			thread(new HelloWorldConsumer(), false);
			thread(new HelloWorldProducer(), false);
			print("started all threads");
			while(monitor.stillGoing()){
				Thread.sleep(10000);
			}
			System.out.println("produced " + produced + " items, avg duration: " + (1.0 * totalProducedDuration / produced));
			System.out.println("consumed " + consumed + " items, avg duration: " + (1.0 * totalConsumedDuration / consumed));

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	static class ThreadMonitor{
		Map threadMap = new HashMap();

		public boolean stillGoing() {
			return threadMap.size() > 0;
		}

		public void remove(Thread thread) {
			Object t = threadMap.remove(thread.hashCode());
			System.out.println("removed thread: " + t);
			System.out.println(threadMap.size() + " threads remaining");
		}

		public void add(Thread thread) {
			threadMap.put(thread.hashCode(), thread);
		}
	}


	public static void thread(Runnable runnable, boolean isServer) {
		Thread brokerThread = new Thread(runnable);
		brokerThread.setDaemon(isServer);
		brokerThread.start();
		if(!isServer)
		monitor.add(brokerThread);
	}

	private static void print(String msg) {
		System.out.println(Thread.currentThread().getName() + ": " + msg + " -- " + new Date());
	}

	private static void consumed(long duration) {
		consumed++;
		totalConsumedDuration += duration;
	}

	private static void produced(long duration) {
		produced++;
		totalProducedDuration += duration;
	}

	public static class HelloWorldServer implements Runnable {
		private final String[] args;


		public HelloWorldServer(String[] args) {
			this.args = args;
		}

		public void run() {
			print("Starting server...");
			try {
				ObjectServer server = openServer();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}


	}

	public static class HelloWorldProducer implements Runnable {
		public void run() {
			print("Starting producer... ");
			try {
				for (int i = 0; i < NUMBER_TO_PRODUCE; i++) {
					long start = System.currentTimeMillis();
					ObjectContainer oc = openConnection();
					// Create an object to store
					String text = "Hello world " + i + "! From: " + Thread.currentThread().getName() + " : " + this.hashCode();
					Entry entry = new Entry(text);
					print("producing: " + text);
					oc.set(entry);
					oc.close();
					long end = System.currentTimeMillis();
					long duration = end - start;
					produced(duration);
				}
			} catch (Exception e) {
				print("Caught: " + e);
				e.printStackTrace();
			} finally {
				monitor.remove(Thread.currentThread());
			}
			print("Producer done. ");
		}


	}

	public static class HelloWorldConsumer implements Runnable {

		public void run() {
			print("Starting consumer...");
			try {
				for (int i = 0; i < NUMBER_TO_PRODUCE; i++) {
					long start = System.currentTimeMillis();
					ObjectContainer oc = openConnection();
					// Create a template for get
					//Entry entry = new Entry();
					// read message
					Object ob = readIfExists(oc, Entry.class);
					if (ob != null) {
						oc.delete(ob);
						if (ob instanceof Entry) {
							Entry e = (Entry) ob;
							String t = e.getText();
							//print("Received: " + t);
						} else {
							//print("Received object: " + ob);
						}
					} else {
						//print("Received NULL");
					}
					oc.close();
					long end = System.currentTimeMillis();
					long duration = end - start;
					consumed(duration);
				}
			} catch (Exception e) {
				System.out.println("Caught: " + e);
				e.printStackTrace();
			}finally {
				monitor.remove(Thread.currentThread());
			}
			print("Consumer done.");
		}


		public Object readIfExists(ObjectContainer oc, Class c) {
			List obs = oc.query(c);
			//print("ob.size: " + obs.size());
			Object ret = null;
			for (int i = 0; i < obs.size(); i++) {
				Object o = obs.get(i);
				//print("got ob: " + o);
				if (i == 0) {
					ret = o;
					break;
				}
			}
			return ret;
		}
	}

	public static class Entry implements Serializable {
		private String text;


		public Entry(String text) {
			this.text = text;
		}

		public Entry() {

		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String toString() {
			return "Entry: " + text;
		}
	}
}

