/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.constraints.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.callbacks.*;
import com.db4o.internal.query.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;
import com.db4o.replication.*;
import com.db4o.types.*;


/**
 * @exclude
 */
public abstract class PartialEmbeddedClientObjectContainer implements TransientClass, ObjectContainerSpec {
    
    protected final LocalObjectContainer _server;
    
    protected final Transaction _transaction;
    
    private boolean _closed = false;
    
    public PartialEmbeddedClientObjectContainer(LocalObjectContainer server, Transaction trans) {
        _server = server;
        _transaction = trans;
        _transaction.setOutSideRepresentation(cast(this));
    }
    
    public PartialEmbeddedClientObjectContainer(LocalObjectContainer server) {
        this(server, server.newTransaction(server.systemTransaction(), server.createReferenceSystem()));
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
        
        // FIXME: Consider allowing configuring
        // throw new NotSupportedException();
        
        // FIXME: Consider throwing NotSupportedException here.
        synchronized(lock()){
            checkClosed();
            return _server.configure();
        }
    }

    public Object descend(Object obj, String[] path) {
        synchronized(lock()){
            checkClosed();
            return _server.descend(_transaction, obj, path);
        }
    }

    private void checkClosed() {
        if(isClosed()){
            throw new DatabaseClosedException();
        }
    }

    public Object getByID(long id) throws DatabaseClosedException, InvalidIDException {
        synchronized(lock()){
            checkClosed();
            return _server.getByID(_transaction, id);
        }
    }

    public Object getByUUID(Db4oUUID uuid) throws DatabaseClosedException, Db4oIOException {
        synchronized(lock()){
            checkClosed();
            return _server.getByUUID(_transaction, uuid);
        }
    }

    public long getID(Object obj) {
        synchronized(lock()){
            checkClosed();
            return _server.getID(_transaction, obj);
        }
    }

    public ObjectInfo getObjectInfo(Object obj) {
        synchronized(lock()){
            checkClosed();
            return _server.getObjectInfo(_transaction, obj);
        }
    }

    // TODO: Db4oDatabase is shared between embedded clients.
    // This should work, since there is an automatic bind
    // replacement. Replication test cases will tell.
    public Db4oDatabase identity() {
        synchronized(lock()){
            checkClosed();
            return _server.identity();
        }
    }

    public boolean isActive(Object obj) {
        synchronized(lock()){
            checkClosed();
            return _server.isActive(_transaction, obj);
        }
    }

    public boolean isCached(long id) {
        synchronized(lock()){
            checkClosed();
            return _server.isCached(_transaction, id);
        }
    }

    public boolean isClosed() {
        synchronized (lock()) {
            return _closed == true;
        }
    }

    public boolean isStored(Object obj) throws DatabaseClosedException {
        synchronized(lock()){
            checkClosed();
            return _server.isStored(_transaction, obj);
        }
    }

    public ReflectClass[] knownClasses() {
        synchronized(lock()){
            checkClosed();
            return _server.knownClasses();
        }
    }

    public Object lock() {
        return _server.lock();
    }
    
    /** @param objectContainer */
    public void migrateFrom(ObjectContainer objectContainer) {
        throw new NotSupportedException();
    }

    public Object peekPersisted(Object object, int depth, boolean committed) {
        synchronized(lock()){
            checkClosed();
            return _server.peekPersisted(_transaction, object, new LegacyActivationDepth(depth, ActivationMode.PEEK), committed);
        }
    }

    public void purge() {
        synchronized(lock()){
            checkClosed();
            _server.purge();
        }
    }

    public void purge(Object obj) {
        synchronized(lock()){
            checkClosed();
            _server.purge(_transaction, obj);
        }
    }

    public GenericReflector reflector() {
        synchronized(lock()){
            checkClosed();
            return _server.reflector();
        }
    }

    public void refresh(Object obj, int depth) {
        synchronized(lock()){
            checkClosed();
            _server.refresh(_transaction, obj, depth);
        }
    }

    public void releaseSemaphore(String name) {
        synchronized(lock()){
            checkClosed();
            _server.releaseSemaphore(_transaction, name);
        }
    }

    /**
     * @param peerB
     * @param conflictHandler
     * @deprecated
     */
    public ReplicationProcess replicationBegin(ObjectContainer peerB,
        ReplicationConflictHandler conflictHandler) {
        throw new NotSupportedException();
    }

    public void set(Object obj, int depth) {
        synchronized(lock()){
            checkClosed();
            _server.set(_transaction, obj, depth);
        }
    }

    public boolean setSemaphore(String name, int waitForAvailability) {
        synchronized(lock()){
            checkClosed();
            return _server.setSemaphore(_transaction, name, waitForAvailability);
        }
    }

