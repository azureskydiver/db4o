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

The instrumentation tool works by replacing invocations to:

    ObjectContainer.Query<Extent>(System.Predicate<Extent> match)
	
by:

    NativeQueryHandler.ExecuteQuery<Extent>(...)
			
inserting the appropriate stack adjustments instructions whenever
possible.

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

