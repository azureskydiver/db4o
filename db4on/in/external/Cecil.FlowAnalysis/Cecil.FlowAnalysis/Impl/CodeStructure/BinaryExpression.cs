using Mono.Cecil;
using Mono.Cecil.Cil;
using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.Impl.CodeStructure
{
	internal class BinaryExpression : IBinaryExpression
	{
		BinaryOperator _operator;
		IExpression _left;
		IExpression _right;

		public BinaryExpression(BinaryOperator operator_, IExpression left, IExpression right)
		{
			_operator = operator_;
			_left = left;
			_right = right;
		}

		public BinaryOperator Operator
		{
			get	{ return _operator; }
		}

		public IExpression Left
		{
			get	{ return _left; }
		}

		public IExpression Right
		{
			get	{ return _right; }
		}

		public CodeElementType CodeElementType
		{
			get { return CodeElementType.BinaryExpression; } 
		}

		public void Accept(ICodeStructureVisitor visitor)
		{
			visitor.Visit(this);
		}
	}
}
