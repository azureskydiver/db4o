using System;
using Db4objects.Db4o.Config.Attributes;

namespace Db4objects.Db4odoc.Attributes
{
	public class Car
	{
		[Indexed]
		private string _model;
		private int _year;
	}
}
