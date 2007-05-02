package com.db4o.ta.instrumentation.test;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;
import com.db4o.replication.*;
import com.db4o.types.*;

import db4ounit.*;

public class TransparentActivationMockObjectContainer implements ExtObjectContainer {

	private Iterator4 _expected;
	
	public TransparentActivationMockObjectContainer(Iterator4 expected) {
		_expected = expected;
	}

	public void activate(Object obj, int depth) throws DatabaseClosedException {
		Assert.areEqual(1, depth);
		Assert.isTrue(_expected.moveNext());
		Assert.areEqual(_expected.current(), obj);
	}

	public boolean close() {
		throw new NotImplementedException();
	}

	public void commit() throws DatabaseClosedException,
			DatabaseReadOnlyException {
		throw new NotImplementedException();
	}

	public void deactivate(Object obj, int depth)
			throws DatabaseClosedException {
		throw new NotImplementedException();
	}

	public void delete(Object obj) throws DatabaseClosedException,
			DatabaseReadOnlyException {
		throw new NotImplementedException();
	}

	public ExtObjectContainer ext() {
		return this;
	}

	public ObjectSet get(Object template) throws DatabaseClosedException {
		throw new NotImplementedException();
	}

	public Query query() throws DatabaseClosedException {
		throw new NotImplementedException();
	}

	public ObjectSet query(Class clazz) throws DatabaseClosedException {
		throw new NotImplementedException();
	}

	public ObjectSet query(Predicate predicate) throws DatabaseClosedException {
		throw new NotImplementedException();
	}

	public ObjectSet query(Predicate predicate, QueryComparator comparator)
			throws DatabaseClosedException {
		throw new NotImplementedException();
	}

	public void rollback() throws DatabaseClosedException,
			DatabaseReadOnlyException {
		throw new NotImplementedException();
	}

	public void set(Object obj) throws DatabaseClosedException,
			DatabaseReadOnlyException {
		throw new NotImplementedException();
	}

	public void validate() {
		Assert.isFalse(_expected.moveNext());
	}

	public void backup(String path) throws BackupException,
			DatabaseClosedException, NotSupportedException {
		throw new UnsupportedOperationException();
	}

	public void bind(Object obj, long id) {
		throw new UnsupportedOperationException();
	}

	public Db4oCollections collections() {
		throw new UnsupportedOperationException();
	}

	public Configuration configure() {
		return Db4o.newConfiguration();
	}

	public Object descend(Object obj, String[] path) {
		throw new UnsupportedOperationException();
	}

	public Object getByID(long ID) {
		throw new UnsupportedOperationException();
	}

	public Object getByUUID(Db4oUUID uuid) {
		throw new UnsupportedOperationException();
	}

	public long getID(Object obj) {
		throw new UnsupportedOperationException();
	}

	public ObjectInfo getObjectInfo(Object obj) {
		throw new UnsupportedOperationException();
	}

	public Db4oDatabase identity() {
		throw new UnsupportedOperationException();
	}

	public boolean isActive(Object obj) {
		throw new UnsupportedOperationException();
	}

	public boolean isCached(long ID) {
		throw new UnsupportedOperationException();
	}

	public boolean isClosed() {
		throw new UnsupportedOperationException();
	}

	public boolean isStored(Object obj) {
		throw new UnsupportedOperationException();
	}

	public ReflectClass[] knownClasses() {
		throw new UnsupportedOperationException();
	}

	public Object lock() {
		throw new UnsupportedOperationException();
	}

	public void migrateFrom(ObjectContainer objectContainer) {
		throw new UnsupportedOperationException();
	}

	public Object peekPersisted(Object object, int depth, boolean committed) {
		throw new UnsupportedOperationException();
	}

	public void purge() {
		throw new UnsupportedOperationException();
	}

	public void purge(Object obj) {
		throw new UnsupportedOperationException();
	}

	public GenericReflector reflector() {
		throw new UnsupportedOperationException();
	}

	public void refresh(Object obj, int depth) {
		throw new UnsupportedOperationException();
	}

	public void releaseSemaphore(String name) {
		throw new UnsupportedOperationException();
	}

	public ReplicationProcess replicationBegin(ObjectContainer peerB,
			ReplicationConflictHandler conflictHandler) {
		throw new UnsupportedOperationException();
	}

	public void set(Object obj, int depth) {
		throw new UnsupportedOperationException();
	}

	public boolean setSemaphore(String name, int waitForAvailability) {
		throw new UnsupportedOperationException();
	}

	public StoredClass storedClass(Object clazz) {
		throw new UnsupportedOperationException();
	}

	public StoredClass[] storedClasses() {
		throw new UnsupportedOperationException();
	}

	public SystemInfo systemInfo() {
		throw new UnsupportedOperationException();
	}

	public long version() {
		throw new UnsupportedOperationException();
	}
}
