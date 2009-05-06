/* Copyright (C) 2008  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.migration;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.reflect.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;

import db4ounit.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class FieldsToTypeHandlerMigrationTestCase implements TestLifeCycle{
	
	public static class Item {
		
		public Item(int id) {
			_id = id;
		}

		public int _id;
		
	}
	
	private String _fileName;
	
	ItemTypeHandler _typeHandler;
	
	public static class ItemTypeHandler implements ReferenceTypeHandler, CascadingTypeHandler, VariableLengthTypeHandler{
		
		private int _writeCalls;
		
		private int _readCalls;
		
		public boolean canHold(ReflectClass type) {
			return ReflectClasses.areEqual(Item.class, type);
		}

		public void defragment(DefragmentContext context) {
			throw new NotImplementedException();
		}

		public void delete(DeleteContext context) throws Db4oIOException {
			throw new NotImplementedException();
		}

		public void activate(ReferenceActivationContext context) {
			_readCalls ++;
			Item item = (Item) ((UnmarshallingContext) context).persistentObject();
			item._id = context.readInt() - 42;
		}

		public void write(WriteContext context, Object obj) {
			_writeCalls ++;
			Item item = (Item) obj;
			context.writeInt(item._id + 42);
		}

		public PreparedComparison prepareComparison(Context context, Object obj) {
			throw new NotImplementedException();
		}

		public void cascadeActivation(ActivationContext context) {
			throw new NotImplementedException();
			
		}

		public void collectIDs(QueryingReadContext context) {
			throw new NotImplementedException();
		}

		public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
			throw new NotImplementedException();
		}

		public int writeCalls() {
			return _writeCalls;
		}

		public int readCalls() {
			return _readCalls;
		}

		public void reset() {
			_writeCalls = 0;
			_readCalls = 0;
		}
		
	}
	
	

	public void setUp() throws Exception {
		_fileName = Path4.getTempFileName();
		File4.delete(_fileName);
	}

	public void tearDown() throws Exception {
		File4.delete(_fileName);
	}
	
	public void testMigration(){
		_typeHandler = null;
		store(new Item(42));
		
		Item item = retrieveOnlyItemInstance();
		Assert.areEqual(42, item._id);
		
		assertItemStoredField(new Integer(42));
		
		_typeHandler = new ItemTypeHandler();
		
		item = retrieveOnlyItemInstance();
		Assert.areEqual(42, item._id);
		assertTypeHandlerCalls(0, 0);
		
		assertItemStoredField(new Integer(42));
		
		updateItem();
		assertTypeHandlerCalls(1, 0);
		
		assertItemStoredField(null);
		
		item = retrieveOnlyItemInstance();
		Assert.areEqual(42, item._id);
		assertTypeHandlerCalls(0, 1);
		
		assertItemStoredField(null);

	}
	
	public void testTypeHandler(){
		_typeHandler = new ItemTypeHandler();
		
		store(new Item(42));
		assertTypeHandlerCalls(1, 0);
		
		Item item = retrieveOnlyItemInstance();
		Assert.areEqual(42, item._id);
		assertTypeHandlerCalls(0, 1);
		
		updateItem();
		assertTypeHandlerCalls(1, 1);
		
	}
	
	private void assertItemStoredField(Object expectedValue){
		ObjectContainer db = openContainer();
		try{
			ObjectSet objectSet = db.query(Item.class);
			Assert.areEqual(1, objectSet.size());
			Item item = (Item) objectSet.next();
			StoredField storedField = db.ext().storedClass(Item.class).storedField("_id", null);
			Object actualValue = storedField.get(item);
			Assert.areEqual(expectedValue, actualValue);
		} finally {
			db.close();
		}
	}
	
	
	private void assertTypeHandlerCalls(int writeCalls, int readCalls){
		Assert.areEqual(writeCalls, _typeHandler.writeCalls());
		Assert.areEqual(readCalls, _typeHandler.readCalls());
	}

	private Item retrieveOnlyItemInstance() {
		ObjectContainer db = openContainer();
		try{
			ObjectSet objectSet = db.query(Item.class);
			Assert.areEqual(1, objectSet.size());
			Item item = (Item) objectSet.next();
			return item;
		} finally {
			db.close();
		}
	}

	private void store(Item item) {
		ObjectContainer db = openContainer();
		try{
			db.store(item);
		} finally {
			db.close();
		}
	}
	
	private void updateItem() {
		ObjectContainer db = openContainer();
		try {
		ObjectSet objectSet = db.query(Item.class);
		db.store(objectSet.next());
		} finally {
			db.close();
		}
	}

	private ObjectContainer openContainer() {
		if(_typeHandler != null){
			_typeHandler.reset();
		}
		Configuration configuration = Db4o.newConfiguration();
		if(_typeHandler != null){
			configuration.registerTypeHandler(new SingleClassTypeHandlerPredicate(Item.class), _typeHandler);
		}
		ObjectContainer db = Db4o.openFile(configuration, _fileName);
		return db;
	}

	public void defragment(DefragmentContext context) {
		// TODO Auto-generated method stub
		
	}

	public void delete(DeleteContext context) throws Db4oIOException {
		// TODO Auto-generated method stub
		
	}

	public Object read(ReadContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	public void write(WriteContext context, Object obj) {
		// TODO Auto-generated method stub
		
	}

	public PreparedComparison prepareComparison(Context context, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	


}
