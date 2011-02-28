/* Copyright (C) 2010 Versant Inc.  http://www.db4o.com */
using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;

namespace Db4objects.Db4o.Linq.Internals
{
	internal class PartialSelectPlaceHolder<TSource>
	{
		protected PartialSelectPlaceHolder(Db4oQuery<TSource> parent)
		{
			_parent = parent;
		}

		public Db4oQuery<TSource> Parent
		{
			get
			{
				return _parent;
			}

			set
			{
				_parent = value;
			}
		}

		private Db4oQuery<TSource> _parent;
	}

	internal class SelectPlaceHolder<TSource, TRet> : PartialSelectPlaceHolder<TSource>, IDb4oLinqQuery<TRet>
	{
		public SelectPlaceHolder(Db4oQuery<TSource> parent, Func<TSource, TRet> selector) : base(parent)
		{
			_selector = selector;
		}

		public IEnumerator<TRet> GetEnumerator()
		{
			return Enumerable.Select(Parent, _selector).GetEnumerator();
		}

		IEnumerator IEnumerable.GetEnumerator()
		{
			return GetEnumerator();
		}
		
		private readonly Func<TSource, TRet> _selector;
	}
}
