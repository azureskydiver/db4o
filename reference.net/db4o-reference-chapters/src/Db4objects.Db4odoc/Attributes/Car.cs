/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
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
