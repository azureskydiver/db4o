package com.db4odoc.clientserver;

import java.io.*;
import com.db4o.*;


public class ClientServerExample  {
    private final static int PORT=0xdb40;
    private final static String USER="user";
    private final static String PASSWORD="password";
    public final static String YAPFILENAME="formula1.yap";
    
    public static void main(String[] args) throws IOException {
        new File(YAPFILENAME).delete();
        accessLocalServer();
        new File(YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(YAPFILENAME);
        try {
            setFirstCar(db);
            setSecondCar(db);
        }
        finally {
            db.close();
        }
        configureDb4o();
        ObjectServer server=Db4o.openServer(YAPFILENAME,0);
        try {
            queryLocalServer(server);
            demonstrateLocalReadCommitted(server);
            demonstrateLocalRollback(server);
        }
        finally {
            server.close();
        }
        accessRemoteServer();
        server=Db4o.openServer(YAPFILENAME,PORT);
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
    // end main
    
    public static void setFirstCar(ObjectContainer db) {
        Pilot pilot=new Pilot("Rubens Barrichello",99);
        Car car=new Car("BMW");
        car.setPilot(pilot);
        db.set(car);
    }
    // end setFirstCar

    public static void setSecondCar(ObjectContainer db) {
        Pilot pilot=new Pilot("Michael Schumacher",100);
        Car car=new Car("Ferrari");
        car.setPilot(pilot);
        db.set(car);
    }
    // end setSecondCar

    public static void accessLocalServer() {
        ObjectServer server=Db4o.openServer(YAPFILENAME,0);
        try {
            ObjectContainer client=server.openClient();
            // Do something with this client, or open more clients
            client.close();
        }
        finally {
            server.close();
        }
    }
    // end accessLocalServer

    public static void queryLocalServer(ObjectServer server) {
        ObjectContainer client=server.openClient();
        listResult(client.get(new Car(null)));
        client.close();
    }
    // end queryLocalServer

    public static void configureDb4o() {
        Db4o.configure().objectClass(Car.class).updateDepth(3);
    }
    // end configureDb4o
    
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
    // end demonstrateLocalReadCommitted

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
    // end demonstrateLocalRollback

    public static void accessRemoteServer() throws IOException {
        ObjectServer server=Db4o.openServer(YAPFILENAME,PORT);
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
    // end accessRemoteServer

    public static void queryRemoteServer(int port,String user,String password) throws IOException {
        ObjectContainer client=Db4o.openClient("localhost",port,user,password);
        listResult(client.get(new Car(null)));
        client.close();
    }
    // end queryRemoteServer

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
    // end demonstrateRemoteReadCommitted

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
    // end demonstrateRemoteRollback
    
    public static void listRefreshedResult(ObjectContainer container,ObjectSet result,int depth) {
        System.out.println(result.size());
        while(result.hasNext()) {
            Object obj = result.next();
            container.ext().refresh(obj, depth);
            System.out.println(obj);
        }
    }
    // end listRefreshedResult
    
    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
