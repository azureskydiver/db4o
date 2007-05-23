This folder contains library files, which you can use in your projects.

ant-@db4o.version.dotted@.jar               - ant build tool, used to build db4o sources 
                            (http://ant.apache.org/)
bloat-@db4o.version.dotted@.jar             - bytecode optimization library, used for Native
                            Query optimization
db4o-java1.1-@db4o.version.dotted@.jar      - db4o, use this jar with JDK1.1
db4o-java1.2-@db4o.version.dotted@.jar      - db4o for JDK1.2-1.4
db4o-java5-@db4o.version.dotted@.jar        - db4o for JDK5-6
db4o-nqopt-@db4o.version.dotted@.jar        - Native Query optimization library. This 
                            library should be available in the classpath 
                            for NQ optimization.
db4o-test-java1.1-@db4o.version.dotted@.jar - tests collection for JDK1.1
db4o-test-java1.2-@db4o.version.dotted@.jar - tests collection for JDK1.2-1.4
db4o-test-java5-@db4o.version.dotted@.jar   - tests collection for JDK5
db4ounit-@db4o.version.dotted@.jar          - testing framework for db4o needs 
                            (http://developer.db4o.com/Resources/view.aspx/Working_With_Source_Code/Testing_Db4o)
db4o-ta-@db4o.version.dotted@.jar           - Transparent Activation add-on (experimental)


USING DB4O LIBRARIES

For using db4o with JDK1.1 you will only need db4o-java1.1-@db4o.version.dotted@.jar.
For JDK1.2-6 you will need the corresponding db4o libraries plus 
db4o-nqopt-@db4o.version.dotted@.jar and bloat for Native Query optimization.


RUNNING THE TESTS

You can run the tests against db4o libraries from the distribution 
or from you own-built db4o versions.

For JDK1.1 run the following command from the current directory:
> java -cp db4o-test-java1.1-@db4o.version.dotted@.jar;db4ounit-@db4o.version.dotted@.jar;db4o-java1.1-@db4o.version.dotted@.jar com.db4o.db4ounit.jre11.AllTests

For JDK1.2-1.4:
> java -cp db4o-test-java1.2-@db4o.version.dotted@.jar;db4ounit-@db4o.version.dotted@.jar;db4o-java1.2-@db4o.version.dotted@.jar com.db4o.db4ounit.jre12.AllTestsJdk1_2

For JDK5-6:
> java -cp db4o-test-java5-@db4o.version.dotted@.jar;db4ounit-@db4o.version.dotted@.jar;db4o-java5-@db4o.version.dotted@.jar com.db4o.db4ounit.jre5.AllTestsDb4oUnitJdk5


