GETTING STARTED
---------------
This is the Sync4o connector for Funambol. It allows the Funambol synchronization
server and clients to be used with the db4o object database.

Licensing
---------
Sync4o is released under the GNU Public License (GPL) Version 2. Please see the 
file sync4o.license.txt for details.

Building
--------
To build the connector, please use the Apache Ant script (build.xml) provided. 

The "pack" target will build all relevant source files and create a Funambol 
server module (sync4o-3.0.1.s4j) and a .zip file sync4o-client-3.0.1.zip) 
containing the .jar files necessary to build a Funambol client program that 
uses sync4o.

The generated files will be found in the /dist/ folder.


Installing
----------
Please refer to the Funambol documentation for details on installing a Funambol 
server module.

The Funambol server we have been using required the following:

(1) Installation of sync4o-3.0.1.s4j into /FunambolServer/ds-server/modules

(2) Calling /FunambolServer/ds-server/bin/install-modules.cmd to install the 
modules.





Version Requirements
--------------------
Sync4o has been designed and built against Funambol 3.0 beta 1 and db4o 5.3. 
It will not work with earlier releases of db4o, though it should work with later 
releases without problem. It is not likely to work with either earlier or later 
releases of Funambol, as the SyncSource API that sync4o uses is under active 
development. However, migrating to later release should only require minor source 
code updates.

The sync4o design requires that databases to be synchronized have version number 
generation enabled. For details on how to do this, please refer to the db4o API 
documentation for the Configuration object.


ABOUT THIS SOFTWARE
-------------------
The software contained in this distribution is the open source Funambol connector 
"sync4o" supplied by:

db4objects Inc.
1900 South Norfolk Street
Suite 350
San Mateo, CA, 94403
USA 

Make sure that you have downloaded the latest version from the db4objects website:

http://www.db4objects.com

sync4o may be used under the GNU General Public License, GPL.
You should have received a copy of the GPL with the download.