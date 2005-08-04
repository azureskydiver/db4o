/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o.test.cs
{

	public class CsArrays
	{
        int[,] ints;

        public void store(){
            Tester.deleteAllInstances(this);
            ints = new int[2,2];
            ints[0,0] = 10;
            Tester.store(this);
        }

        public void test(){
            CsArrays csa = (CsArrays)Tester.getOne(this);
            Tester.ensure(csa.ints[0,0] == 10);
        }
	}
}
