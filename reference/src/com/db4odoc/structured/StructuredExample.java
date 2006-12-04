package com.db4odoc.structured;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;


public class StructuredExample  {
	public final static String YAPFILENAME="formula1.yap";
    
	public static void main(String[] args) {
        new File(YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(YAPFILENAME);
        try {
            storeFirstCar(db);
            storeSecondCar(db);
            retrieveAllCarsQBE(db);
            retrieveAllPilotsQBE(db);
            retrieveCarByPilotQBE(db);
            retrieveCarByPilotNameQuery(db);
            retrieveCarByPilotProtoQuery(db);
            retrievePilotByCarModelQuery(db);
            updateCar(db);
            updatePilotSingleSession(db);
            updatePilotSeparateSessionsPart1(db);
            db.close();
            db=Db4o.openFile(YAPFILENAME);
            updatePilotSeparateSessionsPart2(db);
            db.close();
            updatePilotSeparateSessionsImprovedPart1();
            db=Db4o.openFile(YAPFILENAME);
            updatePilotSeparateSessionsImprovedPart2(db);
            db.close();
            db=Db4o.openFile(YAPFILENAME);
            updatePilotSeparateSessionsImprovedPart3(db);
            deleteFlat(db);
            db.close();
            deleteDeepPart1();
            db=Db4o.openFile(YAPFILENAME);
            deleteDeepPart2(db);
            deleteDeepRevisited(db);
        }
        finally {
            db.close();
        }
    }
	// end main
    
    public static void storeFirstCar(ObjectContainer db) {
        Car car1=new Car("Ferrari");
        Pilot pilot1=new Pilot("Michael Schumacher",100);
        car1.setPilot(pilot1);
        db.set(car1);
    }
    // end storeFirstCar

    public static void storeSecondCar(ObjectContainer db) {
        Pilot pilot2=new Pilot("Rubens Barrichello",99);
        db.set(pilot2);
        Car car2=new Car("BMW");
        car2.setPilot(pilot2);
        db.set(car2);
    }
    // end storeSecondCar

    public static void retrieveAllCarsQBE(ObjectContainer db) {
        Car proto=new Car(null);
        ObjectSet result=db.get(proto);
        listResult(result);
    }
    // end retrieveAllCarsQBE

    public static void retrieveAllPilotsQBE(ObjectContainer db) {
        Pilot proto=new Pilot(null,0);
        ObjectSet result=db.get(proto);
        listResult(result);
    }
    // end retrieveAllPilotsQBE

    public static void retrieveAllPilots(ObjectContainer db) {
        ObjectSet result=db.get(Pilot.class);
        listResult(result);
    }
    // end retrieveAllPilots

    public static void retrieveCarByPilotQBE(
            ObjectContainer db) {
        Pilot pilotproto=new Pilot("Rubens Barrichello",0);
        Car carproto=new Car(null);
        carproto.setPilot(pilotproto);
        ObjectSet result=db.get(carproto);
        listResult(result);
    }
    // end retrieveCarByPilotQBE
    
    public static void retrieveCarByPilotNameQuery(
            ObjectContainer db) {
        Query query=db.query();
        query.constrain(Car.class);
        query.descend("pilot").descend("name")
                .constrain("Rubens Barrichello");
        ObjectSet result=query.execute();
        listResult(result);
    }
    // end retrieveCarByPilotNameQuery

    public static void retrieveCarByPilotProtoQuery(
                ObjectContainer db) {
        Query query=db.query();
        query.constrain(Car.class);
        Pilot proto=new Pilot("Rubens Barrichello",0);
        query.descend("pilot").constrain(proto);
        ObjectSet result=query.execute();
        listResult(result);
    }
    // end retrieveCarByPilotProtoQuery
   
    public static void retrievePilotByCarModelQuery(ObjectContainer db) {
        Query carquery=db.query();
        carquery.constrain(Car.class);
        carquery.descend("model").constrain("Ferrari");
        Query pilotquery=carquery.descend("pilot");
        ObjectSet result=pilotquery.execute();
        listResult(result);
    }
    // end retrievePilotByCarModelQuery
    
    public static void retrieveAllPilotsNative(ObjectContainer db) {
    	ObjectSet results = db.query(new Predicate<Pilot>() {
    		public boolean match(Pilot pilot){
    			return true;
    		}
    	});
    	listResult(results);
    }
    // end retrieveAllPilotsNative
    
    public static void retrieveAllCars(ObjectContainer db) {
    	ObjectSet results = db.get(Car.class);
    	listResult(results);
    }
    // end retrieveAllCars
    
    public static void retrieveCarsByPilotNameNative(ObjectContainer db) {
    	final String pilotName = "Rubens Barrichello";
    	ObjectSet results = db.query(new Predicate<Car>() {
    		public boolean match(Car car){
    			return car.getPilot().getName().equals(pilotName);
    		}
    	});
    	listResult(results);
    }
    // end retrieveCarsByPilotNameNative
    
    public static void updateCar(ObjectContainer db) {
        ObjectSet result=db.query(new Predicate<Car>() {
        	public boolean match(Car car){
        		return car.getModel().equals("Ferrari");
        	}
        });
        Car found=(Car)result.next();
        found.setPilot(new Pilot("Somebody else",0));
        db.set(found);
        result=db.query(new Predicate<Car>() {
        	public boolean match(Car car){
        		return car.getModel().equals("Ferrari");
        	}
        });
        listResult(result);
    }
    // end updateCar
    
    public static void updatePilotSingleSession(
                ObjectContainer db) {
        ObjectSet result=db.query(new Predicate<Car>() {
        	public boolean match(Car car){
        		return car.getModel().equals("Ferrari");
        	}
        });
        Car found=(Car)result.next();
        found.getPilot().addPoints(1);
        db.set(found);
        result=db.query(new Predicate<Car>() {
        	public boolean match(Car car){
        		return car.getModel().equals("Ferrari");
        	}
        });
        listResult(result);
    }
    // end updatePilotSingleSession

    public static void updatePilotSeparateSessionsPart1(
    		ObjectContainer db) {
        ObjectSet result=db.query(new Predicate<Car>() {
        	public boolean match(Car car){
        		return car.getModel().equals("Ferrari");
        	}
        });
        Car found=(Car)result.next();
        found.getPilot().addPoints(1);
        db.set(found);
    }
    // end updatePilotSeparateSessionsPart1

    public static void updatePilotSeparateSessionsPart2(
                ObjectContainer db) {
        ObjectSet result=db.query(new Predicate<Car>() {
        	public boolean match(Car car){
        		return car.getModel().equals("Ferrari");
        	}
        });
        listResult(result);
    }
    // end updatePilotSeparateSessionsPart2

    public static void updatePilotSeparateSessionsImprovedPart1() {
        Db4o.configure().objectClass("com.db4o.f1.chapter2.Car")
                .cascadeOnUpdate(true);        
    }
    // end updatePilotSeparateSessionsImprovedPart1

    public static void updatePilotSeparateSessionsImprovedPart2(
                ObjectContainer db) {
        ObjectSet result=db.query(new Predicate<Car>() {
        	public boolean match(Car car){
        		return car.getModel().equals("Ferrari");
        	}
        });
        Car found=(Car)result.next();
        found.getPilot().addPoints(1);
        db.set(found);
    }
    // end updatePilotSeparateSessionsImprovedPart2

    public static void updatePilotSeparateSessionsImprovedPart3(
                ObjectContainer db) {
        ObjectSet result=db.query(new Predicate<Car>() {
        	public boolean match(Car car){
        		return car.getModel().equals("Ferrari");
        	}
        });
        listResult(result);
    }
    // end updatePilotSeparateSessionsImprovedPart3

    public static void deleteFlat(ObjectContainer db) {
        ObjectSet result=db.query(new Predicate<Car>() {
        	public boolean match(Car car){
        		return car.getModel().equals("Ferrari");
        	}
        });
        Car found=(Car)result.next();
        db.delete(found);
        result=db.get(new Car(null));
        listResult(result);
    }
    // end deleteFlat
    
    public static void deleteDeepPart1() {
        Db4o.configure().objectClass("com.db4o.f1.chapter2.Car")
                .cascadeOnDelete(true);
    }
    // end deleteDeepPart1

    public static void deleteDeepPart2(ObjectContainer db) {
        ObjectSet result=db.query(new Predicate<Car>() {
        	public boolean match(Car car){
        		return car.getModel().equals("BMW");
        	}
        });
        Car found=(Car)result.next();
        db.delete(found);
        result=db.query(new Predicate<Car>() {
        	public boolean match(Car car){
        		return true;
        	}
        });
        listResult(result);
    }
    // end deleteDeepPart2

    public static void deleteDeepRevisited(ObjectContainer db) {
        ObjectSet result=db.query(new Predicate<Pilot>() {
        	public boolean match(Pilot pilot){
        		return pilot.getName().equals("Michael Schumacher");
        	}
        });
        Pilot pilot=(Pilot)result.next();
        Car car1=new Car("Ferrari");
        Car car2=new Car("BMW");
        car1.setPilot(pilot);
        car2.setPilot(pilot);
        db.set(car1);
        db.set(car2);
        db.delete(car2);
        result=db.query(new Predicate<Car>() {
        	public boolean match(Car car){
        		return true;
        	}
        });
        listResult(result);
    }
    // end deleteDeepRevisited
    
    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
