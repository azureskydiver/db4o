/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o.config.attributes
{
	interface IDb4oAttribute
	{
		void Apply (object subject, ConfigurationIntrospector introspector);
	}
}
