/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace j4o.lang {

    public class RuntimeException : Exception {

        private Exception _cause;

        public RuntimeException() {
        }

        public RuntimeException(String message) : base(message) {
        }

        public RuntimeException(Exception cause){
            _cause = cause;
        }

        public virtual Exception fillInStackTrace() {
            return this;
        }
    }
}
