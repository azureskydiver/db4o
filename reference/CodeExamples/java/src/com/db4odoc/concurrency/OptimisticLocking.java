package com.db4odoc.concurrency;

import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.config.ConfigScope;
import com.db4o.cs.Db4oClientServer;
import com.db4o.cs.config.ServerConfiguration;
import com.db4o.events.*;
import com.db4o.foundation.Iterator4;
import com.db4o.internal.LazyObjectReference;

import java.io.File;


public class OptimisticLocking {
    public static void main(String[] args) {
        cleanUp();
        ServerConfiguration configuration = Db4oClientServer.newServerConfiguration();
        configuration.file().generateVersionNumbers(ConfigScope.GLOBALLY);
        ObjectServer server = Db4oClientServer.openServer(configuration,"database.db4o",1337);
        server.grantAccess("sa","sa");
        installOptimisticLocking(server);
        try{
            storeInitialObject();

            updateObject();
            concurrentUpdate();
        } finally {
            server.close();
        }
    }

    private static void installOptimisticLocking(ObjectServer server) {
        final EventRegistry events = EventRegistryFactory.forObjectContainer(server.ext().objectContainer());
        events.committing().addListener(new EventListener4<CommitEventArgs>() {
            @Override
            public void onEvent(Event4<CommitEventArgs> event, CommitEventArgs commitInfo) {
                for(Iterator4<LazyObjectReference> it = commitInfo.updated().iterator();it.moveNext();){
                    versionCheck(commitInfo, it.current());
                }
            }
        });
    }

    private static void versionCheck(CommitEventArgs commitInfo, LazyObjectReference ref) {
        ObjectContainer container = commitInfo.objectContainer();
        Object committerVersionObject = ref.getObject();
        if(committerVersionObject instanceof VersionedObject){
            VersionedObject committerVersion = committerVersion(container, committerVersionObject);
            VersionedObject currentVersionObject = currentVersion(ref, container);

            if(committerVersion.getVersion()==(currentVersionObject.getVersion()+1)){
                throw new VersionConflictException(committerVersion.getVersion(),
                        currentVersionObject.getVersion(),committerVersionObject);
            }
        }
    }

    private static VersionedObject currentVersion(LazyObjectReference ref, ObjectContainer container) {
        VersionedObject currentVersionObject = container.ext().getByID(ref.getInternalID());
        currentVersionObject = container.ext().peekPersisted(currentVersionObject,1,true);
        return currentVersionObject;
    }

    private static VersionedObject committerVersion(ObjectContainer container, Object currentObject) {
        VersionedObject commiterVersion = (VersionedObject) currentObject;
        container.ext().activate(commiterVersion);
        return commiterVersion;
    }

    private static void concurrentUpdate() {
        ObjectContainer client1 = openClient();
        ObjectContainer client2 = openClient();


        VersionedObject toUpdate1 = objectToUpdate(client1);
        VersionedObject toUpdate2 = objectToUpdate(client2);

        client1.store(toUpdate1);
        client1.commit();


        client2.store(toUpdate2);
        try{
            client2.commit();

        } catch (EventException ex){
            ex.getCause().printStackTrace();    
        }
    }

    private static void updateObject() {
        doWithClient(new DatabaseOperation() {
            @Override
            public void invoke(ObjectContainer container) {
                VersionedObject toUpdate = objectToUpdate(container);
                container.store(toUpdate);
                container.store(toUpdate);
                container.commit();
                container.store(toUpdate);
                container.commit();  
            }
        });
    }

    private static void storeInitialObject(){
        doWithClient(new DatabaseOperation() {
            @Override
            public void invoke(ObjectContainer container) {
                container.store(new VersionedObject());
            }
        });
    }

    private static void doWithClient(DatabaseOperation operation) {
        ObjectContainer container = openClient();
        try{
            doWithClient(container,operation);
        } finally {
            container.close();
        }
    }

    private static ObjectContainer openClient() {
        ObjectContainer container =  Db4oClientServer.openClient("localhost",1337,"sa","sa");
        EventRegistry events = EventRegistryFactory.forObjectContainer(container);
        events.creating().addListener(new VersionIncreaseListener());
        events.updating().addListener(new VersionIncreaseListener());
        return container;
    }

    private static VersionedObject objectToUpdate(ObjectContainer container) {
        return container.query(VersionedObject.class).get(0);
    }


    private static void cleanUp() {
        new File("database.db4o").delete();
    }

    private static void doWithClient(ObjectContainer container, DatabaseOperation operation) {
        operation.invoke(container);
    }

    private static class VersionIncreaseListener implements EventListener4<CancellableObjectEventArgs> {
        @Override
        public void onEvent(Event4<CancellableObjectEventArgs> event,
                            CancellableObjectEventArgs objectInfo) {
            if(objectInfo.object() instanceof VersionedObject) {
                VersionedObject versioned = (VersionedObject) objectInfo.object();
                versioned.increment();
            }
        }
    }
}
