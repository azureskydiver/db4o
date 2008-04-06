package com.db4odoc.typehandler;

import java.io.File;
import java.io.IOException;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.defragment.Defragment;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.generic.GenericReflector;
import com.db4o.reflect.jdk.JdkReflector;
import com.db4o.typehandlers.TypeHandlerPredicate;

public class TypehandlerExample {

	private final static String DB4O_FILE_NAME = "reference.db4o";
	private static ObjectContainer _container = null;


	public static void main(String[] args) throws IOException {
		testReadWriteDelete();
		//testDefrag();
		testCompare();
	}
	// end main

	private static Configuration configure() {
		Configuration configuration = Db4o.newConfiguration();
		// add a custom typehandler support
        
        TypeHandlerPredicate predicate = new TypeHandlerPredicate() {
            public boolean match(ReflectClass classReflector, int version) {
            	GenericReflector reflector = new GenericReflector(
            			null, new JdkReflector(Thread.currentThread().getContextClassLoader()));
    			ReflectClass claxx = reflector.forName(StringBuffer.class.getName()); 
    			boolean res = claxx.equals(classReflector);  
                return res;
            }
        };
        
        configuration.registerTypeHandler(predicate, new StringBufferHandler());
		return configuration;	
	}
	// end configure
	
	
	private static void testReadWriteDelete(){
		storeCar();
		// Does it still work after close? 
		retrieveCar();
		// Does deletion work?
		deleteCar();
		retrieveCar();
	}
	// end testReadWriteDelete

	private static void retrieveCar() {
		ObjectContainer container = database(configure());
		if (container != null){
			try {
				ObjectSet result = container.query(Car.class);
				Car car = null;
				if (result.hasNext()){
					car = (Car)result.next();
				}
				System.out.println("Retrieved: " + car);
			} finally {
				closeDatabase();
			}
		}
	}
	// end retrieveCar

	private static void deleteCar() {
		ObjectContainer container = database(configure());
		if (container != null){
			try {
				ObjectSet result = container.query(Car.class);
				Car car = null;
				if (result.hasNext()){
					car = (Car)result.next();
				}
				container.delete(car);
				System.out.println("Deleted: " + car);
			} finally {
				closeDatabase();
			}
		}
	}
	// end deleteCar

	private static void storeCar() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = database(configure());
		if (container != null){
			try {
				Car car = new Car("BMW");
				container.store(car);
				car = (Car)container.query(Car.class).next();
				System.out.println("Stored: " + car);
				
			} finally {
				closeDatabase();
			}
		}
	}
	// end storeCar

	private static void testCompare() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = database(configure());
		if (container != null){
			try {
				Car car = new Car("BMW");
				container.store(car);
				car = new Car("Ferrari");
				container.store(car);
				car = new Car("Mercedes");
				container.store(car);
				Query query = container.query();
				query.constrain(Car.class);
				query.descend("model").orderAscending();
				ObjectSet result = query.execute();
				listResult(result);
				
			} finally {
				closeDatabase();
			}
		}
	}
	// end testCompare

	public static void testDefrag() throws IOException{
		new File(DB4O_FILE_NAME + ".backup").delete();
		storeCar();
		Defragment.defrag(DB4O_FILE_NAME);
		retrieveCar();
	}
	// end testDefrag
	
	private static ObjectContainer database(Configuration configuration) {
		if (_container == null) {
			try {
				_container = Db4o.openFile(configuration, DB4O_FILE_NAME);
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


	private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
	
}
