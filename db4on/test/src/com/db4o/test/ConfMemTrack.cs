/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o.test {
    /// <summary>
    /// Tests WeakReferences.
    /// Derive AllTests from this configuration and watch
    /// the memory consumption in the task manager while
    /// this test runs.
    /// </summary>
    public class ConfMemTrack : AllTestsConfAll {

        internal Type[] TESTS = new Type[]{
                                              typeof(MemTrack)
                                          };
       
        internal int RUNS = 1000;
        internal bool CLIENT_SERVER = false;
    }
}
