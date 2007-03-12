/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre11.events;

import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;

import db4ounit.extensions.*;


public class UniqueFieldIndexTestCase extends AbstractDb4oTestCase{
	
	public static void main(String[] arguments) {
		new UniqueFieldIndexTestCase().runSolo();
	}
	
	public static class Item {
		
		public String	_str;

		public Item(){
			
		}
		
		public Item(String str){
			_str = str;
		}
		
	}
	
	protected void configure(Configuration config) {
		super.configure(config);
		config.objectClass(Item.class).objectField("_str").indexed(true);
		// config.objectClass(Item.class).objectField("_str").uniqueIndex(true);
	}
	
	protected void store() throws Exception {
		store(new Item("1"));
		store(new Item("2"));
		store(new Item("3"));
	}
	
	public void testNew(){
		addCommitListener();
		store(new Item("2"));
		
	}

	private void addCommitListener() {
		eventRegistry().committing().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				CommitEventArgs commitEventArgs = (CommitEventArgs) args;
				Iterator4 i = commitEventArgs.added().iterator();
				while(i.moveNext()){
					ObjectInfo info = (ObjectInfo) i.current();
					int id = (int)info.getInternalID();
					HardObjectReference ref = HardObjectReference.peekPersisted(filetrans(), id, 1);
					Item item = (Item) ref._object;
					String str = item._str;
					FieldMetadata fieldMetadata = ref._reference.getYapClass().getYapField("_str");
					BTreeRange range = fieldMetadata.search(filetrans(), str);
					System.out.println(range.size());
				}
			}
		});
	}
	
	private Transaction filetrans(){
		return fileSession().getTransaction();
	}
	
	private EventRegistry eventRegistry() {
		return EventRegistryFactory.forObjectContainer(fileSession());
	}

}
