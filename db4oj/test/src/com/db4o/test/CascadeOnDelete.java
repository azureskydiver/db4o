/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.test.types.*;

public class CascadeOnDelete{
	
	public ObjectSimplePublic[] osp;
	
	public void test() {
		noAccidentalDeletes();
	}
	
	private void noAccidentalDeletes(){
	 	noAccidentalDeletes1(true, true);
	 	noAccidentalDeletes1(true, false);
	 	noAccidentalDeletes1(false, true);
	 	noAccidentalDeletes1(false, false);
	}
	
	private void noAccidentalDeletes1(boolean cascadeOnUpdate, boolean cascadeOnDelete){
		ObjectContainer con = Test.objectContainer();
		Test.deleteAllInstances(this);
		Test.deleteAllInstances(new ObjectSimplePublic());
		ObjectClass oc = Db4o.configure().objectClass(CascadeOnDelete.class.getName());
		oc.cascadeOnDelete(cascadeOnDelete);
		oc.cascadeOnUpdate(cascadeOnUpdate);
		con = Test.reOpen();
		ObjectSimplePublic myOsp = new ObjectSimplePublic();
		myOsp.set(1);
		CascadeOnDelete cod = new CascadeOnDelete();
		cod.osp = new ObjectSimplePublic[]{
			myOsp
		};
		con.set(cod);
		con.commit();
		
		cod.osp[0].name = "abrakadabra";
		con.set(cod);
		if(! cascadeOnDelete && ! cascadeOnUpdate){
			// the only case, where we don't cascade
			con.set(cod.osp[0]);
		}
		
		Test.ensureOccurrences(cod.osp[0], 1);
		con.commit();
		Test.ensureOccurrences(cod.osp[0], 1);
	}
}

