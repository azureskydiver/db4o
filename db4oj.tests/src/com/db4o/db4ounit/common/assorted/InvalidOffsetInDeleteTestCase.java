package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.diagnostic.*;
import com.db4o.query.*;

import db4ounit.*;

public class InvalidOffsetInDeleteTestCase extends TestWithTempFile implements DiagnosticListener{
	
	public static class Item extends Parent{
		public String _itemName;
	}
	
	public static class Parent {
		public String _parentName;
	}
	
	public void test(){
		Configuration config = Db4o.newConfiguration();
		configure(config);
		ObjectContainer objectContainer = Db4o.openFile(config, tempFile());
		Item item = new Item();
		item._itemName = "item";
		item._parentName = "parent";
		objectContainer.store(item);
		objectContainer.close();
		config = Db4o.newConfiguration();
		configure(config);
		objectContainer = Db4o.openFile(config, tempFile());
		Query query = objectContainer.query();
		query.constrain(Item.class);
		ObjectSet objectSet = query.execute();
		item = (Item) objectSet.next();
		objectContainer.store(item);
		objectContainer.close();
	}

	private void configure(Configuration config) {
		config.diagnostic().addListener(this);
		config.generateVersionNumbers(ConfigScope.GLOBALLY);
		config.generateUUIDs(ConfigScope.GLOBALLY);
		config.objectClass(Item.class).objectField("_itemName").indexed(true);
		config.objectClass(Parent.class).objectField("_parentName").indexed(true);
	}

	public void onDiagnostic(Diagnostic d) {
		if(d instanceof DeletionFailed){
			Assert.fail("No deletion failed diagnostic message expected.");
		}
	}

}
