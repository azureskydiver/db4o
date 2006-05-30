/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace j4o.lang {

    public class RuntimeException : Exception {
    
        public RuntimeException() {
        }

        public RuntimeException(String message) : base(message) {
        }

        public RuntimeException(Exception cause) : base(cause.Message, cause) {
        }

        public virtual Exception FillInStackTrace() {
            return this;
        }
    }
}
