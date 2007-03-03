db4o Build System v1.1 (dBS)
===========================

Welcome to the new dBS! 

dBS helps you to build all db4o products including db4o java , db4o .net and dRS.

QuickStart
==========
http://developer.db4o.com/ProjectSpaces/edit.aspx/Db4o_Product_Design/Build-1.1

Don't forget to provide a machine.properties file to configure your local environment.
For details, please refer to machine.properties.example.windows as an example. 
Rename it to machine.properties and you are ready to go.

Using dBS as Production
=======================
- Turn "failonerror" to "true" in the <java> task of macrodef <runJvmTests> in run-java-tests/build.xml

