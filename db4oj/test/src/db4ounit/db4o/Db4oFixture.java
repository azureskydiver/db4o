package db4ounit.db4o;

import com.db4o.ext.*;

public interface Db4oFixture {
    
	void open() throws Exception;
    
	void close() throws Exception;
    
    void clean();
    
	ExtObjectContainer db();
}
