/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
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
