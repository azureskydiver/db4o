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

}
