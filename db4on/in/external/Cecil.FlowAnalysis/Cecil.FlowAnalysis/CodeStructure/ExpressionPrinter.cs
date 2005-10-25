using System;
using System.IO;
using Cecil.FlowAnalysis.CecilUtilities;
using Mono.Cecil;

namespace Cecil.FlowAnalysis.CodeStructure
{
	/// <summary>
	/// </summary>
	public class ExpressionPrinter : AbstractCodeStructureVisitor
	{
		public static string ToString(IExpression expression)
		{
			ExpressionPrinter printer = new ExpressionPrinter();
			expression.Accept(printer);
			return printer.Writer.ToString();
		}

		private TextWriter _writer;

		public ExpressionPrinter()
		{	
			_writer = new StringWriter();
		}

		public ExpressionPrinter(TextWriter writer)
		{
			if (null == writer) throw new ArgumentNullException("writer");
			_writer = writer;
		}

		public TextWriter Writer
		{
			get { return _writer; }
		}

		public override void Visit(IAssignExpression node)
		{
			Visit(node.Target);
			Write(" = ");
			Visit(node.Expression);
		}

		public override void Visit(IBinaryExpression node)
		{
			Write("(");
			Visit(node.Left);
			Write(" {0} ", ToString(node.Operator));
			Visit(node.Right);
			Write(")");
		}

		public override void Visit(IArgumentReferenceExpression node)
		{
			Write(node.Parameter.Name);
		}

		public override void Visit(IThisReferenceExpression node)
		{
			Write("this");
		}

		public override void Visit(IFieldReferenceExpression node)
		{
			Visit(node.Target);
			Write(".{0}", node.Field.Name);
		}

		public override void Visit(IMethodReferenceExpression node)
		{
			IMethodReference method = node.Method;
			if (null == node.Target)
			{	
				Write(CecilFormatter.FormatTypeReference(method.DeclaringType));
			}
			else
			{
				Visit(node.Target);
			}
			Write(".{0}", method.Name);
		}
		
		public override void Visit(IMethodInvocationExpression node)
		{
			Visit(node.Target);
			Write("(");
			for (int i=0; i<node.Arguments.Count; ++i)
			{
				if (i > 0) Write(", ");
				Visit(node.Arguments[i]);
			}
			Write(")");
		}

		public override void Visit(IVariableReferenceExpression node)
		{
			string name = "local" + node.Variable.Index;
			Write(name);
		}

		public override void Visit(ILiteralExpression node)
		{
			object value = node.Value;
			if (value is string)
			{
				Write("\"{0}\"", value);
				return;
			}

			Write(value == null ? "null" : value.ToString().ToLower());
		}

		public override void Visit(IUnaryExpression node)
		{
			Write("(");
			Write(ToString(node.Operator));
			Visit(node.Operand);
			Write(")");
		}

		private string ToString(UnaryOperator op)
		{
			switch (op)
			{
				case UnaryOperator.Not: return "!";

			}
			throw new ArgumentException(op.ToString(), "op");
		}

		private string ToString(BinaryOperator op)
		{
			switch (op)
			{
				case BinaryOperator.LogicalAnd: return "&&";
				case BinaryOperator.LogicalOr: return "||";
				case BinaryOperator.Multiply: return "*";
				case BinaryOperator.ValueEquality: return "==";
				case BinaryOperator.ValueInequality: return "!=";
				case BinaryOperator.LessThan: return "<";
				case BinaryOperator.LessThanOrEqual: return "<=";
				case BinaryOperator.GreaterThan: return ">";
				case BinaryOperator.GreaterThanOrEqual: return ">=";
			}
			throw new ArgumentException(op.ToString(), "op");
		}

		private void Write(string text)
		{
			_writer.Write(text);
		}

		private void Write(string format, params object[] args)
		{
			_writer.Write(format, args);
		}
	}
}
