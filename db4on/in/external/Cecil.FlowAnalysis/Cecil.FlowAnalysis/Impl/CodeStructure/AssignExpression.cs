using Mono.Cecil;
using Mono.Cecil.Cil;
using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.Impl.CodeStructure
{
	internal class AssignExpression : IAssignExpression
	{
		IExpression _target;
		IExpression _expression;

		public AssignExpression(IExpression target, IExpression expression)
		{
			_target = target;
			_expression = expression;
		}

		public IExpression Target
		{
			get	{ return _target; }
		}

		public IExpression Expression
		{
			get	{ return _expression; }
		}

		public CodeElementType CodeElementType
		{
			get { return CodeElementType.AssignExpression; } 
		}

		public void Accept(ICodeStructureVisitor visitor)
		{
			visitor.Visit(this);
		}
	}
}
