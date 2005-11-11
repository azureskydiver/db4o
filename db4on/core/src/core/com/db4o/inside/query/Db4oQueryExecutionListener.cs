/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
namespace com.db4o.inside.query
{
	public interface Db4oQueryExecutionListener
	{
		void notifyQueryExecuted(object filter, string msg);
	}
}
