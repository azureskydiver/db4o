/* Copyright (C) 2009 db4objects Inc.   http://www.db4o.com */

namespace OManager.BusinessLayer.Config
{
	internal class PathContainer
	{
		public PathContainer(ISearchPath searchPath)
		{
			_searchPath = searchPath;
		}

		public ISearchPath SearchPath
		{
			get
			{
				return _searchPath;
			}

			set
			{
				_searchPath = value;	
			}
		}

		private ISearchPath _searchPath;
	}
}
