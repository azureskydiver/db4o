package com.db4o.cs.common.util;

import com.db4o.cs.server.Db4oServer;

/**
 * User: treeder
 * Date: Dec 1, 2006
 * Time: 6:06:50 PM
 */
public class Log {
	public static boolean debug = false;
	public static void print(String msg) {
		if(debug){
			System.out.println(msg);
		}
	}
}
