package com.db4o.test.jdk5;

import java.util.*;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.*;

public class Jdk5EnumTest {
	private final static int NUMRUNS=10;
    
    public void testSingleStoreRetrieve() {     	
        ObjectContainer db = reopen();
        
        Jdk5Data<String> data=new Jdk5Data<String>("Test",Jdk5Enum.A);
        Jdk5Enum.A.reset();
        Test.ensure(Jdk5Enum.A.getCount() == 0);
        Jdk5Enum.A.incCount();
        Test.ensure(Jdk5Enum.A.getCount() == 1);
        Test.ensure(Jdk5Enum.B.getCount() == 0);
        Test.ensure(data.getType() == Jdk5Enum.A);
        Test.ensure(data.getSize() == 0);
        Test.ensure(data.getMax() == Integer.MIN_VALUE);
        data.add(2,4,6,1,3,5);        
        Test.ensure(data.getSize() == 6);
        Test.ensure(data.getMax() == 6);
        Test.ensure(Jdk5Data.class.isAnnotationPresent(Jdk5Annotation.class));
        
        db.set(data);
        db = reopen();        
        data=null;
        
        Query query=db.query();
        query.constrain(Jdk5Data.class);
        Query sub=query.descend("type");
        sub.constrain(Jdk5Enum.class);
        sub.constrain(Jdk5Enum.A);
        sub.descend("type").constrain("A");
        sub.descend("count").constrain(Integer.valueOf(1));

        ObjectSet result=query.execute();
        Test.ensure(result.size() == 1);
        data=(Jdk5Data<String>)result.next();
        Test.ensure(data.getItem().equals("Test"));
        Test.ensure(Jdk5Enum.A == data.getType());
        Test.ensure(Jdk5Enum.A.name().equals(data.getType().name()));        
        Test.ensure(data.getSize() == 6);
        Test.ensure(data.getMax() == 6);
        Test.ensure(result.size() == 1);

        ensureEnumInstancesInDB(db);
        
        Test.deleteAllInstances(data);
    }

    public void testMultipleStoreRetrieve() {        
    	ObjectContainer db=reopen();
    	for(int i=0;i<NUMRUNS;i++) {
    		Jdk5Data<Integer> data=new Jdk5Data<Integer>(Integer.valueOf(i),nthEnum(i));    		
    		db.set(data);
    	}

    	db=reopen();
    	ObjectSet result=db.get(Jdk5Data.class);
    	Test.ensure(result.size()==NUMRUNS);
    	Comparator<Jdk5Data<Integer>> comp=new Comparator<Jdk5Data<Integer>>() {
			public int compare(Jdk5Data<Integer> d1, Jdk5Data<Integer> d2) {
				return d1.getItem().compareTo(d2.getItem());
			}
    	};
    	SortedSet<Jdk5Data<Integer>> sorted=new TreeSet<Jdk5Data<Integer>>(comp);
    	while(result.hasNext()) {
    		sorted.add((Jdk5Data<Integer>)result.next());
    	}
    	int count=0;
    	for(Jdk5Data<Integer> data : sorted) {
    		Test.ensure(data.getItem().intValue()==count);
    		Test.ensure(data.getType()==nthEnum(count));
    		count++;
    	}    	
    	ensureEnumInstancesInDB(db);

        Test.deleteAllInstances(Jdk5Data.class);
    }    
    
