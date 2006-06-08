package db4ounit;


// TODO: refactor from inheritance to delegation: operate on arbitrary object?
public abstract class BaseTestCase extends TestCase {
	private final static Class[] PARAMTYPES={};
	private final static Object[] ARGS={};
	
	private String _name;
	
	public BaseTestCase() {
		this(null);
	}
	
	public BaseTestCase(String name) {
		_name=name;
	}
	
	public void name(String name) {
		_name=name;
	}
	
	protected void run() throws Exception {
		TestPlatform4.runMethod(this, _name,PARAMTYPES,ARGS);
	}
}
