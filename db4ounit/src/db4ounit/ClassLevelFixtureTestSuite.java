package db4ounit;

import com.db4o.foundation.*;

public class ClassLevelFixtureTestSuite extends OpaqueTestSuiteBase {

	private final Class<?> _clazz;
	
	public ClassLevelFixtureTestSuite(Class<?> clazz, Iterator4<Test> tests) {
		super(tests);
		_clazz = clazz;
	}

	@Override
	protected void suiteSetUp() throws Exception {
		_clazz.getMethod("classSetUp", null).invoke(null, null);
	}

	@Override
	protected void suiteTearDown() throws Exception {
		_clazz.getMethod("classTearDown", null).invoke(null, null);
	}

}
