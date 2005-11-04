/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o {

    /// <summary>
    /// Marks a field as transient.
    /// </summary>
    /// <remarks>
    /// Transient fields are not stored by db4o.
    /// <br />
    /// If you don't want a field to be stored by db4o,
    /// simply mark it with this attribute.
    /// </remarks>
    /// <exclude />
    [AttributeUsage(AttributeTargets.Field)]
    public class Transient : Attribute {
        public Transient() {
        }
    }
}

