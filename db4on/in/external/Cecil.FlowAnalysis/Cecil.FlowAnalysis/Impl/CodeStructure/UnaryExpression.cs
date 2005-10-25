using Mono.Cecil;
using Mono.Cecil.Cil;
using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.Impl.CodeStructure
{
	internal class UnaryExpression : IUnaryExpression
	{
		UnaryOperator _operator;
		IExpression _operand;

		public UnaryExpression(UnaryOperator operator_, IExpression operand)
		{
			_operator = operator_;
			_operand = operand;
		}

		public UnaryOperator Operator
		{
			get	{ return _operator; }
		}

		public IExpression Operand
		{
			get	{ return _operand; }
		}

		public CodeElementType CodeElementType
		{
			get { return CodeElementType.UnaryExpression; } 
		}

		public void Accept(ICodeStructureVisitor visitor)
		{
			visitor.Visit(this);
		}
	}
}
