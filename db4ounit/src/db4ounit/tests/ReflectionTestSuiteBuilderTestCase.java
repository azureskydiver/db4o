package db4ounit.tests;

import db4ounit.Assert;
import db4ounit.CodeBlock;
import db4ounit.ReflectionTestSuiteBuilder;
import db4ounit.TestCase;

public class ReflectionTestSuiteBuilderTestCase implements TestCase {
	
	public static class NonTestFixture {
	}
	
	public void testUnmarkedTestFixture() {
		
		final ReflectionTestSuiteBuilder builder = new ReflectionTestSuiteBuilder(NonTestFixture.class);
		
		Assert.expect(IllegalArgumentException.class, new CodeBlock() {
			public void run() throws Exception {
				builder.build();
			}
		});
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
		ReflectionTestSuiteBuilder builder = new ReflectionTestSuiteBuilder(new Class[]{Accepted.class,NotAccepted.class}) {
			protected boolean isApplicable(Class clazz) {
				return clazz!=NotAccepted.class;
			}
		};
		Assert.areEqual(1,builder.build().getTests().length);
	}
}
