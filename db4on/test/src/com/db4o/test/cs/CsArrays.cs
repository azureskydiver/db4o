/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o.test.cs
{

	public class CsArrays
	{
        int[,] ints;

        public void Store(){
            Tester.DeleteAllInstances(this);
            ints = new int[2,2];
            ints[0,0] = 10;
            Tester.Store(this);
        }

        public void Test(){
            CsArrays csa = (CsArrays)Tester.GetOne(this);
            Tester.Ensure(csa.ints[0,0] == 10);
        }
	}
}
