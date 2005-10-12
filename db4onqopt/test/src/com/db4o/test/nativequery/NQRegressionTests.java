package com.db4o.test.nativequery;

import java.lang.reflect.*;
import java.util.*;

import com.db4o.*;
import com.db4o.inside.query.*;
import com.db4o.nativequery.main.*;
import com.db4o.query.*;
import com.db4o.test.*;


public class NQRegressionTests {

	private static class Data {
		public int id;
		public float value;
		public String name;
		public Data prev;
		
		public Data(int id, float value, String name,Data prev) {
			this.id = id;
			this.value=value;
			this.name = name;
			this.prev=prev;
		}

		public int getId() {
			return id;
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
	
	public void testPrimitiveFieldEquals() {
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.id==1;
			}
		},1);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.id==3;
			}
		},2);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.value==1.1f;
			}
		},2);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.value==3.3f;
			}
		},1);
	}

	public void testStringFieldEquals() {
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.name.equals("Aa");
			}
		},1);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.name.equals("Cc");
			}
		},2);
	}

	public void testIntFieldComparisons() {
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.id<2;
			}
		},1);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.id>2;
			}
		},2);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.id<=2;
			}
		},2);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.id>=2;
			}
		},3);
	}

	public void testDescendField() {
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.getPrev()!=null&&candidate.getPrev().getId()>=1;
			}
		},2);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return (candidate.getPrev()!=null)&&("Bb".equals(candidate.getPrev().getName()));
			}
		},1);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.getPrev()!=null&&candidate.getPrev().getName().equals("");
			}
		},0);
	}

	public void testGetterComparisons() {
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.getId()==2;
			}
		},1);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.getId()<2;
			}
		},1);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.getId()>2;
			}
		},2);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.getId()<=2;
			}
		},2);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.getId()>=2;
			}
		},3);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.getName().equals("Cc");
			}
		},2);
	}

	public void testNegation() {
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return !(candidate.id==1);
			}
		},3);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return !(candidate.getId()>2);
			}
		},2);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return !(candidate.getName().equals("Cc"));
			}
		},2);
	}

	public void testConjunction() {
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return (candidate.id>1)&&candidate.getName().equals("Cc");
			}
		},2);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return (candidate.id>1)&&(candidate.getId()<=2);
			}
		},1);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return (candidate.id>1)&&(candidate.getId()<1);
			}
		},0);
	}

	public void testDisjunction() {
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return (candidate.id==1)||candidate.getName().equals("Cc");
			}
		},3);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return (candidate.id>1)||(candidate.getId()<=2);
			}
		},4);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return (candidate.id<=1)||(candidate.getId()>=3);
			}
		},3);
	}

	public void testNestedBoolean() {
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return ((candidate.id>=1)||candidate.getName().equals("Cc"))&&candidate.getId()<3;
			}
		},2);
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return ((candidate.id==2)||candidate.getId()<=1)&&!candidate.getName().equals("Bb");
			}
		},1);
	}	

	public void testPredicateMemberComparison() {
		// HERE
		assertNQResult(new Predicate() {
			private int id=2;
			
			public boolean match(Data candidate) {
				return candidate.id>=id;
			}
		},3);
		assertNQResult(new Predicate() {
			private String name="Bb";
			
			public boolean match(Data candidate) {
				return candidate.getName().equals(name);
			}
		},1);
		final int id=2;
		final String name="Aa";
		assertNQResult(new Predicate() {
			public boolean match(Data candidate) {
				return candidate.getName().equals(name)||candidate.getId()<=id;
			}
		},2);
	}	
	
	public void testArithmeticExpression() {
		// HERE
		assertNQResult(new Predicate() {
			private int id=2;
			
			public boolean match(Data candidate) {
				return candidate.id>=id+1;
			}
		},2);
		assertNQResult(new Predicate() {
			private int factor=2;
			
			private int calc() {
				return factor+1;
			}
			
			public boolean match(Data candidate) {
				return candidate.id>=calc();
			}
		},2);
	}

	private void assertNQResult(final Predicate filter,int expectedSize) {
		ObjectContainer db=Test.objectContainer();
		Db4oQueryExecutionListener listener = new Db4oQueryExecutionListener() {
			private boolean firstRun=true;
			
			public void notifyQueryExecuted(Predicate actualPredicate, String msg) {
				Test.ensureEquals(actualPredicate,filter);
				Test.ensureEquals((firstRun ? YapStream.UNOPTIMIZED : YapStream.DYNOPTIMIZED),msg);
				firstRun=false;
			}
		};
		((YapStream)db).addListener(listener);
		clearProperty(YapStream.PROPERTY_DYNAMICNQ);
		Collection raw=db.query(filter);
		System.setProperty(YapStream.PROPERTY_DYNAMICNQ,"true");
		Collection optimized=db.query(filter);
		Test.ensure(raw.equals(optimized));
		Test.ensureEquals(expectedSize,raw.size());

		try {
			Db4oEnhancingClassloader loader=new Db4oEnhancingClassloader(getClass().getClassLoader());
			Class parentClass=loader.loadClass(getClass().getName());
			Class filterClass=loader.loadClass(filter.getClass().getName());
			Constructor constr=filterClass.getDeclaredConstructor(new Class[]{parentClass});
			System.out.println(constr);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		((YapStream)db).clearListeners();
	}

	private void clearProperty(String property) {
		Properties properties = System.getProperties();
		properties.remove(property);
		System.setProperties(properties);
	}
}
