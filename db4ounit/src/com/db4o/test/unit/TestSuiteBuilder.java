package com.db4o.test.unit;

public class TestSuiteBuilder {
	public TestSuite buildSuite(Class clazz) {
		String[] testMethodNames=TestPlatform4.testMethodNames(clazz);
		TestCase[] testCases=new TestCase[testMethodNames.length];
		for (int i = 0; i < testMethodNames.length; i++) {
			testCases[i]=createTest(clazz,testMethodNames[i]);
			if(testCases[i]==null) {
				return null;
			}
			configure(testCases[i]);
		}
		return new TestSuite(clazz.getName(), testCases);
	}
	
	public TestCase createTest(Object clazz, String methodName) {
		BaseTestCase testCase=null;
		testCase=(BaseTestCase)TestPlatform4.create(clazz,new Class[]{String.class},new Object[]{methodName});
		if(testCase!=null) {
			return testCase;
		}
		testCase=(BaseTestCase)TestPlatform4.create(clazz,new Class[]{},new Object[]{});
		if(testCase==null) {
			return null;
		}
		testCase.name(methodName);
		return testCase;
	}

	
	protected void configure(TestCase test) {}
}
