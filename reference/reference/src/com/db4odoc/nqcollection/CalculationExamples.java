/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.nqcollection;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.ext.Db4oException;
import com.db4o.query.Predicate;
import com.db4o.query.QueryComparator;

public class CalculationExamples {

	private final static String DB4O_FILE_NAME = "reference.db4o";

	private final static int OBJECT_COUNT = 10;

	private static ObjectContainer _container = null;

	public static void main(String[] args) {
		storePilots();
		sumPilotPoints();
		selectMinPointsPilot();
		averagePilotPoints();
		countSubGroups();
	}

	// end main

	private static ObjectContainer database() {
		if (_container == null) {
			try {
				_container = Db4o.openFile(DB4O_FILE_NAME);
			} catch (DatabaseFileLockedException ex) {
				System.out.println(ex.getMessage());
			}
		}
		return _container;
	}

	// end database

	private static void closeDatabase() {
		if (_container != null) {
			_container.close();
			_container = null;
		}
	}

	// end closeDatabase

	private static void storePilots() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = database();
		if (container != null) {
			try {
				Pilot pilot;
				for (int i = 0; i < OBJECT_COUNT; i++) {
					pilot = new Pilot("Test Pilot #" + i, i);
					container.store(pilot);
				}
				for (int i = 0; i < OBJECT_COUNT; i++) {
					pilot = new Pilot("Professional Pilot #" + (i + 10), i + 10);
					container.store(pilot);
				}
				container.commit();
			} catch (Db4oException ex) {
				System.out.println("Db4o Exception: " + ex.getMessage());
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end storePilots

	private static void sumPilotPoints() {
		ObjectContainer container = database();

		if (container != null) {
			try {
				SumPredicate sumPredicate = new SumPredicate();
				List<Pilot> result = container.query(sumPredicate);
				listResult(result);
				System.out.println("Sum of pilots points: " + sumPredicate.sum);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end sumPilotPoints

	private static class SumPredicate extends Predicate<Pilot> {
		private int sum = 0;

		public boolean match(Pilot pilot) {
			// return all pilots
			sum += pilot.getPoints();
			return true;
		}
	}

	// end SumPredicate

	private static void selectMinPointsPilot() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Pilot> result = container.query(new Predicate<Pilot>() {
					public boolean match(Pilot pilot) {
						// return all pilots
						return true;
					}
				}, new QueryComparator<Pilot>() {
					// sort by points then by name
					public int compare(Pilot p1, Pilot p2) {
						return p1.getPoints() - p2.getPoints();
					}
				});
				if (result.size() > 0) {
					System.out.println("The min points result is: "
							+ result.get(0));
				}
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end selectMinPointsPilot

	private static void averagePilotPoints() {
		ObjectContainer container = database();

		if (container != null) {
			try {
				AveragePredicate averagePredicate = new AveragePredicate();
				List<Pilot> result = container.query(averagePredicate);
				if (averagePredicate.count > 0) {
					System.out
							.println("Average points for professional pilots: "
									+ averagePredicate.sum
									/ averagePredicate.count);
				} else {
					System.out.println("No results");
				}
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end averagePilotPoints

	private static class AveragePredicate extends Predicate<Pilot> {
		private int sum = 0;

		private int count = 0;

		public boolean match(Pilot pilot) {
			// return professional pilots
			if (pilot.getName().startsWith("Professional")) {
				sum += pilot.getPoints();
				count++;
				return true;
			}
			return false;
		}
	}

	// end AveragePredicate

	private static class CountPredicate extends Predicate<Pilot> {

		private HashMap countMap = new HashMap();

		public boolean match(Pilot pilot) {
			// return all Professional and Test pilots and count in
			// each category
			String[] keywords = { "Professional", "Test" };
			for (int i = 0; i < keywords.length; i++) {
				if (pilot.getName().startsWith(keywords[i])) {
					if (countMap.containsKey(keywords[i])) {
						countMap.put(keywords[i], ((Integer) countMap
								.get(keywords[i])) + 1);
					} else {
						countMap.put(keywords[i], 1);
					}
					return true;
				}
			}
			return false;
		}
	}

	// end CountPredicate

	private static void countSubGroups() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				CountPredicate predicate = new CountPredicate();
				List<Pilot> result = container.query(predicate);
				listResult(result);
				Iterator keyIterator = predicate.countMap.keySet().iterator();
				while (keyIterator.hasNext()) {
					String key = keyIterator.next().toString();
					System.out
							.println(key + ": " + predicate.countMap.get(key));
				}
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end countSubGroups

	private static void listResult(List result) {
		System.out.println(result.size());
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
		}
	}

	// end listResult

}
