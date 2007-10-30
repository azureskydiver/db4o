/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */
using System;
using Db4objects.Db4odoc.Refactoring.Initial;

namespace Db4objects.Db4odoc.Refactoring.Refactored
{
    class D: B
    {
        public DateTime storedDate;

        public override string ToString()
        {
            return name + "/" + number + ": " + storedDate;
        }
    }
}
