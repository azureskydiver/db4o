package db4ounit.tests;

import com.db4o.foundation.*;

import db4ounit.*;

public class ReflectionTestSuiteBuilderTestCase implements TestCase {
	
	private final static class ExcludingReflectionTestSuiteBuilder extends
			ReflectionTestSuiteBuilder {
		public ExcludingReflectionTestSuiteBuilder(Class[] classes) {
			super(classes);
		}

		protected boolean isApplicable(Class clazz) {
			return clazz!=NotAccepted.class;
		}
	}

	public static class NonTestFixture {
	}
	
	public void testUnmarkedTestFixture() {
		
		final ReflectionTestSuiteBuilder builder = new ReflectionTestSuiteBuilder(NonTestFixture.class);
		assertFailingTestCase(IllegalArgumentException.class, builder);
	}
	
	public static class Accepted implements TestCase {
		public void test() {
		}
	}

	public static class NotAccepted implements TestCase {
		public void test() {
		}
	}

	public void testNotAcceptedFixture() {
		ReflectionTestSuiteBuilder builder = new ExcludingReflectionTestSuiteBuilder(new Class[]{Accepted.class,NotAccepted.class});
		Assert.areEqual(1, Iterators.size(builder.build()));
	}
	
	public static class ConstructorThrows implements TestCase {
		
		public static final RuntimeException ERROR = new RuntimeException("no way");
		
		public ConstructorThrows() {
			throw ERROR;
		}
		
		public void test1() {
		}
		
		public void test2() {
		}
	}
	
	public void testConstructorFailuresAppearAsFailedTestCases() {
		
		final ReflectionTestSuiteBuilder builder = new ReflectionTestSuiteBuilder(ConstructorThrows.class);
		final Throwable cause = assertFailingTestCase(TestException.class, builder);
		Assert.areSame(ConstructorThrows.ERROR, ((TestException)cause).getReason());
	}

	private Throwable assertFailingTestCase(final Class expectedError,
			final ReflectionTestSuiteBuilder builder) {
		final TestSuite suite = new TestSuite(builder.build());
		Assert.areEqual(1, suite.getTests().length);
		
		FailingTest test = (FailingTest) suite.getTests()[0];
		Assert.areSame(expectedError, test.error().getClass());
		return test.error();
	}
}
