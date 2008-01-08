using System;
using System.Collections.Generic;

namespace UGMan6000.Extensible
{
	class UnitOfWork
	{	
		public static void Changed(object o)
		{
			List<object> current = _current;
			if (current == null) return;
			if (current.Contains(o)) return;
			current.Add(o);
		}
		
		public delegate void Block();

		public static List<object> Run(Block code)
		{	
			List<object> changeList = new List<object>();
			List<object> saved = _current;
			_current = changeList;
			try
			{
				code();
			}
			finally
			{
				_current = saved;
			}
			return changeList;
		}

		[ThreadStatic] private static List<object> _current;
	}
}
