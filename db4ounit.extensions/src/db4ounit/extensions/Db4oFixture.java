/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;

public interface Db4oFixture {
	
	String getLabel();
    
	void open() throws Exception;
    
	void close() throws Exception;
	
	void reopen() throws Exception;
    
    void clean();
    
	LocalObjectContainer fileSession();
	
	ExtObjectContainer db();
	
	Configuration config();
	
	boolean accept(Class clazz);

	void defragment() throws Exception;

	void configureAtRuntime(RuntimeConfigureAction action);
}
