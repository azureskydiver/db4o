/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

namespace Db4objects.Db4odoc.Refactoring.Initial
{
    class B: A
    {
        public int number;

        public override string ToString()
        {
            return name + "/" + number;
        }
    }
}
