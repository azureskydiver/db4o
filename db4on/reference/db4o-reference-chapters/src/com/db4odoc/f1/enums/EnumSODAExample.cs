/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using System;
using com.db4o;
using com.db4o.query;

namespace com.db4odoc.f1.enums
{
	class EnumSODAExample 
	{
		public static ObjectSet FindOpenDoors(ObjectContainer container) 
		{
			Query query = container.Query();
			query.Constrain(typeof(Door));
			query.Descend("_state").Constrain(DoorState.Open);
			return query.Execute();
		}
	}
}
