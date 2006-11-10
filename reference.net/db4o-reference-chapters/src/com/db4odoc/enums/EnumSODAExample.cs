/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using System;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Enums
{
	class EnumSODAExample 
	{
		public static IObjectSet FindOpenDoors(IObjectContainer container) 
		{
			IQuery query = container.Query();
			query.Constrain(typeof(Door));
			query.Descend("_state").Constrain(DoorState.Open);
			return query.Execute();
		}
	}
}
