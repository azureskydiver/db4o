﻿/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
using System;
using System.Collections.Generic;
using Db4oUnit.Extensions;
using Db4oUnit;

namespace Db4objects.Db4o.Tests.CLI2.Assorted
{
    class ListOfNullableItemTestCase : AbstractDb4oTestCase
    {
        private class Item
        {
            public IList<int?> nullableList;
            public Item(IList<int?> nullableList_)
            {
                nullableList = nullableList_;
            }

        }

        private static IList<int?> nullableIntList1()
        {
            return new List<int?>(new int?[] { 1, 2, 3 });
        }

        protected override void Store()
        {
            Item item = new Item(nullableIntList1());
            Store(item);
        }

        /**
         * TODO: Enable after fixing nullable array handling.
         */
        public void _test() 
        {
            Item item = (Item)RetrieveOnlyInstance(typeof(Item));
            Assert.IsNotNull(item.nullableList);
            Iterator4Assert.AreEqual(nullableIntList1().GetEnumerator(), item.nullableList.GetEnumerator());
        }
    }
}
