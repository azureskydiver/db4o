CFNativeQueriesEnabler
======================

CompactFramework NQ enabler tool.

Background Information
----------------------

The CompactFramework API is missing two important properties in the
System.Delegate type: Target and Method.

Without these two properties is not possible to discover which method and
object are behind a delegate reference making it impossible for db4o
to do any query analysis at runtime.


The Solution
------------

The instrumentation tool works by replacing invocations of:

    ObjectContainer.Query<Extent>(System.Predicate<Extent> match)
	
by:

    NativeQueryHandler.ExecuteMeta<Extent>(
			com.db4o.inside.query.MetaDelegate<System.Predicate<Extent>> match)
			
inserting the appropriate MetaDelegate construction into the stack whenever
possible.

MetaDelegate is a structure which holds the delegate reference along with
its target object and method thus providing the missing CompactFramework 
API properties.


CFNativeQueries.Enabler.Tests
=============================

This is the test driver.

It works by invoking the instrumentation tool
on the CFNativeQueriesEnabler.Tests.Subject.exe assembly and then loading and
executing the instrumented assembly checking if NativeQueryHandler.ExecuteMeta
is being called as expected.

CFNativeQueries.Enabler.Tests.Subject
=====================================

Test cases for the instrumentation tool.

This application should not be executed directly. If you want to run the
test cases, run the driver application (CFNativeQueries.Enabler.Tests.exe).

