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
    
    private final LocalObjectContainer _server;
    
    private final Transaction _transaction;
    
    public PartialEmbeddedClientObjectContainer(LocalObjectContainer server) {
        this(server, server.newTransaction(server.systemTransaction(), server.createReferenceSystem()));
    }

    public PartialEmbeddedClientObjectContainer(LocalObjectContainer server, Transaction trans) {
        _server = server;
        _transaction = trans;
    }

    public void backup(String path) throws Db4oIOException, DatabaseClosedException,
        NotSupportedException {
        // TODO Auto-generated method stub

    }

    public void bind(Object obj, long id) throws InvalidIDException, DatabaseClosedException {
        // TODO Auto-generated method stub

    }

    public Db4oCollections collections() {
        // TODO Auto-generated method stub
        return null;
    }

    public Configuration configure() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object descend(Object obj, String[] path) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getByID(long ID) throws DatabaseClosedException, InvalidIDException {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getByUUID(Db4oUUID uuid) throws DatabaseClosedException, Db4oIOException {
        // TODO Auto-generated method stub
        return null;
    }

    public long getID(Object obj) {
        // TODO Auto-generated method stub
        return 0;
    }

    public ObjectInfo getObjectInfo(Object obj) {
        // TODO Auto-generated method stub
        return null;
    }

    public Db4oDatabase identity() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isActive(Object obj) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isCached(long ID) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isClosed() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isStored(Object obj) throws DatabaseClosedException {
        // TODO Auto-generated method stub
        return false;
    }

    public ReflectClass[] knownClasses() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object lock() {
        // TODO Auto-generated method stub
        return null;
    }

    public void migrateFrom(ObjectContainer objectContainer) {
        // TODO Auto-generated method stub

    }

    public Object peekPersisted(Object object, int depth, boolean committed) {
        // TODO Auto-generated method stub
        return null;
    }

    public void purge() {
        // TODO Auto-generated method stub

    }

    public void purge(Object obj) {
        // TODO Auto-generated method stub

    }

    public GenericReflector reflector() {
        // TODO Auto-generated method stub
        return null;
    }

    public void refresh(Object obj, int depth) {
        // TODO Auto-generated method stub

    }

    public void releaseSemaphore(String name) {
        // TODO Auto-generated method stub

    }

    public ReplicationProcess replicationBegin(ObjectContainer peerB,
        ReplicationConflictHandler conflictHandler) {
        // TODO Auto-generated method stub
        return null;
    }

    public void set(Object obj, int depth) {
        // TODO Auto-generated method stub

    }

    public boolean setSemaphore(String name, int waitForAvailability) {
        // TODO Auto-generated method stub
        return false;
    }

    public StoredClass storedClass(Object clazz) {
        // TODO Auto-generated method stub
        return null;
    }

    public StoredClass[] storedClasses() {
        // TODO Auto-generated method stub
        return null;
    }

    public SystemInfo systemInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    public long version() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void activate(Object obj, int depth) throws Db4oIOException, DatabaseClosedException {
        // TODO Auto-generated method stub

    }

    public boolean close() throws Db4oIOException {
        _transaction.close(false);
        return true;
    }

    public void commit() throws Db4oIOException, DatabaseClosedException,
        DatabaseReadOnlyException, UniqueFieldValueConstraintViolationException {
        _server.commit(_transaction);
    }

    public void deactivate(Object obj, int depth) throws DatabaseClosedException {
        // TODO Auto-generated method stub

    }

    public void delete(Object obj) throws Db4oIOException, DatabaseClosedException,
        DatabaseReadOnlyException {
        // TODO Auto-generated method stub

    }

    public ExtObjectContainer ext() {
        // TODO Auto-generated method stub
        return null;
    }

    public ObjectSet get(Object template) throws Db4oIOException, DatabaseClosedException {
        // TODO Auto-generated method stub
        return null;
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
