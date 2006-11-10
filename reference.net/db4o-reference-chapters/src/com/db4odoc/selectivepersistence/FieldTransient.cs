/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;

namespace Db4objects.Db4odoc.SelectivePersistence
{
	[AttributeUsage(AttributeTargets.Field)]
	public class FieldTransient: Attribute
	{
	}
}
