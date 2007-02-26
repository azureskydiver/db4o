db4o Build System v1.1 (dBS)
===========================

Welcome to the new dBS! 

dBS helps you to build all db4o products including db4o java , db4o .net and dRS.

QuickStart
==========
-svn checkout /javatocsharp to /javatocsharp
-edit machine.properties to suit your environment
	+ typically, you only need to change "dir.workspace"
-execute \build-1.1\db4o-java\keytool1
-if you installed Jdk5 only but do not have jdk 1.3, in \build-1.1\common-1.1.properties, uncomment the line "file.compiler.jdk1.3.args.optional=-source 1.3"
-Open a console
-Type ant
-Waiting for several minutes, all products are ready to use at /repository!

Updating db4o devtools and doctor
=================================
db4o devtools and doctor are required by dBS. They are already built and packaged in /lib. If you updated the source code of db4o devtools or doctor, you will need to rebuild them.

-Open a console
-Type ant -f build-build.xml
-The updated jars of db4o devtools and doctor will be placed in /lib

Using dBS as Production
=======================
- Turn "failonerror" to "true" in the <java> task of macrodef <runJvmTests> in run-java-tests/build.xml

