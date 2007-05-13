This folder contains binary release files in the following folders:
compact-1.1 
compact-2.0 
net-1.1 
net-2.0 
(This list might be not full for your distribution depending on the
selected version.)


compact-1.1, compact-2.0 and net-1.1 contain the following files:

db4objects.db4o.dll       - db4o database library. Include it in your
                            project references to get access to the 
                            db4o functionality
db4objects.db4o.xml       - assembly documentation file
db4objects.db4o.tools.dll - db4o tools package, used for Native Query 
                            optimization, contains defragment and 
                            statistics tools. 
                            Include this library in your project 
                            references to enable Native Query
                            optimization.
                        
db4objects.db4o.tools.xml - assembly documentation file
cecil.flowanalysis.dll 
and mono.cecil.dll        - bytecode analyzing libraries, used for Native 
                            Query Optimization.
                            (http://www.mono-project.com/Cecil)
                            Include this library in your project 
                            references to enable Native Query
                            optimization.


net-2.0 in addition to the above mentioned files also includes:

db4oadmin.exe             - bytecode instrumentation tool, for a list of 
                            options execute without parameters
mono.getoptions.dll       - library used for parameter parsing in 
                            Db4oAdmin tool.
