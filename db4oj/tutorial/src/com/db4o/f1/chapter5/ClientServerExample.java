package com.db4o.f1.chapter5;

import java.io.*;
import com.db4o.*;
import com.db4o.f1.*;


public class ClientServerExample extends Util {
    private final static int PORT=0xdb40;
    private final static String USER="user";
    private final static String PASSWORD="password";
    
    public static void main(String[] args) throws IOException {
        new File(Util.YAPFILENAME).delete();
        accessLocalServer();
        new File(Util.YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
        try {
            setFirstCar(db);
            setSecondCar(db);
        }
        finally {
            db.close();
        }
        configureDb4o();
        ObjectServer server=Db4o.openServer(Util.YAPFILENAME,0);
        try {
            queryLocalServer(server);
            demonstrateLocalReadCommitted(server);
            demonstrateLocalRollback(server);
        }
        finally {
            server.close();
        }
        accessRemoteServer();
        server=Db4o.openServer(Util.YAPFILENAME,PORT);
        server.grantAccess(USER,PASSWORD);
        try {
            queryRemoteServer(PORT,USER,PASSWORD);
            demonstrateRemoteReadCommitted(PORT,USER,PASSWORD);
            demonstrateRemoteRollback(PORT,USER,PASSWORD);
        }
        finally {
            server.close();
        }
    }
        
    public static void setFirstCar(ObjectContainer db) {
        Pilot pilot=new Pilot("Rubens Barrichello",99);
        Car car=new Car("BMW");
        car.setPilot(pilot);
        db.set(car);
    }

    public static void setSecondCar(ObjectContainer db) {
        Pilot pilot=new Pilot("Michael Schumacher",100);
        Car car=new Car("Ferrari");
        car.setPilot(pilot);
        db.set(car);
    }

    public static void accessLocalServer() {
        ObjectServer server=Db4o.openServer(Util.YAPFILENAME,0);
        try {
            ObjectContainer client=server.openClient();
            // Do something with this client, or open more clients
            client.close();
        }
        finally {
            server.close();
        }
    }

    public static void queryLocalServer(ObjectServer server) {
        ObjectContainer client=server.openClient();
        listResult(client.get(new Car(null)));
        client.close();
    }

    public static void configureDb4o() {
        Db4o.configure().objectClass(Car.class).updateDepth(3);
    }
    
    public static void demonstrateLocalReadCommitted(ObjectServer server) {
        ObjectContainer client1=server.openClient();
        ObjectContainer client2=server.openClient();
        Pilot pilot=new Pilot("David Coulthard",98);
        ObjectSet result=client1.get(new Car("BMW"));
        Car car=(Car)result.next();
        car.setPilot(pilot);
        client1.set(car);
        listResult(client1.get(new Car(null)));
        listResult(client2.get(new Car(null)));
        client1.commit();        
        listResult(client1.get(Car.class));
        listRefreshedResult(client2,client2.get(Car.class),2);
        client1.close();
        client2.close();
    }

    public static void demonstrateLocalRollback(ObjectServer server) {
        ObjectContainer client1=server.openClient();
        ObjectContainer client2=server.openClient();
        ObjectSet result=client1.get(new Car("BMW"));
        Car car=(Car)result.next();
        car.setPilot(new Pilot("Someone else",0));
        client1.set(car);
        listResult(client1.get(new Car(null)));
        listResult(client2.get(new Car(null)));
        client1.rollback();
        client1.ext().refresh(car,2);
        listResult(client1.get(new Car(null)));
        listResult(client2.get(new Car(null)));
        client1.close();
        client2.close();
    }

    public static void accessRemoteServer() throws IOException {
        ObjectServer server=Db4o.openServer(Util.YAPFILENAME,PORT);
        server.grantAccess(USER,PASSWORD);
        try {
            ObjectContainer client=Db4o.openClient("localhost",PORT,USER,PASSWORD);
            // Do something with this client, or open more clients
            client.close();
        }
        finally {
            server.close();
        }
    }

    public static void queryRemoteServer(int port,String user,String password) throws IOException {
        ObjectContainer client=Db4o.openClient("localhost",port,user,password);
        listResult(client.get(new Car(null)));
        client.close();
    }

    public static void demonstrateRemoteReadCommitted(int port,String user,String password) throws IOException {
        ObjectContainer client1=Db4o.openClient("localhost",port,user,password);
        ObjectContainer client2=Db4o.openClient("localhost",port,user,password);
        Pilot pilot=new Pilot("Jenson Button",97);
        ObjectSet result=client1.get(new Car(null));
        Car car=(Car)result.next();
        car.setPilot(pilot);
        client1.set(car);
        listResult(client1.get(new Car(null)));
        listResult(client2.get(new Car(null)));
        client1.commit();
        listResult(client1.get(new Car(null)));
        listRefreshedResult(client2,client2.get(Car.class),2);
        client1.close();
        client2.close();
    }

    public static void demonstrateRemoteRollback(int port,String user,String password) throws IOException {
        ObjectContainer client1=Db4o.openClient("localhost",port,user,password);
        ObjectContainer client2=Db4o.openClient("localhost",port,user,password);
        ObjectSet result=client1.get(new Car(null));
        Car car=(Car)result.next();
        car.setPilot(new Pilot("Someone else",0));
        client1.set(car);
        listResult(client1.get(new Car(null)));
        listResult(client2.get(new Car(null)));
        client1.rollback();
        client1.ext().refresh(car,2);
        listResult(client1.get(new Car(null)));
        listResult(client2.get(new Car(null)));
        client1.close();
        client2.close();
    }
}
