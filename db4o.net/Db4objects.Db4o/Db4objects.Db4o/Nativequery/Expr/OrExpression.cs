/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using Db4objects.Db4o.Nativequery.Expr;

namespace Db4objects.Db4o.Nativequery.Expr
{
	public class OrExpression : BinaryExpression
	{
		public OrExpression(IExpression left, IExpression right) : base(left, right)
		{
		}

		public override string ToString()
		{
			return "(" + _left + ")||(" + _right + ")";
		}

		public override void Accept(IExpressionVisitor visitor)
		{
			visitor.Visit(this);
		}
	}
}
