/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.query.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.concurrency.*;
import db4ounit.extensions.fixtures.*;

public class AbstractDb4oTestCase implements Db4oTestCase {
    
	private transient Db4oFixture _fixture;
	
	private static final int DEFAULT_CONCURRENCY_THREAD_COUNT = 10;
	
	private transient int _threadCount = DEFAULT_CONCURRENCY_THREAD_COUNT;
	
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
		return fixture() instanceof Db4oClientServerFixture;
	}
	
	protected boolean isEmbeddedClientServer() {
		return isClientServer() && ((Db4oClientServerFixture)fixture()).embeddedClients();
	}
	
	// TODO: The following code is only a temporary addition until MTOC
	//       is part of the core. When it is, all occurences of this 
	//       method should be replaced with    isEmbeddedClientServer() 
	protected boolean isMTOC(){
	    return fixture().db() instanceof EmbeddedClientObjectContainer;
	}
    
    protected void reopen() throws Exception{
    	_fixture.reopen(getClass());
    }
	
	public final void setUp() throws Exception {
        _fixture.clean();
		configure(_fixture.config());
		_fixture.open(getClass());
        db4oSetupBeforeStore();
		store();
		_fixture.db().commit();
        _fixture.close();
        _fixture.open(getClass());
        db4oSetupAfterStore();
	}
	
	public final void tearDown() throws Exception {
		try {
			db4oTearDownBeforeClean();
		} finally {
			_fixture.close();
	        _fixture.clean();
		}
		db4oTearDownAfterClean();
	}
	
	protected void db4oSetupBeforeStore() throws Exception {}
	protected void db4oSetupAfterStore() throws Exception {}
	protected void db4oTearDownBeforeClean() throws Exception {}
	protected void db4oTearDownAfterClean() throws Exception {}

	protected void configure(Configuration config) throws Exception {}
	
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

    public int runConcurrency() {
    	return runConcurrency(true);
    }

    public int runEmbeddedConcurrency() {
    	return runEmbeddedConcurrency(true);
    }

    public int runConcurrencyAll() {
		return runConcurrencyAll(true);
	}
	
    protected int runEmbeddedClientServer(boolean independentConfig) {
		return new TestRunner(embeddedClientServerSuite(independentConfig)).run();
	}

	public int runClientServer(boolean independentConfig) {
        return new TestRunner(
                    clientServerSuite(independentConfig)).run();
    }

	private int runConcurrency(boolean independentConfig) {
    	return new TestRunner(concurrenyClientServerSuite(independentConfig, false, "CONC")).run();
	}
	
	private int runEmbeddedConcurrency(boolean independentConfig) {
    	return new TestRunner(concurrenyClientServerSuite(independentConfig, true, "CONC EMBEDDED")).run();
	}
	
	private int runConcurrencyAll(final boolean independentConfig) {
		return new TestRunner(new TestSuite(new Test[] {
				concurrenyClientServerSuite(independentConfig, false, "CONC").build(),
				concurrenyClientServerSuite(independentConfig, true, "CONC EMBEDDED").build(),
		})).run();
	}
	

    protected Db4oTestSuiteBuilder soloSuite(boolean independentConfig) {
		return new Db4oTestSuiteBuilder(
				new Db4oSolo(configSource(independentConfig)), testCases());
	}

	protected Db4oTestSuiteBuilder clientServerSuite(boolean independentConfig) {
		return new Db4oTestSuiteBuilder(
		        new Db4oClientServer(configSource(independentConfig), false, "C/S"), 
		        testCases());
	}
	
	protected Db4oTestSuiteBuilder embeddedClientServerSuite(boolean independentConfig) {
		return new Db4oTestSuiteBuilder(
		        new Db4oClientServer(configSource(independentConfig), true, "C/S EMBEDDED"), 
		        testCases());
	}

	protected Db4oTestSuiteBuilder concurrenyClientServerSuite(boolean independentConfig, boolean embedded, String label) {
		return new Db4oConcurrencyTestSuiteBuilder(
		        new Db4oClientServer(configSource(independentConfig), embedded, label), 
		        testCases());
	}
	
    protected ConfigurationSource configSource(boolean independentConfig) {
        return (independentConfig ? (ConfigurationSource)new IndependentConfigurationSource() : new GlobalConfigurationSource());
    }

    protected ObjectContainerBase stream() {
        return ((InternalObjectContainer) db()).container();
    }
    
    public LocalObjectContainer fileSession() {
        return fixture().fileSession();
    }

    public Transaction trans() {
        return ((InternalObjectContainer) db()).transaction();
    }

    protected Transaction systemTrans() {
        return trans().systemTransaction();
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
        return newQuery(db());
    }
    
    protected static Query newQuery(ExtObjectContainer oc){
        return oc.query();
    }
    
	protected Query newQuery(Class clazz) {
		return newQuery(db(), clazz);
	}
	
	protected static Query newQuery(ExtObjectContainer oc, Class clazz) {
		final Query query = newQuery(oc);
		query.constrain(clazz);
		return query;
	}
	
    protected Reflector reflector(){
        return stream().reflector();
    }

	protected void indexField(Configuration config,Class clazz, String fieldName) {
		config.objectClass(clazz).objectField(fieldName).indexed(true);
	}

	protected Transaction newTransaction() {
		return stream().newUserTransaction();
	}
	
	public Object retrieveOnlyInstance(Class clazz) {
		return retrieveOnlyInstance(db(), clazz);
	}
	
	public static Object retrieveOnlyInstance(ExtObjectContainer oc, Class clazz) {
		ObjectSet result=newQuery(oc, clazz).execute();
		Assert.areEqual(1,result.size());
		return result.next();
	}
	
	protected int countOccurences(Class clazz) {
		ObjectSet result = newQuery(clazz).execute();
		return result.size();
	}
	
	protected int countOccurences(ExtObjectContainer oc, Class clazz) {
		ObjectSet result = newQuery(oc, clazz).execute();
		return result.size();
	}
	
	protected void assertOccurrences(ExtObjectContainer oc, Class clazz, int expected) {
		Assert.areEqual(expected, countOccurences(oc, clazz));
	}
	
	protected void foreach(Class clazz, Visitor4 visitor) {
        ExtObjectContainer oc = db();
        oc.deactivate(clazz, Integer.MAX_VALUE);
        ObjectSet set = newQuery(clazz).execute();
        while (set.hasNext()) {
            visitor.visit(set.next());
        }
	}
	
	protected final void deleteAll(Class clazz) {
		deleteAll(db(), clazz);
	}
	
	protected final void deleteAll(final ExtObjectContainer oc, Class clazz) {
		foreach(clazz, new Visitor4() {
			public void visit(Object obj) {
				oc.delete(obj);
			}
		});
	}
	
	protected final void deleteObjectSet(ObjectSet os) {
		deleteObjectSet(db(), os);
	}
	
	protected final void deleteObjectSet(ObjectContainer oc, ObjectSet os) {
		while (os.hasNext()) {
			oc.delete(os.next());
		}
	}
	
	public final void store(Object obj) {
		db().set(obj);
	}
	
	protected ClassMetadata classMetadataFor(Class clazz) {
		return stream().classMetadataForReflectClass(reflectClass(clazz));
	}

	protected ReflectClass reflectClass(Class clazz) {
		return reflector().forClass(clazz);
	}
	
	protected void defragment() throws Exception{
		fixture().close();
		fixture().defragment();
		fixture().open(getClass());
	}
	
	public final int threadCount() {
		return _threadCount;
	}
	
	public final void configureThreadCount(int count) {
		_threadCount = count;
	}

	protected EventRegistry eventRegistry() {
		return EventRegistryFactory.forObjectContainer(db());
	}

	protected EventRegistry serverEventRegistry() {
		return EventRegistryFactory.forObjectContainer(fileSession());
	}
}
