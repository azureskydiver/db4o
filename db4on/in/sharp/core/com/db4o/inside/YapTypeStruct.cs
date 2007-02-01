/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;

namespace com.db4o.inside {

    abstract internal class YapTypeStruct : YapTypeAbstract {

        public YapTypeStruct(com.db4o.inside.ObjectContainerBase stream) : base(stream) {
        }

        public override bool IsEqual(Object compare, Object with){
            // TODO: Does == work here? Check !
            return compare.Equals(with);
        }

    }
}
