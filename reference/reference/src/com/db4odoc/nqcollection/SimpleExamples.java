/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.nqcollection;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.ext.Db4oException;
import com.db4o.query.Predicate;
import com.db4o.query.QueryComparator;

public class SimpleExamples {

	private final static String DB4O_FILE_NAME = "reference.db4o";

	private final static int OBJECT_COUNT = 10;

	private static ObjectContainer _container = null;

	public static void main(String[] args) {
		storePilots();
		selectAllPilots();
		selectAllPilotsNonGeneric();
        selectPilot5Points();
		selectTestPilots();
		selectPilotsNumberX6();
		selectTestPilots6PointsMore();
		selectPilots6To12Points();
		selectPilotsRandom();
		selectPilotsEven();
		selectAnyOnePilot();
		getSortedPilots();
		getPilotsSortByNameAndPoints();
		selectAndChangePilots();
		storeDuplicates();
		selectDistinctPilots();
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

	private static void storeDuplicates() {
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
					pilot = new Pilot("Test Pilot #" + i, i);
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

	// end storeDuplicates

	private static void selectAllPilots() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Pilot> result = container.query(new Predicate<Pilot>() {
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

	// end selectAllPilots

	private static void selectPilot5Points() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Pilot> result = container.query(new Predicate<Pilot>() {
					public boolean match(Pilot pilot) {
						// pilots with 5 points are included in the
						// result
						return pilot.getPoints() == 5;
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

	// end selectPilot5Points

	private static void selectAllPilotsNonGeneric() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List result = container.query(new Predicate() {
					public boolean match(Object object) {
						// each Pilot is included in the result
						if (object instanceof Pilot) {
							return true;
						}
						return false;
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

	// end selectAllPilotsNonGeneric

	private static void selectTestPilots() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Pilot> result = container.query(new Predicate<Pilot>() {
					public boolean match(Pilot pilot) {
						// all Pilots containing "Test" in the name
						// are included in the result
						return pilot.getName().indexOf("Test") >= 0;
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

	// end selectTestPilots

	private static void selectPilotsNumberX6() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Pilot> result = container.query(new Predicate<Pilot>() {
					public boolean match(Pilot pilot) {
						// all Pilots with the name ending with 6 will
						// be included
						return pilot.getName().endsWith("6");
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

	// end selectPilotsNumberX6

	private static void selectTestPilots6PointsMore() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Pilot> result = container.query(new Predicate<Pilot>() {
					public boolean match(Pilot pilot) {
						// all Pilots containing "Test" in the name
						// and 6 point are included in the result
						boolean b1 = pilot.getName().indexOf("Test") >= 0;
						boolean b2 = pilot.getPoints() > 6;
						return b1 && b2;
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

	// end selectTestPilots6PointsMore

	private static void selectPilots6To12Points() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Pilot> result = container.query(new Predicate<Pilot>() {
					public boolean match(Pilot pilot) {
						// all Pilots having 6 to 12 point are
						// included in the result
						return ((pilot.getPoints() >= 6) && (pilot.getPoints() <= 12));
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

	// end selectPilots6To12Points

	private static void selectPilotsRandom() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Pilot> result = container.query(new Predicate<Pilot>() {
					private ArrayList randomArray = null;

					private List getRandomArray() {
						if (randomArray == null) {
							randomArray = new ArrayList();
							for (int i = 0; i < 10; i++) {
								randomArray.add((int) (Math.random() * 10));
								System.out.println(randomArray.get(i));
							}
						}
						return randomArray;
					}

					public boolean match(Pilot pilot) {
						// all Pilots having points in the values of
						// the randomArray
						return getRandomArray().contains(pilot.getPoints());
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

	// end selectPilotsRandom

	private static void selectPilotsEven() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Pilot> result = container.query(new Predicate<Pilot>() {
					public boolean match(Pilot pilot) {
						// all Pilots having even points
						return pilot.getPoints() % 2 == 0;
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

	// end selectPilotsEven

	private static void selectAnyOnePilot() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Pilot> result = container.query(new Predicate<Pilot>() {
					boolean selected = false;

					public boolean match(Pilot pilot) {
						// return only first result (first result can
						// be any value from the resultset)
						if (!selected) {
							selected = true;
							return selected;
						} else {
							return !selected;
						}
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

	// end selectAnyOnePilot

	public static void getSortedPilots() {
		ObjectContainer container = database();
		try {
			List result = container.query(new Predicate<Pilot>() {
				public boolean match(Pilot pilot) {
					return true;
				}
			}, new QueryComparator<Pilot>() {
				// sort by points
				public int compare(Pilot p1, Pilot p2) {
					return p2.getPoints() - p1.getPoints();
				}
			});
			listResult(result);
		} finally {
			closeDatabase();
		}
	}
	// end getSortedPilots

	public static void getPilotsSortByNameAndPoints() {
		ObjectContainer container = database();
		try {
			List result = container.query(new Predicate<Pilot>() {
				public boolean match(Pilot pilot) {
					return true;
				}
			}, new QueryComparator<Pilot>() {
				// sort by name then by points: descending
				public int compare(Pilot p1, Pilot p2) {
					int result = p1.getName().compareTo(p2.getName());
					if (result == 0) {
						return p1.getPoints() - p2.getPoints();
					} else {
						return -result;
					}
				}
			});
			listResult(result);
		} finally {
			closeDatabase();
		}
	}

	// end getPilotsSortByNameAndPoints

	public static void getPilotsSortWithComparator() {
		ObjectContainer container = database();
		try {
			List result = container.query(new Predicate<Pilot>() {
				public boolean match(Pilot pilot) {
					return true;
				}
			}, new PilotComparator());
			listResult(result);
		} finally {
			closeDatabase();
		}
	}

	// end getPilotsSortWithComparator

	public static class PilotComparator implements Comparator<Pilot> {
		public int compare(Pilot p1, Pilot p2) {
			int result = p1.getName().compareTo(p2.getName());
			if (result == 0) {
				return p1.getPoints() - p2.getPoints();
			} else {
				return -result;
			}
		}
	}

	// end PilotComparator

	
	
	static class DistinctPilotsPredicate extends Predicate <Pilot>
	{
		static HashSet<Pilot> uniqueResult = new HashSet<Pilot>();	
		
		public boolean match(Pilot pilot) {
			// each Pilot is included in the result
			uniqueResult.add(pilot);
			return false;
		}
	}
	// end DistinctPilotsPredicate

	private static void selectDistinctPilots() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				DistinctPilotsPredicate predicate = new DistinctPilotsPredicate();
				List<Pilot> result = container.query(predicate);
				listResult(predicate.uniqueResult);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end selectDistinctPilots

	private static void selectAndChangePilots() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Pilot> result = container.query(new Predicate<Pilot>() {
					public boolean match(Pilot pilot) {
						// Add ranking to the pilots during the query.
						// Note: pilot records in the database won't
						// be changed!!!
						if (pilot.getPoints() <= 5) {
							pilot.setName(pilot.getName() + ": weak");
						} else if (pilot.getPoints() > 5
								&& pilot.getPoints() <= 15) {
							pilot.setName(pilot.getName() + ": average");
						} else if (pilot.getPoints() > 15) {
							pilot.setName(pilot.getName() + ": strong");
						}
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

	// end selectAndChangePilots

	private static void listResult(List result) {
		System.out.println(result.size());
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
		}
	}

	// end listResult

	private static void listResult(Set result) {
		System.out.println(result.size());
		Iterator i = result.iterator();
		while (i.hasNext()) {
			System.out.println(i.next());
		}
	}
	// end listResult

}
