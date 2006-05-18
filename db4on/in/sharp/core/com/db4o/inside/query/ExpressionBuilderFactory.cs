/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

namespace com.db4o.inside.query
{
	using System;
	using System.IO;
	using System.Configuration;
	using System.Reflection;

	public class ExpressionBuilderFactory
	{
		public static ExpressionBuilder CreateExpressionBuilder()
		{
			Type type = Type.GetType("Db4o.Tools.NativeQueries.QueryExpressionBuilder, Db4o.Tools", true);
			return (ExpressionBuilder)Activator.CreateInstance(type);
		}
	}
}