    public void testEnumsInCollections() {
    	final boolean withDb4oCollections=false;

    	ObjectContainer db=reopen();

    	class CollectionHolder {
    		public List<Jdk5Enum> list; 
    		public List<Jdk5Enum> db4olist;
    		public Set<Jdk5Enum> set; 
    		public Map<Jdk5Enum,String> keymap; 
    		public Map<String,Jdk5Enum> valmap; 
    		public Map<Jdk5Enum,String> db4okeymap; 
    		public Map<String,Jdk5Enum> db4ovalmap; 
    		public Jdk5Enum[] array; 
    	}

    	CollectionHolder holder=new CollectionHolder();
    	holder.list=new ArrayList<Jdk5Enum>(NUMRUNS);
    	Comparator<Jdk5Enum> comp=new Comparator<Jdk5Enum>() {
			public int compare(Jdk5Enum e1, Jdk5Enum e2) {
				return e1.name().compareTo(e2.name());
			}    		
    	};
    	holder.set=new TreeSet<Jdk5Enum>(comp);
    	holder.keymap=new HashMap<Jdk5Enum,String>(NUMRUNS);
    	holder.valmap=new HashMap<String,Jdk5Enum>(NUMRUNS);
    	holder.array=new Jdk5Enum[NUMRUNS];
    	holder.db4olist=db.ext().collections().newLinkedList();
    	holder.db4okeymap=db.ext().collections().newHashMap(2);
    	holder.db4ovalmap=db.ext().collections().newHashMap(2);
    	for(int i=0;i<NUMRUNS;i++) {
    		Jdk5Enum curenum=nthEnum(i);
			holder.list.add(curenum);
    		if(withDb4oCollections) {
        		holder.db4olist.add(curenum);
    		}
    		holder.array[i]=curenum;
    	}
		holder.set.add(Jdk5Enum.A);
		holder.set.add(Jdk5Enum.B);
		holder.keymap.put(Jdk5Enum.A,Jdk5Enum.A.name());
		holder.keymap.put(Jdk5Enum.B,Jdk5Enum.B.name());
		holder.valmap.put(Jdk5Enum.A.name(),Jdk5Enum.A);
		holder.valmap.put(Jdk5Enum.B.name(),Jdk5Enum.B);	
		if(withDb4oCollections) {
			holder.db4okeymap.put(Jdk5Enum.A,Jdk5Enum.A.name());
			holder.db4okeymap.put(Jdk5Enum.B,Jdk5Enum.B.name());
			holder.db4ovalmap.put(Jdk5Enum.A.name(),Jdk5Enum.A);
			holder.db4ovalmap.put(Jdk5Enum.B.name(),Jdk5Enum.B);
		}
    	db.set(holder);
    	
    	db=reopen();
    	ObjectSet result=db.get(CollectionHolder.class);
    	Test.ensure(result.size()==1);
    	holder=(CollectionHolder)result.next();

    	Test.ensure(holder.list.size()==NUMRUNS);
    	Test.ensure(holder.set.size()==2);
    	Test.ensure(holder.keymap.size()==2);
    	Test.ensure(holder.valmap.size()==2);
    	Test.ensure(holder.array.length==NUMRUNS);
    	if(withDb4oCollections) {
    		Test.ensure(holder.db4olist.size()==NUMRUNS);
        	Test.ensure(holder.db4okeymap.size()==2);
        	Test.ensure(holder.db4ovalmap.size()==2);
    	}
    	ensureEnumInstancesInDB(db);
    	
    	Test.deleteAllInstances(CollectionHolder.class);
    }
    
    private ObjectContainer reopen() {
    	Test.reOpen();
    	return Test.objectContainer();
    }
    
	private void ensureEnumInstancesInDB(ObjectContainer db) {
		Query query;
		ObjectSet result;
		query=db.query();
		query.constrain(Jdk5Enum.class);
		result=query.execute();
		// We should have all enum members once in the database, since they're
        // statically referenced by the Enum subclass.
		if(result.size()!=2) {
			System.err.println("# instances in db: "+result.size());
			while(result.hasNext()) {
				Jdk5Enum curenum=(Jdk5Enum)result.next();
				System.err.println(curenum+" : "+System.identityHashCode(curenum));
			}
			
		}
        Test.ensure(result.size()==2);
	}
	
	private Jdk5Enum nthEnum(int n) {
		return (n%2==0 ? Jdk5Enum.A : Jdk5Enum.B);
	}
}
