/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o.test
{
	public class ArrayNOrder
	{
        public String[,,] s1;
        public Object[,] o1;

        public void store() {
            Tester.deleteAllInstances(this);
            s1 = new String[2,2,3];
            s1[0,0,0] = "000";
            s1[0,0,1] = "001";
            s1[0,0,2] = "002";
            s1[0,1,0] = "010";
            s1[0,1,1] = "011";
            s1[0,1,2] = "012";
            s1[1,0,0] = "100";
            s1[1,0,1] = "101";
            s1[1,0,2] = "102";
            s1[1,1,0] = "110";
            s1[1,1,1] = "111";
            s1[1,1,2] = "112";

            o1 = new object[2,2];
            o1[0,0] = 0;
            o1[0,1] = "01";
            o1[1,0] = (float)10;
            o1[1,1] = (double)1.1;
            Tester.store(this);
        }

        public void test() {
            ArrayNOrder ano = (ArrayNOrder)Tester.getOne(this);
            ano.check();
        }
    
        public void check(){
            Tester.ensure(s1[0,0,0].Equals("000"));
            Tester.ensure(s1[0,0,1].Equals("001"));
            Tester.ensure(s1[0,0,2].Equals("002"));
            Tester.ensure(s1[0,1,0].Equals("010"));
            Tester.ensure(s1[0,1,1].Equals("011"));
            Tester.ensure(s1[0,1,2].Equals("012"));
            Tester.ensure(s1[1,0,0].Equals("100"));
            Tester.ensure(s1[1,0,1].Equals("101"));
            Tester.ensure(s1[1,0,2].Equals("102"));
            Tester.ensure(s1[1,1,0].Equals("110"));
            Tester.ensure(s1[1,1,1].Equals("111"));
            Tester.ensure(s1[1,1,2].Equals("112"));
            Tester.ensure(o1[0,0].Equals(0));
            Tester.ensure(o1[0,1].Equals("01"));
            Tester.ensure(o1[1,0].Equals((float)10));
            Tester.ensure(o1[1,1].Equals((double)1.1));
        }
    
    }
}
