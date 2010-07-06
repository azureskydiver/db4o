package db4ounit;

import com.db4o.foundation.*;

public class ClassLevelFixtureTestSuite extends OpaqueTestSuiteBase {

	private final Class<?> _clazz;
	
	public ClassLevelFixtureTestSuite(Class<?> clazz, Closure4<Iterator4<Test>> tests) {
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

	public String label() {
		return _clazz.getName();
	}
	
	public Test transmogrify(final Function4<Test, Test> fun) {
		return new ClassLevelFixtureTestSuite(_clazz, 
			new Closure4<Iterator4<Test>>() {
				public Iterator4<Test> run() {
					return Iterators.map(tests().run(), new Function4<Test, Test>() {
						public Test apply(Test test) {
							return fun.apply(test);
						}
					});
				}
			});
	}


}
