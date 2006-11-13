/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.defragment;

public class Data {
		public int _id;
		public String _name;
		public Data _previous;
		public Data[] _payload;

		public Data(int id, String name, Data previous, Data[] payload) {
			this._id = id;
			this._name = name;
			this._previous = previous;
			this._payload = payload;
		}
		
		public String toString() {
			return _id+":"+_name+","+(_payload==null ? "-" : String.valueOf(_payload.length)+"["+_payload[1]+"]");
		}
	}