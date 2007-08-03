/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.constraints.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;
import com.db4o.replication.*;
import com.db4o.types.*;


/**
 * @exclude
 */
public abstract class PartialEmbeddedClientObjectContainer implements TransientClass, ObjectContainerSpec {
    
    private LocalObjectContainer _server;
    
    private final Transaction _transaction;
    
    public PartialEmbeddedClientObjectContainer(LocalObjectContainer server) {
        this(server, server.newTransaction(server.systemTransaction(), server.createReferenceSystem()));
    }

    public PartialEmbeddedClientObjectContainer(LocalObjectContainer server, Transaction trans) {
        _server = server;
        _transaction = trans;
    }

    /** @param path */
    public void backup(String path) throws Db4oIOException, DatabaseClosedException,
        NotSupportedException {
        throw new NotSupportedException();
    }

    public void bind(Object obj, long id) throws InvalidIDException, DatabaseClosedException {
        _server.bind(_transaction, obj, id);
    }

    public Db4oCollections collections() {
        return _server.collections(_transaction);
    }

    public Configuration configure() {
        throw new NotSupportedException();
    }

    public Object descend(Object obj, String[] path) {
        return _server.descend(_transaction, obj, path);
    }

    public Object getByID(long id) throws DatabaseClosedException, InvalidIDException {
        return _server.getByID(_transaction, id);
    }

    public Object getByUUID(Db4oUUID uuid) throws DatabaseClosedException, Db4oIOException {
        return _server.getByUUID(_transaction, uuid);
    }

    public long getID(Object obj) {
        return _server.getID(_transaction, obj);
    }

    public ObjectInfo getObjectInfo(Object obj) {
        return _server.getObjectInfo(_transaction, obj);
    }

    // TODO: Db4oDatabase is shared between embedded clients.
    // This should work, since there is an automatic bind
    // replacement. Replication test cases will tell.
    public Db4oDatabase identity() {
        return _server.identity();
    }

    public boolean isActive(Object obj) {
        return _server.isActive(_transaction, obj);
    }

    public boolean isCached(long id) {
        return _server.isCached(_transaction, id);
    }

    public boolean isClosed() {
        return _server == null;
    }

    public boolean isStored(Object obj) throws DatabaseClosedException {
        return _server.isStored(_transaction, obj);
    }

    public ReflectClass[] knownClasses() {
        return _server.knownClasses();
    }

    public Object lock() {
        return _server.lock();
    }

    public void migrateFrom(ObjectContainer objectContainer) {
        // TODO Auto-generated method stub

    }

    public Object peekPersisted(Object object, int depth, boolean committed) {
        return _server.peekPersisted(_transaction, object, depth, committed);
    }

    public void purge() {
        _server.purge();
    }

    public void purge(Object obj) {
        _server.purge(_transaction, obj);
    }

    public GenericReflector reflector() {
        return _server.reflector();
    }

    public void refresh(Object obj, int depth) {
        _server.refresh(_transaction, obj, depth);
    }

    public void releaseSemaphore(String name) {
        _server.releaseSemaphore(_transaction, name);

    }

    public ReplicationProcess replicationBegin(ObjectContainer peerB,
        ReplicationConflictHandler conflictHandler) {
        throw new NotSupportedException();
    }

    public void set(Object obj, int depth) {
        _server.set(_transaction, obj, depth);
    }

    public boolean setSemaphore(String name, int waitForAvailability) {
        return _server.setSemaphore(_transaction, name, waitForAvailability);
    }

    public StoredClass storedClass(Object clazz) {
        return _server.storedClass(_transaction, clazz);
   }

    public StoredClass[] storedClasses() {
        return _server.storedClasses(_transaction);
    }

    public SystemInfo systemInfo() {
        return _server.systemInfo();
    }

    public long version() {
        return _server.version();
    }

    public void activate(Object obj, int depth) throws Db4oIOException, DatabaseClosedException {
        _server.activate(_transaction, obj, depth);

    }

    public boolean close() throws Db4oIOException {
        if(isClosed()){
            return false;
        }
        _transaction.close(false);
        _server = null;
        return true;
    }

    public void commit() throws Db4oIOException, DatabaseClosedException,
        DatabaseReadOnlyException, UniqueFieldValueConstraintViolationException {
        _server.commit(_transaction);
    }

    public void deactivate(Object obj, int depth) throws DatabaseClosedException {
        _server.deactivate(_transaction, obj, depth);
    }

    public void delete(Object obj) throws Db4oIOException, DatabaseClosedException,
        DatabaseReadOnlyException {
        _server.delete(_transaction, obj);
    }

    public ExtObjectContainer ext() {
        return (ExtObjectContainer)this;
    }

    public ObjectSet get(Object template) throws Db4oIOException, DatabaseClosedException {
        return _server.get(_transaction, template);
    }

    public Query query() throws DatabaseClosedException {
        return _server.query(_transaction);
    }

    public ObjectSet query(Class clazz) throws Db4oIOException, DatabaseClosedException {
        return null;
    }

    public ObjectSet query(Predicate predicate) throws Db4oIOException, DatabaseClosedException {
        // TODO Auto-generated method stub
        return null;
    }

    public ObjectSet query(Predicate predicate, QueryComparator comparator) throws Db4oIOException,
        DatabaseClosedException {
        // TODO Auto-generated method stub
        return null;
    }

    public void rollback() throws Db4oIOException, DatabaseClosedException,
        DatabaseReadOnlyException {
        // TODO Auto-generated method stub

    }

    public void set(Object obj) throws DatabaseClosedException, DatabaseReadOnlyException {
        _server.set(_transaction, obj);
    }



}
