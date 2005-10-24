package com.db4o.test.nativequery;

import java.lang.reflect.*;

import com.db4o.*;
import com.db4o.inside.query.*;
import com.db4o.nativequery.main.*;
import com.db4o.query.*;
import com.db4o.test.*;


public class NQRegressionTests {
	private static abstract class Base {
		int id;
		
		public Base(int id) {
			this.id=id;
		}

		public int getId() {
			return id;
		}
}
	
	private static class Data extends Base {
		float value;
		String name;
		Data prev;
		
		public Data(int id, float value, String name,Data prev) {
			super(id);
			this.value=value;
			this.name = name;
			this.prev=prev;
		}

		public float getValue() {
			return value;
		}

		public String getName() {
			return name;
		}

		public Data getPrev() {
			return prev;
		}	
	}

	public static void main(String[] args) {
		Test.run(NQRegressionTests.class);
	}

	public void store() {
		Data a=new Data(1,1.1f,"Aa",null);
		Data b=new Data(2,1.1f,"Bb",a);
		Data c=new Data(3,2.2f,"Cc",b);
		Data cc=new Data(3,3.3f,"Cc",null);
		Test.store(a);
		Test.store(b);
		Test.store(c);
		Test.store(cc);
	}
	
	private abstract static class ExpectingPredicate extends Predicate {
		public abstract int expected();
	}
	
