namespace com.db4o.f1.chapter2
{
    using System;
    using System.IO;
    using com.db4o;
    using com.db4o.f1;
    using com.db4o.query;
    
    public class StructuredExample : Util
    {
        public static void Main(String[] args)
        {
            File.Delete(Util.YapFileName);
            
            ObjectContainer db = Db4o.openFile(Util.YapFileName);
            try
            {
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
                db=Db4o.openFile(Util.YapFileName);
                updatePilotSeparateSessionsPart2(db);
                db.close();
                updatePilotSeparateSessionsImprovedPart1(db);
                db=Db4o.openFile(Util.YapFileName);
                updatePilotSeparateSessionsImprovedPart2(db);
                db.close();
                db=Db4o.openFile(Util.YapFileName);
                updatePilotSeparateSessionsImprovedPart3(db);
                deleteFlat(db);
                db.close();
                deleteDeepPart1(db);
                db=Db4o.openFile(Util.YapFileName);
                deleteDeepPart2(db);
                deleteDeepRevisited(db);
            }
            finally
            {
                db.close();
            }
        }
        
        public static void storeFirstCar(ObjectContainer db)
        {
            Car car1 = new Car("Ferrari");
            Pilot pilot1 = new Pilot("Michael Schumacher", 100);
            car1.Pilot = pilot1;
            db.set(car1);
        }
        
        public static void storeSecondCar(ObjectContainer db)
        {
            Pilot pilot2 = new Pilot("Rubens Barrichello", 99);
            db.set(pilot2);
            Car car2 = new Car("BMW");
            car2.Pilot = pilot2;
            db.set(car2);
        }

        public static void retrieveAllCarsQBE(ObjectContainer db)
        {
            Car proto = new Car(null);
            ObjectSet result = db.get(proto);
            listResult(result);
        }
        
        public static void retrieveAllPilotsQBE(ObjectContainer db)
        {
            Pilot proto = new Pilot(null, 0);
            ObjectSet result = db.get(proto);
            listResult(result);
        }
        
        public static void retrieveCarByPilotQBE(ObjectContainer db)
        {
            Pilot pilotproto = new Pilot("Rubens Barrichello",0);
            Car carproto = new Car(null);
            carproto.Pilot = pilotproto;
            ObjectSet result = db.get(carproto);
            listResult(result);
        }
        
        public static void retrieveCarByPilotNameQuery(ObjectContainer db)
        {
            Query query = db.query();
            query.constrain(typeof(Car));
            query.descend("_pilot").descend("_name")
                .constrain("Rubens Barrichello");
            ObjectSet result = query.execute();
            listResult(result);
        }
        
        public static void retrieveCarByPilotProtoQuery(ObjectContainer db)
        {
            Query query = db.query();
            query.constrain(typeof(Car));
            Pilot proto = new Pilot("Rubens Barrichello", 0);
            query.descend("_pilot").constrain(proto);
            ObjectSet result = query.execute();
            listResult(result);
        }
        
        public static void retrievePilotByCarModelQuery(ObjectContainer db) 
        {
	        Query carquery=db.query();
	        carquery.constrain(typeof(Car));
	        carquery.descend("_model").constrain("Ferrari");
	        Query pilotquery=carquery.descend("_pilot");
	        ObjectSet result=pilotquery.execute();
	        listResult(result);
        }
        
        public static void updateCar(ObjectContainer db)
        {
            ObjectSet result = db.get(new Car("Ferrari"));
            Car found = (Car)result.next();
            found.Pilot = new Pilot("Somebody else", 0);
            db.set(found);
            result = db.get(new Car("Ferrari"));
            listResult(result);
        }
        
        public static void updatePilotSingleSession(ObjectContainer db)
        {
            ObjectSet result = db.get(new Car("Ferrari"));
            Car found = (Car)result.next();
            found.Pilot.AddPoints(1);
            db.set(found);
            result = db.get(new Car("Ferrari"));
            listResult(result);
        }
        
        public static void updatePilotSeparateSessionsPart1(ObjectContainer db)
        {
            ObjectSet result = db.get(new Car("Ferrari"));
            Car found = (Car)result.next();
            found.Pilot.AddPoints(1);
            db.set(found);
        }
        
        public static void updatePilotSeparateSessionsPart2(ObjectContainer db)
        {
            ObjectSet result = db.get(new Car("Ferrari"));
            listResult(result);
        }
        
        public static void updatePilotSeparateSessionsImprovedPart1(ObjectContainer db)
        {
            Db4o.configure().objectClass(typeof(Car))
                .cascadeOnUpdate(true);        
        }
        
        public static void updatePilotSeparateSessionsImprovedPart2(ObjectContainer db)
        {
            ObjectSet result = db.get(new Car("Ferrari"));
            Car found = (Car)result.next();
            found.Pilot.AddPoints(1);
            db.set(found);
        }
        
        public static void updatePilotSeparateSessionsImprovedPart3(ObjectContainer db)
        {
            ObjectSet result = db.get(new Car("Ferrari"));
            listResult(result);
        }
        
        public static void deleteFlat(ObjectContainer db)
        {
            ObjectSet result = db.get(new Car("Ferrari"));
            Car found = (Car)result.next();
            db.delete(found);
            result = db.get(new Car(null));
            listResult(result);
        }
        
        public static void deleteDeepPart1(ObjectContainer db)
        {
            Db4o.configure().objectClass(typeof(Car))
                .cascadeOnDelete(true);
        }
        
        public static void deleteDeepPart2(ObjectContainer db)
        {
            ObjectSet result = db.get(new Car("BMW"));
            Car found = (Car)result.next();
            db.delete(found);
            result = db.get(new Car(null));
            listResult(result);
        }
        
        public static void deleteDeepRevisited(ObjectContainer db)
        {
            ObjectSet result = db.get(new Pilot("Michael Schumacher", 0));
            Pilot pilot = (Pilot)result.next();
            Car car1 = new Car("Ferrari");
            Car car2 = new Car("BMW");
            car1.Pilot = pilot;
            car2.Pilot = pilot;
            db.set(car1);
            db.set(car2);
            db.delete(car2);
            result = db.get(new Car(null));
            listResult(result);
        }
        
        public static void deleteAll(ObjectContainer db) {
	        ObjectSet cars=db.get(new Car(null));
	        while(cars.hasNext()) {
	            db.delete(cars.next());
	        }
	        ObjectSet pilots=db.get(new Pilot(null,0));
	        while(pilots.hasNext()) {
	            db.delete(pilots.next());
	        }
	    }        
    }    
}
