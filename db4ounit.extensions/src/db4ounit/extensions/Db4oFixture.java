/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;

import db4ounit.fixtures.*;

public interface Db4oFixture extends Labeled {
    
	void open(Class testCaseClass) throws Exception;
    
	void close() throws Exception;
	
	void reopen(Class testCaseClass) throws Exception;
    
    void clean();
    
	LocalObjectContainer fileSession();
	
	ExtObjectContainer db();
	
	Configuration config();
	
	boolean accept(Class clazz);

	void defragment() throws Exception;

	void configureAtRuntime(RuntimeConfigureAction action);

	void fixtureConfiguration(FixtureConfiguration configuration);

}
