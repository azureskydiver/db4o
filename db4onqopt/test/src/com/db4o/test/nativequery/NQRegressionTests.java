/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.nativequery;

import java.lang.reflect.*;

import com.db4o.*;
import com.db4o.inside.query.*;
import com.db4o.nativequery.*;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.main.*;
import com.db4o.query.*;
import com.db4o.test.*;


public class NQRegressionTests {
	private final static boolean RUN_LOADTIME=false;
	
	private static final String CSTR = "Cc";
	private static final String BSTR = "Ba";
	private static final String ASTR = "Aa";
	public final static Integer INTWRAPPER=new Integer(1);
	private final static Integer PRIVATE_INTWRAPPER=new Integer(1);
	
	private static abstract class Base {
		int id;
		Integer idWrap;
		
		public Base(int id) {
			this.id=id;
			idWrap=new Integer(id);
		}

		public int getId() {
			return id;
		}
	}
	
	private static class Other extends Base {
		public Other() {
			super(1);
		}
	}
	
	private static class Data extends Base {
		boolean bool;
		float value;
		String name;
		Data prev;
		int id2;
		Boolean boolWrap;
		
		public Data(int id, boolean bool,float value, String name,Data prev, int id2) {
			super(id);
			this.bool=bool;
			this.boolWrap=Boolean.valueOf(bool);
			this.value=value;
			this.name = name;
			this.prev=prev;
			this.id2=id2;
		}

		public float getValue() {
			return value;
		}

		public String getName() {
			return name;
		}
		
		public boolean getBool() {
			return bool;
		}

		public Data getPrev() {
			return prev;
		}	
	}

	public static void main(String[] args) {
		Test.run(NQRegressionTests.class);
	}

	public void store() {
		Data a=new Data(1,false,1.1f,ASTR,null, 0);
		Data b=new Data(2,false,1.1f,BSTR,a, Integer.MIN_VALUE);
		Data c=new Data(3,true,2.2f,CSTR,b, Integer.MIN_VALUE);
		Data cc=new Data(3,false,3.3f,CSTR,null, Integer.MIN_VALUE);
		Test.store(a);
		Test.store(b);
		Test.store(c);
		Test.store(cc);
		Test.store(new Other());
	}
	
	private abstract static class ExpectingPredicate extends Predicate {
		private String _name;
		
		public ExpectingPredicate(String name) {
			_name=name;
		}

		public ExpectingPredicate(String name,Class extentType) {
			super(extentType);
			_name=name;
		}

		public abstract int expected();
		
		public String toString() {
			return _name;
		}
	}
	
