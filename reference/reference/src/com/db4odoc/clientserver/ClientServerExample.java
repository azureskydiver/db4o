/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.clientserver;

import java.io.File;
import java.io.IOException;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;


public class ClientServerExample  {
    private final static int PORT=0xdb40;
    private final static String USER="user";
    private final static String PASSWORD="password";
    private final static String DB4O_FILE_NAME="reference.db4o";
    
    public static void main(String[] args) throws IOException {
        new File(DB4O_FILE_NAME).delete();
        accessLocalServer();
        new File(DB4O_FILE_NAME).delete();
        ObjectContainer container=Db4o.openFile(DB4O_FILE_NAME);
        try {
            setFirstCar(container);
            setSecondCar(container);
        }
        finally {
            container.close();
        }
        Configuration configuration = configureDb4o();
        ObjectServer server=Db4o.openServer(configuration, DB4O_FILE_NAME,0);
        try {
            queryLocalServer(server);
            demonstrateLocalReadCommitted(server);
            demonstrateLocalRollback(server);
        }
        finally {
            server.close();
        }
        accessRemoteServer();
        server=Db4o.openServer(configuration, DB4O_FILE_NAME,PORT);
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
    
    private static void setFirstCar(ObjectContainer container) {
        Pilot pilot=new Pilot("Rubens Barrichello",99);
        Car car=new Car("BMW");
        car.setPilot(pilot);
        container.store(car);
    }
    // end setFirstCar

    private static void setSecondCar(ObjectContainer container) {
        Pilot pilot=new Pilot("Michael Schumacher",100);
        Car car=new Car("Ferrari");
        car.setPilot(pilot);
        container.store(car);
    }
    // end setSecondCar

    private static void accessLocalServer() {
        ObjectServer server=Db4o.openServer(DB4O_FILE_NAME,0);
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

    private static void queryLocalServer(ObjectServer server) {
        ObjectContainer client=server.openClient();
        listResult(client.queryByExample(new Car(null)));
        client.close();
    }
    // end queryLocalServer

    private static Configuration configureDb4o() {
    	Configuration configuration = Db4o.newConfiguration();
    	configuration.objectClass(Car.class).updateDepth(3);
    	return configuration;
    }
    // end configureDb4o
    
    private static void demonstrateLocalReadCommitted(ObjectServer server) {
        ObjectContainer client1=server.openClient();
        ObjectContainer client2=server.openClient();
        Pilot pilot=new Pilot("David Coulthard",98);
        ObjectSet result=client1.queryByExample(new Car("BMW"));
        Car car=(Car)result.next();
        car.setPilot(pilot);
        client1.store(car);
        listResult(client1.queryByExample(new Car(null)));
        listResult(client2.queryByExample(new Car(null)));
        client1.commit();        
        listResult(client1.queryByExample(Car.class));
        listRefreshedResult(client2,client2.queryByExample(Car.class),2);
        client1.close();
        client2.close();
    }
    // end demonstrateLocalReadCommitted

    private static void demonstrateLocalRollback(ObjectServer server) {
        ObjectContainer client1=server.openClient();
        ObjectContainer client2=server.openClient();
        ObjectSet result=client1.queryByExample(new Car("BMW"));
        Car car=(Car)result.next();
        car.setPilot(new Pilot("Someone else",0));
        client1.store(car);
        listResult(client1.queryByExample(new Car(null)));
        listResult(client2.queryByExample(new Car(null)));
        client1.rollback();
        client1.ext().refresh(car,2);
        listResult(client1.queryByExample(new Car(null)));
        listResult(client2.queryByExample(new Car(null)));
        client1.close();
        client2.close();
    }
    // end demonstrateLocalRollback

    private static void accessRemoteServer() throws IOException {
        ObjectServer server=Db4o.openServer(DB4O_FILE_NAME,PORT);
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

    private static void queryRemoteServer(int port,String user,String password) throws IOException {
        ObjectContainer client=Db4o.openClient("localhost",port,user,password);
        listResult(client.queryByExample(new Car(null)));
        client.close();
    }
    // end queryRemoteServer

    private static void demonstrateRemoteReadCommitted(int port,String user,String password) throws IOException {
        ObjectContainer client1=Db4o.openClient("localhost",port,user,password);
        ObjectContainer client2=Db4o.openClient("localhost",port,user,password);
        Pilot pilot=new Pilot("Jenson Button",97);
        ObjectSet result=client1.queryByExample(new Car(null));
        Car car=(Car)result.next();
        car.setPilot(pilot);
        client1.store(car);
        listResult(client1.queryByExample(new Car(null)));
        listResult(client2.queryByExample(new Car(null)));
        client1.commit();
        listResult(client1.queryByExample(new Car(null)));
        listRefreshedResult(client2,client2.queryByExample(Car.class),2);
        client1.close();
        client2.close();
    }
    // end demonstrateRemoteReadCommitted

    private static void demonstrateRemoteRollback(int port,String user,String password) throws IOException {
        ObjectContainer client1=Db4o.openClient("localhost",port,user,password);
        ObjectContainer client2=Db4o.openClient("localhost",port,user,password);
        ObjectSet result=client1.queryByExample(new Car(null));
        Car car=(Car)result.next();
        car.setPilot(new Pilot("Someone else",0));
        client1.store(car);
        listResult(client1.queryByExample(new Car(null)));
        listResult(client2.queryByExample(new Car(null)));
        client1.rollback();
        client1.ext().refresh(car,2);
        listResult(client1.queryByExample(new Car(null)));
        listResult(client2.queryByExample(new Car(null)));
        client1.close();
        client2.close();
    }
    // end demonstrateRemoteRollback
    
    private static void listRefreshedResult(ObjectContainer container,ObjectSet result,int depth) {
        System.out.println(result.size());
        while(result.hasNext()) {
            Object obj = result.next();
            container.ext().refresh(obj, depth);
            System.out.println(obj);
        }
    }
    // end listRefreshedResult
    
    private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
