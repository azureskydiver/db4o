/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions;

import com.db4o.*;
import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.query.Query;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

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
	
	public boolean isClientServer() {
		return fixture() instanceof AbstractClientServerDb4oFixture;
	}
    
    protected void reopen() throws Exception{
    	_fixture.reopen();
    }
	
	public final void setUp() throws Exception {
        _fixture.clean();
		configure(_fixture.config());
		_fixture.open();
        db4oSetupBeforeStore();
		store();
		_fixture.db().commit();
        _fixture.close();
        _fixture.open();
        db4oSetupAfterStore();
	}
	
	public final void tearDown() throws Exception {
		db4oCustomTearDown();
		_fixture.close();
        _fixture.clean();
	}
	
	protected void db4oSetupBeforeStore() throws Exception {}
	protected void db4oSetupAfterStore() throws Exception {}
	protected void db4oCustomTearDown() throws Exception {}

	protected void configure(Configuration config) {}
	
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
	
	public int runAll() {
		return runAll(true);
	}
	
	private int runAll(final boolean independentConfig) {
		return new TestRunner(new TestSuite(new Test[] {
				soloSuite(independentConfig).build(),
				clientServerSuite(independentConfig).build(),
				embeddedClientServerSuite(independentConfig).build(),
		})).run();
	}
	
	public int runSoloAndClientServer() {
		return runSoloAndClientServer(true);
	}

	private int runSoloAndClientServer(final boolean independentConfig) {
		return new TestRunner(new TestSuite(new Test[] {
				soloSuite(independentConfig).build(),
				clientServerSuite(independentConfig).build(),				
		})).run();
	}

	public int runSolo() {
		return runSolo(true);
	}

	public int runSolo(boolean independentConfig) {
		return new TestRunner(
					soloSuite(independentConfig)).run();
	}	

    public int runClientServer() {
    	return runClientServer(true);
    }
    
    public int runEmbeddedClientServer() {
    	return runEmbeddedClientServer(true);
    }

    private int runEmbeddedClientServer(boolean independentConfig) {
		return new TestRunner(embeddedClientServerSuite(independentConfig)).run();
	}

	public int runClientServer(boolean independentConfig) {
        return new TestRunner(
                    clientServerSuite(independentConfig)).run();
    }
    
    private Db4oTestSuiteBuilder soloSuite(boolean independentConfig) {
		return new Db4oTestSuiteBuilder(
				new Db4oSolo(configSource(independentConfig)), testCases());
	}

	private Db4oTestSuiteBuilder clientServerSuite(boolean independentConfig) {
		return new Db4oTestSuiteBuilder(
		        new Db4oSingleClient(configSource(independentConfig)), 
		        testCases());
	}
	
	private Db4oTestSuiteBuilder embeddedClientServerSuite(boolean independentConfig) {
		return new Db4oTestSuiteBuilder(
		        new Db4oSingleClient(configSource(independentConfig), true), 
		        testCases());
	}

    private ConfigurationSource configSource(boolean independentConfig) {
        return (independentConfig ? (ConfigurationSource)new IndependentConfigurationSource() : new GlobalConfigurationSource());
    }

	protected ObjectContainerBase stream() {
	    return (ObjectContainerBase) db();
	}
	
	public LocalObjectContainer fileSession() {
		return fixture().fileSession();
	}

	protected Transaction trans() {
	    return stream().getTransaction();
	}

	protected Transaction systemTrans() {
	    return stream().systemTransaction();
	}
	
	protected Query newQuery(Transaction transaction, Class clazz) {
		final Query query = newQuery(transaction);
		query.constrain(clazz);
		return query;
	}
	
	protected Query newQuery(Transaction transaction) {
		return stream().query(transaction);
	}
    
    protected Query newQuery(){
        return db().query();
    }
    
    protected Reflector reflector(){
        return stream().reflector();
    }

	protected void indexField(Configuration config,Class clazz, String fieldName) {
		config.objectClass(clazz).objectField(fieldName).indexed(true);
	}

	protected Transaction newTransaction() {
		return stream().newTransaction();
	}

	protected Query newQuery(Class clazz) {
		final Query query = newQuery();
		query.constrain(clazz);
		return query;
	}
	
	protected Object retrieveOnlyInstance(Class clazz) {
		ObjectSet result=newQuery(clazz).execute();
		Assert.areEqual(1,result.size());
		return result.next();
	}
	
	protected int countOccurences(Class clazz) {
		ObjectSet result = newQuery(clazz).execute();
		return result.size();
	}
	
	protected void foreach(Class clazz, Visitor4 visitor) {
        ExtObjectContainer oc = db();
        oc.deactivate(clazz, Integer.MAX_VALUE);
        ObjectSet set = newQuery(clazz).execute();
        while (set.hasNext()) {
            visitor.visit(set.next());
        }
	}
	
	protected void deleteAll(Class clazz) {
		foreach(clazz, new Visitor4() {
			public void visit(Object obj) {
				db().delete(obj);
			}
		});
	}
	
	protected final void store(Object obj) {
		db().set(obj);
	}

	protected ReflectClass reflectClass(Class clazz) {
		return reflector().forClass(clazz);
	}
	
	protected void defragment() throws Exception{
		fixture().close();
		fixture().defragment();
		fixture().open();
	}
	
}
