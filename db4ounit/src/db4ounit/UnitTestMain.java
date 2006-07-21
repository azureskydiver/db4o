package db4ounit;

public class UnitTestMain {
	public static void main(String[] args) throws Exception {
		Class[] classes=new Class[args.length];
		for(int idx=0;idx<args.length;idx++) {
			classes[idx]=Class.forName(args[idx]);
		}
		TestSuiteBuilder builder=new ReflectionTestSuiteBuilder(classes);
		TestRunner runner=new TestRunner(builder);
		runner.run();
	}
}
