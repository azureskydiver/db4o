/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o {

    /// <summary>
    /// attribute to declare field as transient.
    /// If you don't want a field to be stored by db4o,
    /// simply mark it with this attribute.
    /// </summary>
    public class Transient : Attribute {
        public Transient() {
        }
    }
}
