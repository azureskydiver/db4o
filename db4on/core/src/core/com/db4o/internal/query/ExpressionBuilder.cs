/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

namespace com.db4o.@internal.query {

	using System;
	using System.Reflection;

	using com.db4o.nativequery.expr;

	/// <summary>
	/// This is the base class for tools capable
	/// too create an expression tree from a method.
	/// </summary>
	public abstract class ExpressionBuilder {

        protected static ICachingStrategy _assemblyCachingStrategy = new SingleItemCachingStrategy();

        protected static ICachingStrategy _expressionCachingStrategy = new SingleItemCachingStrategy();

		public static ICachingStrategy AssemblyCachingStrategy
		{
			get
			{
				return _assemblyCachingStrategy;
			}

			set
			{
				if (null == value) throw new ArgumentNullException("AssemblyCachingStrategy");
				_assemblyCachingStrategy = value;
			}
		}

        public static ICachingStrategy ExpressionCachingStrategy
        {
            get
            {
                return _expressionCachingStrategy;
            }

            set
            {
                if (null == value) throw new ArgumentNullException("ExpressionCachingStrategy");
                _expressionCachingStrategy = value;
            }
        }

		/// <summary>
		/// Create an expression tree from a method
		/// </summary>
		/// <param name="method">A method</param>
		/// <returns>An expression Tree</returns>
		public abstract Expression FromMethod (MethodBase method);
	}
}
