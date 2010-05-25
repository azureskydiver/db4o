package com.db4odoc.clientserver.refresh;

import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ObjectSet;
import com.db4o.cs.Db4oClientServer;
import com.db4o.cs.config.ServerConfiguration;
import com.db4o.events.*;
import com.db4o.ext.ObjectInfoCollection;
import com.db4o.foundation.Iterator4;
import com.db4o.internal.FrozenObjectInfo;
import com.db4o.internal.LazyObjectReference;

import java.io.File;


public class Refresh {
    private static final String DATABASE_FILE_NAME = "database.db4o";


    public static void main(String[] args) {
        cleanUp();

        ObjectServer server = openServer();
        server.grantAccess("sa","sa");

       
        openMonitoringClient("Client 1");
        storeJoe();
        updateJoe();
        deleteJoe();

        sleepForAWhile();

        server.close();
        cleanUp();
    }

    private static void storeJoe() {
        ObjectContainer client = openClient();
        client.store(new Person("Joe"));
        client.commit();
        client.close();
    }

    private static void updateJoe() {
        ObjectContainer container = openClient();
        ObjectSet<Person> persons = container.query(Person.class);
        for (Person person : persons) {
            person.setName("New "+person.getName());
            container.store(person);
        }
        container.close();
    }

    private static void deleteJoe() {
        ObjectContainer container = openClient();
        ObjectSet<Person> persons = container.query(Person.class);
        for (Person person : persons) {
            container.delete(person);
        }
        container.close();
    }


    private static void openMonitoringClient(String clientName) {
        ObjectContainer client = openClient();
        registerEvent(clientName,client);
        ObjectSet<Person> persons = client.query(Person.class);
        for (Person person : persons) {
            System.out.println("Person on client"+person);
        }
    }

    private static void registerEvent(final String clientName,final ObjectContainer container) {
        EventRegistry events = EventRegistryFactory.forObjectContainer(container);
        events.committed().addListener(new EventListener4<CommitEventArgs>() {
            public void onEvent(Event4<CommitEventArgs> commitEventArgsEvent4, CommitEventArgs commitEventArgs) {
                printChangedObjects(container,commitEventArgs.added(), clientName, " added ");
                printChangedObjects(container,commitEventArgs.updated(), clientName, " updated ");
                printDeletedObjects(commitEventArgs.deleted(), clientName, " deleted ");
            }
        });
    }

    private static void printChangedObjects(ObjectContainer container,ObjectInfoCollection collection, String clientName, String eventName) {
        for(Iterator4 it = collection.iterator();it.moveNext();){
            LazyObjectReference reference = (LazyObjectReference) it.current();
            Object obj = reference.getObject();
            container.ext().refresh(obj,1);
            System.out.println(clientName + eventName +obj);
        }
    }
    private static void printDeletedObjects(ObjectInfoCollection collection, String clientName, String eventName) {
        for(Iterator4 it = collection.iterator();it.moveNext();){
            FrozenObjectInfo reference = (FrozenObjectInfo) it.current();
            System.out.println(clientName + eventName+" with id "+reference.getInternalID());
        }
    }

    private static ObjectContainer openClient() {
        return Db4oClientServer.openClient("localhost",1337,"sa","sa");
    }

    private static void cleanUp() {
        new File(DATABASE_FILE_NAME).delete();
    }

    private static void sleepForAWhile() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static ObjectServer openServer() {
        ServerConfiguration configuration = Db4oClientServer.newServerConfiguration();
        return Db4oClientServer.openServer(configuration, DATABASE_FILE_NAME,1337);
    }
}
