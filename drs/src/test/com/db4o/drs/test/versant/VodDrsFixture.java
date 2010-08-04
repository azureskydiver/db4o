/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.io.*;

import com.db4o.drs.inside.*;
import com.db4o.drs.test.*;
import com.db4o.drs.test.data.*;
import com.db4o.drs.versant.*;

public class VodDrsFixture implements DrsFixture{
	
	private static boolean enhanced = false;
	
	private final VodDatabase _vod;
	
	protected VodReplicationProvider _provider;
	

	public VodDrsFixture(String name){
		_vod = new VodDatabase(name);
		if(! enhanced ){
			JdoMetadataGenerator generator = new JdoMetadataGenerator(new File("bin"));
			
			// TODO: Knowledge about all the persistent classes right
			// now is in DrsTestCase.mappings
			// Move to a smarter place and pull all the package names
			// from there to generate .jdo files for all of them.
			
			File jdoFile = generator.generate(SPCChild.class);
			
			_vod.addJdoMetaDataFile(jdoFile);
			
			_vod.enhance();
			enhanced = true;
		}
		
	}
	
	public void clean() {
		_vod.removeDb();
	}
	
	public void close() {
		_provider.destroy();
		_provider = null;
	}

	public void open() {
		_vod.produceDb();
		_provider = new VodReplicationProvider(_vod);
	}

	public TestableReplicationProviderInside provider() {
		return _provider;
	}

}
