/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.replication.db4o;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.inside.replication.*;
import com.db4o.query.*;
import com.db4o.reflect.*;

//TODO: Add additional query methods (whereModified )


public class Db4oReplicationProvider implements TestableReplicationProvider, Db4oReplicationReferenceProvider, TestableReplicationProviderInside {
    
    private ReadonlyReplicationProviderSignature _mySignature;
    
    private final YapStream _stream;
    
    private final Reflector _reflector;
    
    private ReplicationRecord _replicationRecord;
    
    private Db4oReplicationReferenceImpl _referencesByObject;
    
    private Db4oSignatureMap _signatureMap;
    
    public Db4oReplicationProvider(ObjectContainer objectContainer){
        _stream = (YapStream)objectContainer;
        _reflector = _stream.reflector();
        _signatureMap = new Db4oSignatureMap(_stream);
    }

    public ReadonlyReplicationProviderSignature getSignature() {
        if(_mySignature == null){
            _mySignature = new Db4oReplicationProviderSignature(_stream.identity());
        }
        return _mySignature; 
    }
    
    public Object getMonitor() {
        return _stream.lock();
    }
    
    public void startReplicationTransaction(ReadonlyReplicationProviderSignature peerSignature) {
        
        clearAllReferences();
        
        synchronized(getMonitor()){
        
            Transaction trans = _stream.getTransaction();
            
            Db4oDatabase myIdentity = _stream.identity();
            _signatureMap.put(myIdentity);
            
            Db4oDatabase otherIdentity = _signatureMap.produce(peerSignature.getBytes(), peerSignature.getCreationTime()); 
            
            Db4oDatabase younger = null;
            Db4oDatabase older = null;
            
            if(myIdentity.isOlderThan(otherIdentity)){
                younger = otherIdentity;
                older = myIdentity;
            }else{
                younger = myIdentity;
                older = otherIdentity;
            }
            
            _replicationRecord = ReplicationRecord.queryForReplicationRecord(_stream, younger, older);
            if(_replicationRecord == null){
                _replicationRecord = new ReplicationRecord(younger, older);
                _replicationRecord.store(_stream);
            }
            
            long localInitialVersion = _stream.version();
        }
    }

    
    public void storeReplicationRecord(long version){
        long versionTest = getCurrentVersion();
        _replicationRecord._version = version;
        _replicationRecord.store(_stream);
    }
    

    public void commit(long raisedDatabaseVersion) {
        
        long versionTest = getCurrentVersion();
        
        _stream.raiseVersion(raisedDatabaseVersion);
        _stream.commit();
    }

    public void rollbackReplication() {
        _stream.rollback();
        _referencesByObject = null;
    }

    public long getCurrentVersion() {
        return _stream.version();
    }

    public long getLastReplicationVersion() {
        return _replicationRecord._version;
    }

    public void storeReplica(Object obj) {
        synchronized(getMonitor()){
            _stream.setByNewReplication(this, obj);
        }
    }

    public void activate(Object obj) {
        
        if(obj == null){
            return;
        }
        
        ReflectClass claxx = _reflector.forObject(obj);
        
        int level = claxx.isCollection() ? 3 : 1;
        
        _stream.activate(obj, level);
        
    }
    
    public Db4oReplicationReference referenceFor(Object obj) {
        if(_referencesByObject == null){
            return null;
        }
        return  _referencesByObject.find(obj);
    }
    
    public ReplicationReference produceReference(Object obj) {
        
        if(obj == null){
            return null;
        }
        
        if(_referencesByObject != null){
            Db4oReplicationReferenceImpl existingNode =  _referencesByObject.find(obj);
            if(existingNode != null){
                return existingNode;
            }
        }
        
        ObjectInfo objectInfo = _stream.getObjectInfo(obj);
        
        if(objectInfo == null){
            return null;
        }
        
        Db4oReplicationReferenceImpl newNode = new Db4oReplicationReferenceImpl (objectInfo);
        
        addReference(newNode);
        
        return newNode;
    }
    
    private void addReference(Db4oReplicationReferenceImpl newNode){
        if (_referencesByObject == null){
            _referencesByObject = newNode;
        }else{
            _referencesByObject = _referencesByObject.add(newNode);  
        }
    }
    
    public ReplicationReference referenceNewObject(Object obj, ReplicationReference counterpartReference){
        
        Db4oUUID uuid = counterpartReference.uuid();
        
        if(uuid == null){
            return null;
        }
        
        byte[] signature = uuid.getSignaturePart();
        long longPart = uuid.getLongPart();
        long version = counterpartReference.version();
        
        Db4oDatabase db = _signatureMap.produce(signature, 0);
        
        Db4oReplicationReferenceImpl ref = new Db4oReplicationReferenceImpl (obj, db, longPart, version); 
        
        addReference(ref);
        
        return ref;
    }

    public ReplicationReference produceReferenceByUUID(Db4oUUID uuid, Class hint) {
        if(uuid == null){
            return null;
        }
        Object obj = _stream.getByUUID(uuid);
        if(obj == null){
            return null;
        }
        if(! _stream.isActive(obj)){
            _stream.activate(obj, 1);
        }
        return produceReference(obj); 
    }

    public boolean hasReplicationReferenceAlready(Object obj) {
        if(_referencesByObject == null){
            return false;
        }
        return _referencesByObject.find(obj) != null;
    }
    
    public void visitCachedReferences(final Visitor4 visitor) {
        if(_referencesByObject != null){
            _referencesByObject.traverse(new Visitor4() {
                public void visit(Object obj) {
                    Db4oReplicationReferenceImpl node = (Db4oReplicationReferenceImpl) obj;
                    visitor.visit(node);
                }
            });
        }
    }

    public void clearAllReferences() {
        _referencesByObject = null;
    }

    public ObjectSet objectsChangedSinceLastReplication() {
        Query q = _stream.query();
        q.descend(VirtualField.VERSION).constrain(
            new Long(getLastReplicationVersion())).greater();
        return q.execute();
    }

    public ObjectSet objectsChangedSinceLastReplication(Class clazz) {
        Query q = _stream.query();
        q.constrain(clazz);
        q.descend(VirtualField.VERSION).constrain(
            new Long(getLastReplicationVersion())).greater();
        return q.execute();
    }

    public ObjectSet getStoredObjects(Class type) {
        return _stream.query(type);
    }

    public void storeNew(Object o) {
        _stream.set(o);
    }

    public void update(Object o) {
        _stream.set(o);
    }

    public String getName() {
        return Db4o.version();
    }

    public void closeIfOpened() {
        // do nothing
    }

    public void commit() {
        _stream.commit();
    }

    public void delete(Class clazz) {
        Query q = _stream.query();
        q.constrain(clazz);
        ObjectSet objectSet = q.execute();
        while(objectSet.hasNext()){
            _stream.delete(objectSet.next());
        }
    }

}
