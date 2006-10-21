using System;
using com.db4o.config.attributes;

namespace com.db4odoc.f1.attributes
{
	public class Car
	{
		[Indexed]
		private string _model;
		private int _year;
	}
}