	private static ExpectingPredicate[] _PREDICATES={
		// unconditional/untyped
		new ExpectingPredicate("unconditional/untyped") {
			public int expected() { return 5;}
			public boolean match(Object candidate) {
				return true;
			}
		},
		// unconditional
		new ExpectingPredicate("unconditional: Base") {
			public int expected() { return 5;}
			public boolean match(Base candidate) {
				return true;
			}
		},
		new ExpectingPredicate("unconditional: Data") {
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
		new ExpectingPredicate("bool") {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.bool;
			}
		},
		new ExpectingPredicate("!bool") {
			public int expected() { return 3;}
			public boolean match(Data candidate) {
				return !candidate.bool;
			}
		},
		new ExpectingPredicate("id2==0") {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.id2==0;
			}
		},
		new ExpectingPredicate("id==1") {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.id==1;
			}
		},
		new ExpectingPredicate("id==3") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.id==3;
			}
		},
		new ExpectingPredicate("value==1.1") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.value==1.1f;
			}
		},
		new ExpectingPredicate("value==3.3") {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.value==3.3f;
			}
		},
		// string equals
		new ExpectingPredicate("name.eq(ASTR)") {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.name.equals(ASTR);
			}
		},
		new ExpectingPredicate("name.eq(CSTR)") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.name.equals(CSTR);
			}
		},
		// string specific comparisons
		new ExpectingPredicate("name.contains('a')") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.name.contains("a");
			}
		},
		new ExpectingPredicate("name.contains('A')") {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.name.contains("A");
			}
		},
		new ExpectingPredicate("name.contains('C')") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.name.contains("C");
			}
		},
		new ExpectingPredicate("name.startsWith('C')") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.name.startsWith("C");
			}
		},
		new ExpectingPredicate("name.startsWith('a')") {
			public int expected() { return 0;}
			public boolean match(Data candidate) {
				return candidate.name.startsWith("a");
			}
		},
		new ExpectingPredicate("name.endsWith('A')") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.name.endsWith("a");
			}
		},
		new ExpectingPredicate("name.endsWith('A')") {
			public int expected() { return 0;}
			public boolean match(Data candidate) {
				return candidate.name.endsWith("A");
			}
		},
		new ExpectingPredicate("!(name.contains('A'))") {
			public int expected() { return 3;}
			public boolean match(Data candidate) {
				return !candidate.name.contains("A");
			}
		},
		new ExpectingPredicate("!(name.startsWith('C'))") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return !candidate.name.startsWith("C");
			}
		},
		// int field comparison
		new ExpectingPredicate("id<2") {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.id<2;
			}
		},
		new ExpectingPredicate("id>2") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.id>2;
			}
		},
		new ExpectingPredicate("id<=2") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.id<=2;
			}
		},
		new ExpectingPredicate("id>=2") {
			public int expected() { return 3;}
			public boolean match(Data candidate) {
				return candidate.id>=2;
			}
		},
		// float field comparison
		new ExpectingPredicate("value>2.9") {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.value>2.9f;
			}
		},
		new ExpectingPredicate("1.5>=value") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return 1.5f >= candidate.value;
			}
		},
		// mixed comparison (coercion)
		new ExpectingPredicate("id==1.0") {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.id==1.0f;
			}
		},
		new ExpectingPredicate("id!=1.0") {
			public int expected() { return 3;}
			public boolean match(Data candidate) {
				return candidate.id!=1.0f;
			}
		},
		new ExpectingPredicate("value!=1") {
			public int expected() { return 4;}
			public boolean match(Data candidate) {
				return candidate.value!=1;
			}
		},
