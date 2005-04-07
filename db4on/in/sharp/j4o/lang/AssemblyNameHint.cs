/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace j4o.lang {

    /// <summary>
    /// holds a pair of short and long assembly name to help Type.forName()<br />
    /// Instances of this class are stored to the db4o database files.
    /// </summary>
    /// 
    public class AssemblyNameHint {

        public String shortName;
        public String longName;

        public AssemblyNameHint() {
        }

        public AssemblyNameHint(String shortName, String longName) {
            this.shortName = shortName;
            this.longName = longName;
        }
    }
}
