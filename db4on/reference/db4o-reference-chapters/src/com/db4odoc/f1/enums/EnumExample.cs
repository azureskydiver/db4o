/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using System;
using com.db4o;

namespace com.db4odoc.f1.enums
{
	class EnumExample 
	{
		public static ObjectSet FindOpenDoors(ObjectContainer container) 
		{
			return container.Get(new Door(DoorState.Open));
		}
	}
}