// won't work: SODA coercion is broken for greater/smaller comparisons
//		new ExpectingPredicate() {
//			public int expected() { return 1;}
//			public boolean match(Data candidate) {
//				return candidate.value>2.9d;
//			}
//		},
		// descend field
		new ExpectingPredicate("getPrev().getId()>=1") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.getPrev()!=null&&candidate.getPrev().getId()>=1;
			}
		},
		new ExpectingPredicate("BSTR.eq(getPrev().getName()") {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return (candidate.getPrev()!=null)&&(BSTR.equals(candidate.getPrev().getName()));
			}
		},
		new ExpectingPredicate("getPrev().getName().eq('')") {
			public int expected() { return 0;}
			public boolean match(Data candidate) {
				return candidate.getPrev()!=null&&candidate.getPrev().getName().equals("");
			}
		},
		// getter comparisons
		new ExpectingPredicate("getId()==2") {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.getId()==2;
			}
		},
		new ExpectingPredicate("getId()<2") {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.getId()<2;
			}
		},
		new ExpectingPredicate("getId()>2") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.getId()>2;
			}
		},
		new ExpectingPredicate("getId()<=2") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.getId()<=2;
			}
		},
		new ExpectingPredicate("getId()>=2") {
			public int expected() { return 3;}
			public boolean match(Data candidate) {
				return candidate.getId()>=2;
			}
		},
		new ExpectingPredicate("getName().eq(CSTR)") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.getName().equals(CSTR);
			}
		},
		// negation
		new ExpectingPredicate("!(id==1)") {
			public int expected() { return 3;}
			public boolean match(Data candidate) {
				return !(candidate.id==1);
			}
		},
		new ExpectingPredicate("!(getId()>2)") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return !(candidate.getId()>2);
			}
		},
		new ExpectingPredicate("!getName().eq(CSTR)") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return !(candidate.getName().equals(CSTR));
			}
		},
		// conjunction
		new ExpectingPredicate("bool&&!getBool()") {
			public int expected() { return 0;}
			public boolean match(Data candidate) {
				return candidate.bool&&!candidate.getBool();
			}
		},
		new ExpectingPredicate("id>1&&getName().eq(CSTR)") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return (candidate.id>1)&&candidate.getName().equals(CSTR);
			}
		},
		new ExpectingPredicate("id>1&&getId()<=2") {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return (candidate.id>1)&&(candidate.getId()<=2);
			}
		},
		new ExpectingPredicate("id>1&&getId()<1") {
			public int expected() { return 0;}
			public boolean match(Data candidate) {
				return (candidate.id>1)&&(candidate.getId()<1);
			}
		},
		// disjunction
		new ExpectingPredicate("bool||getId()==1") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.bool||candidate.getId()==1;
			}
		},
		new ExpectingPredicate("id==1||getName().eq(CSTR)") {
			public int expected() { return 3;}
			public boolean match(Data candidate) {
				return (candidate.id==1)||candidate.getName().equals(CSTR);
			}
		},
		new ExpectingPredicate("id>1||getId()<=2") {
			public int expected() { return 4;}
			public boolean match(Data candidate) {
				return (candidate.id>1)||(candidate.getId()<=2);
			}
		},
		new ExpectingPredicate("id<=1||getId()>=3") {
			public int expected() { return 3;}
			public boolean match(Data candidate) {
				return (candidate.id<=1)||(candidate.getId()>=3);
			}
		},
		// nested boolean
		new ExpectingPredicate("id>=1||getName().eq(CSTR)&&getId()<3") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return ((candidate.id>=1)||candidate.getName().equals(CSTR))&&candidate.getId()<3;
			}
		},
		new ExpectingPredicate("(id==2||getId()<=1)&&!(getName().eq(BSTR))") {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return ((candidate.id==2)||candidate.getId()<=1)&&!candidate.getName().equals(BSTR);
			}
		},
		// predicate member comparison
		new ExpectingPredicate("id>=P.id") {
			private int id=2;
			
			public int expected() { return 3;}
			public boolean match(Data candidate) {
				return candidate.id>=id;
			}
		},
		new ExpectingPredicate("getName().eq(P.name)") {
			private String name=BSTR;
			
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.getName().equals(name);
			}
		},
		// arithmetic
		new ExpectingPredicate("id>=P.id+1") {
			private int id=2;
			
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.id>=id+1;
			}
		},
		new ExpectingPredicate("id>=P.calc()") {
			private int factor=2;
			
			private int calc() {
				return factor+1;
			}
			
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.id>=calc();
			}
		},
		new ExpectingPredicate("getValue()==P.calc()") {
			private float predFactor=2.0f;
			
			private float calc() {
				return predFactor*1.1f;
			}
			
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.getValue()==calc();
			}
		},
		// force extent
		new ExpectingPredicate("force extent",Data.class) {
			public int expected() { return 1;}
			public boolean match(Object candidate) {
				return ((Data)candidate).getId()==1;
			}
		},
		// array access
		new ExpectingPredicate("id==P.data[3]") {
			private int[] data={0,1,2,3,4};
			
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.id==data[3];
			}
		},
		new ExpectingPredicate("prev==P.data[3]") {
			private Data[] data={null,null,null,null};
			
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.prev==data[3];
			}
		},
		// non-candidate method calls
		new ExpectingPredicate("id==Integer.parseInt('3')") {
			public int expected() { return 2;}
			public boolean match(Data candidate) {
				return candidate.id==Integer.parseInt("3");
			}
		},
		new ExpectingPredicate("id==P.sum(3,0)") {
			public int expected() { return 2;}
			private int sum(int a,int b) {
				return a+b;
			}
			public boolean match(Data candidate) {
				return candidate.id==sum(3,0);
			}
		},
		// primitive wrappers
		new ExpectingPredicate("boolWrapper") {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.boolWrap.booleanValue();
			}
		},
		new ExpectingPredicate("INTWRAPPER.eq(iwrap)") {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return NQRegressionTests.INTWRAPPER.equals(candidate.idWrap);
			}
		},
		new ExpectingPredicate("iwrap.value==1") {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return candidate.idWrap.intValue()==1;
			}
		},
		// Note: We never get to see a static field access here - non-static inner class
		// stuff converts this to NQRegressionTests#access$0()
		new ExpectingPredicate("PRIVATE_INTWRAPPER.eq(idWrap)") {
			public int expected() { return 1;}
			public boolean match(Data candidate) {
				return NQRegressionTests.PRIVATE_INTWRAPPER.equals(candidate.idWrap);
			}
		},
	};
	
	private static ExpectingPredicate[] PREDICATES=_PREDICATES;
	
	public void testAll() {
		for (int predIdx = 0; predIdx < PREDICATES.length; predIdx++) {
			ExpectingPredicate predicate = PREDICATES[predIdx];
			assertNQResult(predicate);
		}
	}
	
	private void assertNQResult(final ExpectingPredicate predicate) {
		final String predicateId = "PREDICATE: "+predicate;
		ObjectContainer db=Test.objectContainer();
		Db4oQueryExecutionListener listener = new Db4oQueryExecutionListener() {
			private int run=0;
			
			public void notifyQueryExecuted(NQOptimizationInfo info) {
				if(run<2) {
					Test.ensureEquals(info.predicate(),predicate,predicateId);
				}
				String expMsg=null;
				switch(run) {
					case 0:
						expMsg=NativeQueryHandler.UNOPTIMIZED;
						Test.ensure(info.optimized()==null,predicateId);
						break;
					case 1:
						expMsg=NativeQueryHandler.DYNOPTIMIZED;
						Test.ensure(info.optimized() instanceof Expression,predicateId);
						break;
					case 2:
						expMsg=NativeQueryHandler.PREOPTIMIZED;
						Test.ensure(info.optimized()==null,predicateId);
						break;
				}
				Test.ensureEquals(expMsg,info.message(),predicateId);
				run++;
			}
		};
		((YapStream)db).getNativeQueryHandler().addListener(listener);
		db.ext().configure().optimizeNativeQueries(false);
		ObjectSet raw=db.query(predicate);
		db.ext().configure().optimizeNativeQueries(true);
		if(NQDebug.LOG) {
			System.err.println("PREDICATE: "+predicate);
		}
		ObjectSet optimized=db.query(predicate);
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
		Test.ensure(raw.equals(optimized),predicateId);
		Test.ensureEquals(predicate.expected(),raw.size(),predicateId);

		if(RUN_LOADTIME) {
			db.ext().configure().optimizeNativeQueries(false);
			try {
				Db4oEnhancingClassloader loader=new Db4oEnhancingClassloader(getClass().getClassLoader());
				Class filterClass=loader.loadClass(predicate.getClass().getName());
				Constructor constr=null;
				Object[] args=null;
				try {
					constr=filterClass.getDeclaredConstructor(new Class[]{String.class});
					args=new Object[]{filterClass.getName()};
				}
				catch(NoSuchMethodException exc) {
					constr=filterClass.getDeclaredConstructor(new Class[]{String.class,Class.class});
					args=new Object[]{filterClass.getName(),Data.class};
				}
				constr.setAccessible(true);
				Predicate clPredicate=(Predicate)constr.newInstance(args);
				ObjectSet preoptimized=db.query(clPredicate);
				Test.ensureEquals(predicate.expected(),preoptimized.size(),predicateId);
				Test.ensure(raw.equals(preoptimized),predicateId);
				Test.ensure(optimized.equals(preoptimized),predicateId);
			} 
			catch (Throwable exc) {
				exc.printStackTrace();
			}
		}
		((YapStream)db).getNativeQueryHandler().clearListeners();
		db.ext().configure().optimizeNativeQueries(true);
	}
}
