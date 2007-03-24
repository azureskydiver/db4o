/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
using System;

namespace Db4objects.Db4odoc.marshal
{
    class Item
    {
		public int _one;
		
		public long _two;
		
		public int _three;
		
		public Item(int one, long two, int three) {
			_one = one;
			_two = two;
			_three = three;
			
		}

		public Item() {
			
		}
		
		public override string ToString() {
			return String.Format("{0:X}, {1:X}, {2:N}", _one, _two, _three);
		}
    }
}
