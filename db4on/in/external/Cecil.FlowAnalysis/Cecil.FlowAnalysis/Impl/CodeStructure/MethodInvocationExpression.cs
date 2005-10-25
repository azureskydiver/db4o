using Mono.Cecil;
using Mono.Cecil.Cil;
using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.Impl.CodeStructure
{
	internal class MethodInvocationExpression : IMethodInvocationExpression
	{
		IExpression _target;
		IExpressionCollection _arguments;

		public MethodInvocationExpression(IExpression target, IExpressionCollection arguments)
		{
			_target = target;
			_arguments = arguments;
		}

		public IExpression Target
		{
			get	{ return _target; }
		}

		public IExpressionCollection Arguments
		{
			get	{ return _arguments; }
		}

		public CodeElementType CodeElementType
		{
			get { return CodeElementType.MethodInvocationExpression; } 
		}

		public void Accept(ICodeStructureVisitor visitor)
		{
			visitor.Visit(this);
		}
	}
}
