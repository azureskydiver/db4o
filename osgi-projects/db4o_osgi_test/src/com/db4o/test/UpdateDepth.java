/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.test;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.tools.*;

public class UpdateDepth {
	
	String name;
	UpdateDepth child;
	UpdateDepth[] childArray;
	

	public static void main(String[] args) {
		Configuration conf = Db4o.configure();
		
		
		// STILL A MANUAL TEST

		
		// Play with the following two parameters and watch the output.
		// conf.updateDepth(0);
		conf.objectClass("com.db4o.test.UpdateDepth").updateDepth(1);


		new File("updateDepth.yap").delete();
		ObjectContainer con = Db4o.openFile("updateDepth.yap");
		ObjectSet set = null;
		UpdateDepth ud = new UpdateDepth();
		ud.name = "Level 0";
		ud.child = new UpdateDepth();
		ud.child.name = "Level 1";
		ud.child.child = new UpdateDepth();
		ud.child.child.name = "Level 2";
		ud.childArray = new UpdateDepth[] {new UpdateDepth()};
		ud.childArray[0].name = "Array Level 1";
		ud.child.childArray = new UpdateDepth[] {new UpdateDepth()};
		ud.child.childArray[0].name = "Array Level 2";
		con.set(ud);
		
		/*
		set = con.get(null);
		while(set.hasNext()){
		  Logger.log(con, set.next());
		}
		*/
		
		
		ud.name = "Update Level 0";
		ud.child.name = "Update Level 1";
		ud.child.child.name = "Update Level 2";
		ud.childArray[0].name = "Update Array Level 1";
		ud.child.childArray[0].name = "Update Array Level 2";
		con.set(ud);
		con.close();
		con = Db4o.openFile("updateDepth.yap");		
		set = con.get(null);
		while(set.hasNext()){
			Logger.log(con, set.next());
		}
		con.close();
	}
}
