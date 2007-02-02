/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;

namespace com.db4o.inside.handlers
{
    abstract public class StructHandler : NetTypeHandler {

        public StructHandler(com.db4o.inside.ObjectContainerBase stream) : base(stream) {
        }

        public override bool IsEqual(Object compare, Object with){
            // TODO: Does == work here? Check !
            return compare.Equals(with);
        }

    }
}