	private static ExpectingPredicate[] PREDICATES={
		// unconditional
		new ExpectingPredicate() {
			public int expected() { return 4;}
			public boolean match(Data candidate) {
				return true;
			}
		},
//		new ExpectingPredicate() {
//			public int expected() { return 0;}
//			public boolean match(Data candidate) {
//				return false;
//			}
//		},
		// primitive equals
		new ExpectingPredicate() {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.id==1;
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.id==3;
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.value==1.1f;
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.value==3.3f;
			}
		},
		// string equals
		new ExpectingPredicate() {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.name.equals("Aa");
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.name.equals("Cc");
			}
		},
		// int field comparison
		new ExpectingPredicate() {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.id<2;
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.id>2;
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.id<=2;
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 3;}
			public boolean match(Data candidate) {
				return candidate.id>=2;
			}
		},
		// float field comparison
		new ExpectingPredicate() {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.value>2.9f;
			}
		},
		// descend field
		new ExpectingPredicate() {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.getPrev()!=null&&candidate.getPrev().getId()>=1;
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return (candidate.getPrev()!=null)&&("Bb".equals(candidate.getPrev().getName()));
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 0;}
			public boolean match(Data candidate) {
				return candidate.getPrev()!=null&&candidate.getPrev().getName().equals("");
			}
		},
		// getter comparisons
		new ExpectingPredicate() {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.getId()==2;
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.getId()<2;
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.getId()>2;
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.getId()<=2;
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 3;}
			public boolean match(Data candidate) {
				return candidate.getId()>=2;
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.getName().equals("Cc");
			}
		},
		// negation
		new ExpectingPredicate() {
			public int expected() { return 3;}
			public boolean match(Data candidate) {
				return !(candidate.id==1);
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return !(candidate.getId()>2);
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return !(candidate.getName().equals("Cc"));
			}
		},
		// conjunction
		new ExpectingPredicate() {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return (candidate.id>1)&&candidate.getName().equals("Cc");
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return (candidate.id>1)&&(candidate.getId()<=2);
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 0;}
			public boolean match(Data candidate) {
				return (candidate.id>1)&&(candidate.getId()<1);
			}
		},
		// disjunction
		new ExpectingPredicate() {
			public int expected() { return 3;}
			public boolean match(Data candidate) {
				return (candidate.id==1)||candidate.getName().equals("Cc");
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 4;}
			public boolean match(Data candidate) {
				return (candidate.id>1)||(candidate.getId()<=2);
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 3;}
			public boolean match(Data candidate) {
				return (candidate.id<=1)||(candidate.getId()>=3);
			}
		},
		// nested boolean
		new ExpectingPredicate() {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return ((candidate.id>=1)||candidate.getName().equals("Cc"))&&candidate.getId()<3;
			}
		},
		new ExpectingPredicate() {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return ((candidate.id==2)||candidate.getId()<=1)&&!candidate.getName().equals("Bb");
			}
		},
		// predicate member comparison
		new ExpectingPredicate() {
			private int id=2;
			
			public int expected() { return 3;}
			public boolean match(Data candidate) {
				return candidate.id>=id;
			}
		},
		new ExpectingPredicate() {
			private String name="Bb";
			
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.getName().equals(name);
			}
		},
		// arithmetic
		new ExpectingPredicate() {
			private int id=2;
			
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.id>=id+1;
			}
		},
		new ExpectingPredicate() {
			private int factor=2;
			
			private int calc() {
				return factor+1;
			}
			
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.id>=calc();
			}
		},
		new ExpectingPredicate() {
			private float predFactor=2.0f;
			
			private float calc() {
				return predFactor*1.1f;
			}
			
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.getValue()==calc();
			}
		}
	};
		
	public void testAll() {
		for (int predIdx = 0; predIdx < PREDICATES.length; predIdx++) {
			ExpectingPredicate predicate = PREDICATES[predIdx];
			assertNQResult(predicate);
		}
	}
	
	private void assertNQResult(final ExpectingPredicate filter) {
		ObjectContainer db=Test.objectContainer();
		Db4oQueryExecutionListener listener = new Db4oQueryExecutionListener() {
			private int run=0;
			
			public void notifyQueryExecuted(Predicate actualPredicate, String msg) {
				if(run<2) {
					Test.ensureEquals(actualPredicate,filter);
				}
				String expMsg=null;
				switch(run) {
					case 0:
						expMsg=YapStream.UNOPTIMIZED;
						break;
					case 1:
						expMsg=YapStream.DYNOPTIMIZED;
						break;
					case 2:
						expMsg=YapStream.PREOPTIMIZED;
						break;
				}
				Test.ensureEquals(expMsg,msg);
				run++;
			}
		};
		((YapStream)db).addListener(listener);
		db.ext().configure().optimizeNativeQueries(false);
		ObjectSet raw=db.query(filter);
		db.ext().configure().optimizeNativeQueries(true);
		ObjectSet optimized=db.query(filter);
		if(!raw.equals(optimized)) {
			System.out.println("RAW");
			raw.reset();
			while(raw.hasNext()) {
				System.out.println(raw.next());
			}
			System.out.println("OPT");
			optimized.reset();
			while(optimized.hasNext()) {
				System.out.println(optimized.next());
			}
		}
		Test.ensure(raw.equals(optimized));
		Test.ensureEquals(filter.expected(),raw.size());

		db.ext().configure().optimizeNativeQueries(false);
		try {
			Db4oEnhancingClassloader loader=new Db4oEnhancingClassloader(getClass().getClassLoader());
			Class filterClass=loader.loadClass(filter.getClass().getName());
			Constructor constr=filterClass.getDeclaredConstructor(new Class[]{});
			constr.setAccessible(true);
			Predicate predicate=(Predicate)constr.newInstance(new Object[]{});
			ObjectSet preoptimized=db.query(predicate);
			Test.ensureEquals(filter.expected(),preoptimized.size());
			Test.ensure(raw.equals(preoptimized));
			Test.ensure(optimized.equals(preoptimized));
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		((YapStream)db).clearListeners();
	}

	// TODO incorporate
	
//	public void testMixedFieldComparisons() {
//		 FIXME
//				assertNQResult(new Predicate() {
//					public boolean match(Data candidate) {
//						return candidate.value>2.9;
//					}
//				},1);
//	}
	
//	public void testPredicateMemberComparison() {
//		final int id=2;
//		final String name="Aa";
//		assertNQResult(new Predicate() {
//			public boolean match(Data candidate) {
//				return candidate.getName().equals(name)||candidate.getId()<=id;
//			}
//		},2);
//	}	
}
