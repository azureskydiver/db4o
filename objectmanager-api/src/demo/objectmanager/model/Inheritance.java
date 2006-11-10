/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package demo.objectmanager.model;


/**
 * @exclude
 */
public class Inheritance {
	
	
	public static class I0 {
		
		public String s0;
		
	}
	
	public static class I1 extends I0 {
		
		public String s1;
		
	}
	
	public static class I2 extends I1 {
		
		public String s2;
		
	}
	
	public static Object forDemo(){
		I2 i2 = new I2();
		i2.s0 = "0";
		i2.s1 = "1";
		i2.s2 = "2";
		return i2;
	}

}