    public StoredClass storedClass(Object clazz) {
        synchronized(lock()){
            checkClosed();
            return _server.storedClass(_transaction, clazz);
        }
   }

    public StoredClass[] storedClasses() {
        synchronized(lock()){
            checkClosed();
            return _server.storedClasses(_transaction);
        }
    }

    public SystemInfo systemInfo() {
        synchronized(lock()){
            checkClosed();
            return _server.systemInfo();
        }
    }

    public long version() {
        synchronized(lock()){
            checkClosed();
            return _server.version();
        }
    }

    public void activate(Object obj, int depth) throws Db4oIOException, DatabaseClosedException {
        synchronized(lock()){
            checkClosed();
            _server.activate(_transaction, obj, activationDepthProvider().activationDepth(depth));
        }
    }

	private ActivationDepthProvider activationDepthProvider() {
		return _server.activationDepthProvider();
	}

    public boolean close() throws Db4oIOException {
        synchronized(lock()){
            if(isClosed()){
                return false;
            }
            if(! _server.isClosed()){
                if(! _server.configImpl().isReadOnly()){
                    commit();
                }
            }
            _transaction.close(false);
            _closed = true;
            return true;
        }
    }

    public void commit() throws Db4oIOException, DatabaseClosedException,
        DatabaseReadOnlyException, UniqueFieldValueConstraintViolationException {
        synchronized(lock()){
            checkClosed();
            _server.commit(_transaction);
        }
    }

    public void deactivate(Object obj, int depth) throws DatabaseClosedException {
        synchronized(lock()){
            checkClosed();
            _server.deactivate(_transaction, obj, depth);
        }
    }

    public void delete(Object obj) throws Db4oIOException, DatabaseClosedException,
        DatabaseReadOnlyException {
        synchronized(lock()){
            checkClosed();
            _server.delete(_transaction, obj);
        }
    }

    public ExtObjectContainer ext() {
        return (ExtObjectContainer)this;
    }

    public ObjectSet get(Object template) throws Db4oIOException, DatabaseClosedException {
        synchronized(lock()){
            checkClosed();
            return _server.get(_transaction, template);
        }
    }

    public Query query() throws DatabaseClosedException {
        synchronized(lock()){
            checkClosed();
            return _server.query(_transaction);
        }
    }

    public ObjectSet query(Class clazz) throws Db4oIOException, DatabaseClosedException {
        synchronized(lock()){
            checkClosed();
            return _server.query(_transaction, clazz);
        }
    }

    public ObjectSet query(Predicate predicate) throws Db4oIOException, DatabaseClosedException {
        synchronized(lock()){
            checkClosed();
            return _server.query(_transaction, predicate);
        }
    }

    public ObjectSet query(Predicate predicate, QueryComparator comparator) throws Db4oIOException,
        DatabaseClosedException {
        synchronized(lock()){
            checkClosed();
            return _server.query(_transaction, predicate, comparator);
        }
    }

    public void rollback() throws Db4oIOException, DatabaseClosedException,
        DatabaseReadOnlyException {
        synchronized(lock()){
            checkClosed();
            _server.rollback(_transaction);
        }
    }

    public void set(Object obj) throws DatabaseClosedException, DatabaseReadOnlyException {
        synchronized(lock()){
            checkClosed();
            _server.set(_transaction, obj);
        }
    }
    
    public ObjectContainerBase container(){
        return _server;
    }
    
    public Transaction transaction(){
        return _transaction;
    }
    
    public void callbacks(Callbacks cb){
        synchronized(lock()){
            checkClosed();
            _server.callbacks(cb);
        }
    }
    
    public Callbacks callbacks(){
        synchronized(lock()){
            checkClosed();
            return _server.callbacks();
        }
    }
    
    public final NativeQueryHandler getNativeQueryHandler() {
        synchronized(lock()){
            checkClosed();
            return _server.getNativeQueryHandler();
        }
    }
    
    public void onCommittedListener() {
        // do nothing
    }
    
    private static ObjectContainer cast(PartialEmbeddedClientObjectContainer container){
        return (ObjectContainer) container;
    }
    
    public ClassMetadata classMetadataForReflectClass(ReflectClass reflectClass) {
        return _server.classMetadataForReflectClass(reflectClass);
    }

    public ClassMetadata classMetadataForName(String name) {
        return _server.classMetadataForName(name);
    }

    public ClassMetadata classMetadataForId(int id) {
        return _server.classMetadataForId(id);
    }

    public HandlerRegistry handlers(){
        return _server.handlers();
    }

}
