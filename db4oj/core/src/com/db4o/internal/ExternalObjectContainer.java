/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.query.*;
import com.db4o.replication.*;
import com.db4o.types.*;


/**
 * @exclude
 */
public abstract class ExternalObjectContainer extends ObjectContainerBase implements InternalObjectContainer {
    
    public ExternalObjectContainer(Configuration config,ObjectContainerBase parentContainer) {
        super(config, parentContainer);
    }
    
    public final void activate(Object obj){
        activate(null, obj);
    }
    
    public final void activate(Object obj, int depth) throws DatabaseClosedException {
        activate(null, obj, activationDepthProvider().activationDepth(depth, ActivationMode.ACTIVATE));
    }
    
    public final void bind(Object obj, long id) throws ArgumentNullException, IllegalArgumentException {
        bind(null, obj, id);
    }

    /**
     * @deprecated
     */
    public Db4oCollections collections() {
        return collections(null);
    }
    
    public final void commit() throws DatabaseReadOnlyException, DatabaseClosedException {
        commit(null);
    }
    
    public final void deactivate(Object obj, int depth) throws DatabaseClosedException {
        deactivate(null, obj, depth);
    }

    public final void delete(Object a_object) {
        delete(null, a_object);
    }
    
    public Object descend(Object obj, String[] path){
        return descend(null, obj, path);
    }
    
    public ExtObjectContainer ext() {
        return this;
    }
    
    public final ObjectSet get(Object template) throws DatabaseClosedException {
        return get(null, template);
    }

    public final Object getByID(long id) throws DatabaseClosedException, InvalidIDException  {
        return getByID(null, id);
    }

    public final Object getByUUID(Db4oUUID uuid){
        return getByUUID(null, uuid);
    }
    
    public final long getID(Object obj) {
        return getID(null, obj);
    }

    public final ObjectInfo getObjectInfo (Object obj){
        return getObjectInfo(null, obj);
    }
    
    public boolean isActive(Object obj) {
        return isActive(null, obj);
    }

    public boolean isCached(long id) {
        return isCached(null, id); 
    }

    public boolean isStored(Object obj) {
        return isStored(null, obj);
    }

    public final Object peekPersisted(Object obj, int depth, boolean committed) throws DatabaseClosedException {
        return peekPersisted(null, obj, activationDepthProvider().activationDepth(depth, ActivationMode.PEEK), committed);
    }

    public final void purge(Object obj) {
        purge(null, obj);
    }

    public Query query() {
        return query((Transaction)null);
    }
    
    public final ObjectSet query(Class clazz) {
        return get(clazz);
    }
    
    public final ObjectSet query(Predicate predicate){
        return query(predicate,(QueryComparator)null);
    }
    
    public final ObjectSet query(Predicate predicate,QueryComparator comparator){
        return query(null, predicate, comparator);
    }

    public final void refresh(Object obj, int depth) {
        refresh(null, obj, depth);
    }
    
    public final void rollback() {
        rollback(null);
    }
    
    public final void set(Object obj) 
        throws DatabaseClosedException, DatabaseReadOnlyException {
        set(obj, Const4.UNSPECIFIED);
    }

    public final void set(Object obj, int depth) 
        throws DatabaseClosedException, DatabaseReadOnlyException {
        set(null, obj, depth);
    }
    
    public final StoredClass storedClass(Object clazz) {
        return storedClass(null, clazz);
    }
    
    public StoredClass[] storedClasses() {
        return storedClasses(null);
    }
    
    public abstract void backup(String path) throws Db4oIOException, DatabaseClosedException,
        NotSupportedException;
    
    public abstract Db4oDatabase identity();
    
    /**
     * @param peerB
     * @param conflictHandler
     * @deprecated
     */
    public ReplicationProcess replicationBegin(ObjectContainer peerB,
        ReplicationConflictHandler conflictHandler) {
        throw new NotSupportedException();
    }

    
}
