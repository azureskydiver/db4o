/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using System;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.Enums
{
	class EnumExample 
	{
		public static IObjectSet FindOpenDoors(IObjectContainer container) 
		{
			return container.Get(new Door(DoorState.Open));
		}
	}
}
