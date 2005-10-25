using Mono.Cecil;
using Mono.Cecil.Cil;
using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.Impl.CodeStructure
{
	internal class ReturnStatement : IReturnStatement
	{
		IExpression _expression;

		public ReturnStatement(IExpression expression)
		{
			_expression = expression;
		}

		public IExpression Expression
		{
			get	{ return _expression; }
		}

		public CodeElementType CodeElementType
		{
			get { return CodeElementType.ReturnStatement; } 
		}

		public void Accept(ICodeStructureVisitor visitor)
		{
			visitor.Visit(this);
		}
	}
}
