/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.nqsyntax;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4odoc.nqcollection.Pilot;

/**
 * @sharpen.ignore
 */
public class NQSyntaxExamples {

	private final static String DB4O_FILE_NAME = "reference.db4o";

	private final static int OBJECT_COUNT = 10;

	private static ObjectContainer _container = null;

	public static void main(String[] args) {
		storePilots();
		querySyntax1();
		querySyntax2();
		querySyntax3();
		querySyntax4();
		// querySyntax5();
	}

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

	private static void querySyntax1() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Pilot> result = container.query(Pilot.class);
				container.ext().configure().freespace();
				listResult(result);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end querySyntax1

	private static void querySyntax2() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Pilot> result = container.query(new com.db4o.query.Predicate<Pilot>() {
					public boolean match(Pilot pilot) {
						// each Pilot is included in the result
						return true;
					}
				});
				listResult(result);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end querySyntax2

	private static void querySyntax3() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Pilot> result = container.query(new com.db4o.query.Predicate<Pilot>() {
					public boolean match(Pilot pilot) {
						// each Pilot is included in the result
						return true;
					}
				}, new Comparator<Pilot>() {
					public int compare(Pilot pilot1, Pilot pilot2) {
						return pilot1.getPoints() - pilot2.getPoints();
					}
				});
				listResult(result);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end querySyntax3

	private static class PilotPredicate extends com.db4o.query.Predicate<Pilot> {
		public boolean match(Pilot pilot) {
			// each Pilot is included in the result
			return true;
		}
	}

	// end PilotPredicate

	private static class PilotComparator implements Comparator<Pilot> {
		public int compare(Pilot pilot1, Pilot pilot2) {
			return pilot1.getPoints() - pilot2.getPoints();
		}
	}

	// end PilotComparator

	private static void querySyntax4() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Pilot> result = container.query(new PilotPredicate(),
						new PilotComparator());
				listResult(result);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end querySyntax4

	private static class PilotPredicateNotGeneric extends com.db4o.query.Predicate {
		public boolean match(Object obj) {
			// each Pilot is included in the result
			if (obj instanceof Pilot) {
				return true;
			}
			return false;
		}
	}

	// end PilotPredicateNotGeneric

	private static class PilotComparatorNotGeneric implements Comparator {
		public int compare(Object object1, Object object2) {
			return ((Pilot) object1).getPoints()
					- ((Pilot) object2).getPoints();
		}
	}

	// end PilotComparatorNotGeneric

	private static void querySyntax5() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List result = container.query(new com.db4o.query.Predicate() {
					public boolean match(Object obj) {
						// each Pilot is included in the result
						if (obj instanceof Pilot) {
							return true;
						}
						return false;
					}
				}, new Comparator() {
					public int compare(Object object1, Object object2) {
						return ((Pilot) object1).getPoints()
								- ((Pilot) object2).getPoints();
					}
				});
				listResult(result);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end querySyntax5

	private static void querySyntax6() {
		// this example will only work with java versions without
		// generics support
		ObjectContainer container = database();
		if (container != null) {
			try {
				List result = container.query(new com.db4o.query.Predicate() {
					public boolean match(Object obj) {
						// each Pilot is included in the result
						if (obj instanceof Pilot) {
							return true;
						}
						return false;
					}
				}, new QueryComparator() {
					public int compare(Object pilot1, Object pilot2) {
						return ((Pilot) pilot1).getPoints()
								- ((Pilot) pilot2).getPoints();
					}
				});
				listResult(result);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end querySyntax6

	private static void listResult(List result) {
		System.out.println(result.size());
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
		}
	}

	// end listResult
}
