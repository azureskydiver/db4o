/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace j4o.lang {

    public class ClassNotFoundException : System.Exception {

        public ClassNotFoundException() {
        }

        public ClassNotFoundException(String name) : base(name) {
        }
    }
}
