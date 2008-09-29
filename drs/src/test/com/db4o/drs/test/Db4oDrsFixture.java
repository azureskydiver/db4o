/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com

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
package com.db4o.drs.test;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.drs.db4o.*;
import com.db4o.drs.inside.*;
import com.db4o.ext.*;

public class Db4oDrsFixture implements DrsFixture {
	static final File RAM_DRIVE = new File("w:");
	
	protected String _name;
	protected ExtObjectContainer _db;
	protected TestableReplicationProviderInside _provider;
	protected final File testFile;
	private Configuration _config;
	
	public Db4oDrsFixture(String name) {
		_name = name;
		
		if (RAM_DRIVE.exists())
			testFile = new File(RAM_DRIVE.getPath() + "drs_cs_" + _name + ".yap");
		else	
			testFile = new File("drs_cs_" + _name + ".yap");
	}
	
	public TestableReplicationProviderInside provider() {
		return _provider;
	}

	public void clean() {
		testFile.delete();
		_config = null;
	}

	public void close() {
		_provider.destroy();
		_db.close();
	}

	public ExtObjectContainer db() {
		return _db;
	}

	public void open() {
		//	Comment out because MemoryIoAdapter has problems on .net 
		//	MemoryIoAdapter memoryIoAdapter = new MemoryIoAdapter();
		//	Db4o.configure().io(memoryIoAdapter);
		
		_db = Db4oEmbedded.openFile(config(), testFile.getPath()).ext();
		_provider = Db4oProviderFactory.newInstance(_db, _name);
	}
	
	public Configuration config() {
		if(_config == null) {
			_config = Db4oEmbedded.newConfiguration();
		}
		return _config;
	}
}