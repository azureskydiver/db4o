db4o Build System v1.1 (dBS)
===========================

Welcome to the new dBS! 

dBS helps you to build all db4o products including db4o java , db4o .net and dRS.

QuickStart
==========
-Open a console
-Type ant
-Waiting for several minutes, all products are ready to use at /repository!

Updating db4o devtools and doctor
=================================
db4o devtools and doctor are required by dBS. They are already built and packaged in /lib. If you updated the source code of db4o devtools or doctor, you will need to rebuild them.

-Open a console
-Type ant -f build-build.xml
-The updated jars of db4o devtools and doctor will be placed in /lib