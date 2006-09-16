/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions;

import com.db4o.Db4o;
import com.db4o.Transaction;
import com.db4o.YapStream;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;
import com.db4o.reflect.Reflector;

import db4ounit.TestRunner;
import db4ounit.extensions.fixtures.Db4oSolo;

public class AbstractDb4oTestCase implements Db4oTestCase {
    
	private transient Db4oFixture _fixture;
	
	/* (non-Javadoc)
	 * @see db4ounit.extensions.Db4oTestCase#fixture(db4ounit.extensions.Db4oFixture)
	 */
	public void fixture(Db4oFixture fixture) {
		_fixture = fixture;
	}

	/* (non-Javadoc)
	 * @see db4ounit.extensions.Db4oTestCase#fixture()
	 */
	public Db4oFixture fixture() {
		return _fixture;
	}
    
    protected void reopen() throws Exception{
        fixture().close();
        fixture().open();
    }
	
	public void setUp() throws Exception {
        _fixture.clean();
		configure();
		_fixture.open();
		store();
        _fixture.close();
        _fixture.open();
	}
	
	public void tearDown() throws Exception {
		_fixture.close();
        _fixture.clean();
	}

	protected void configure() {}
	
	protected void store() throws Exception {}

	/* (non-Javadoc)
	 * @see db4ounit.extensions.Db4oTestCase#db()
	 */
	public ExtObjectContainer db() {
		return fixture().db();
	}
	
	protected Class[] testCases() {
		return new Class[] { getClass() };
	}
	
	public int runSolo() {
		return new TestRunner(
					new Db4oTestSuiteBuilder(
							new Db4oSolo(), testCases())).run();
	}

	protected YapStream stream() {
	    return (YapStream) db();
	}

	protected Transaction trans() {
	    return stream().getTransaction();
	}

	protected Transaction systemTrans() {
	    return stream().getSystemTransaction();
	}
    
    protected Query newQuery(){
        return db().query();
    }
    
    protected Reflector reflector(){
        return stream().reflector();
    }

	protected void indexField(Class clazz, String fieldName) {
		Db4o.configure().objectClass(clazz).objectField(fieldName).indexed(true);
	}

	protected Transaction newTransaction() {
		return stream().newTransaction();
	}

	protected Query newQuery(Class clazz) {
		final Query query = newQuery();
		query.constrain(clazz);
		return query;
	}
}
