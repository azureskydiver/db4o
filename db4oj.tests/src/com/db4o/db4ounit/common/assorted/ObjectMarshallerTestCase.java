/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.config.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class ObjectMarshallerTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new ObjectMarshallerTestCase().runSoloAndClientServer();
	}
	
	public static class Item{
		
		public int _one;
		
		public long _two;
		
		public int _three;
		
		public Item(int one, long two, int three) {
			_one = one;
			_two = two;
			_three = three;
			
		}

		public Item() {
		}
		
	}
	
	public static class ItemMarshaller implements ObjectMarshaller{
		
		public boolean readCalled;
		
		public boolean writeCalled;
		
		public void reset(){
			readCalled = false;
			writeCalled = false;
		}

		public void writeFields(Object obj, byte[] slot, int offset) {
			writeCalled = true;
			Item item = (Item)obj;
			PrimitiveCodec.writeInt(slot, offset, item._one);
			offset+= PrimitiveCodec.INT_LENGTH;
			PrimitiveCodec.writeLong(slot, offset, item._two);
			offset+= PrimitiveCodec.LONG_LENGTH;
			PrimitiveCodec.writeInt(slot, offset, item._three);
		}
	
		public void readFields(Object obj, byte[] slot, int offset) {
			readCalled = true;
			Item item = (Item)obj;
			item._one = PrimitiveCodec.readInt(slot, offset);
			offset+= PrimitiveCodec.INT_LENGTH;
			item._two = PrimitiveCodec.readLong(slot, offset);
			offset+= PrimitiveCodec.LONG_LENGTH;
			item._three = PrimitiveCodec.readInt(slot, offset);
		}
	
		public int marshalledFieldLength() {
			return PrimitiveCodec.INT_LENGTH * 2 + PrimitiveCodec.LONG_LENGTH;
		}
		
	}
	
	public static final ItemMarshaller marshaller = new ItemMarshaller();
	
	
	protected void configure(Configuration config) {
		super.configure(config);
		config.objectClass(Item.class).marshallWith(marshaller);
	}
	
	protected void store() throws Exception {
		marshaller.reset();
		store(new Item(Integer.MAX_VALUE, Long.MAX_VALUE, 1));
		Assert.isTrue(marshaller.writeCalled);
	}
	
	public void test() throws Exception{
		Item item = assertRetrieve();
		Assert.isTrue(marshaller.readCalled);
		
		marshaller.reset();
		db().set(item);
		Assert.isTrue(marshaller.writeCalled);
		
		defragment();
		
		assertRetrieve();
	}
	
	private Item assertRetrieve(){
		marshaller.reset();
		Item item = (Item) retrieveOnlyInstance(Item.class);
		Assert.areEqual(Integer.MAX_VALUE, item._one);
		Assert.areEqual(Long.MAX_VALUE, item._two);
		Assert.areEqual(1, item._three);
		return item;
	}
	
}
