Manifest-Version: 1.0
Bundle-Name: db4o OSGi test
Created-By: 1.5.0-beta2 (Sun Microsystems Inc.)
Bundle-Activator: com.db4o.osgi.test.Db4oTestActivator
Import-Package: org.osgi.framework
Bundle-Vendor: ProSyst
Bundle-Category: db
Bundle-Version: @db4o.version.dotted@
Bundle-SymbolicName: db4o_osgi_test
Require-Bundle: db4o_osgi
Bundle-ClassPath: .,
 lib/db4o-@db4o.version.dotted@-db4ounit.jar
Provide-Package: com.db4o.osgi.test

