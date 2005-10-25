using Mono.Cecil;
using Mono.Cecil.Cil;
using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.Impl.CodeStructure
{
	internal class MethodReferenceExpression : IMethodReferenceExpression
	{
		IExpression _target;
		IMethodReference _method;

		public MethodReferenceExpression(IExpression target, IMethodReference method)
		{
			_target = target;
			_method = method;
		}

		public IExpression Target
		{
			get	{ return _target; }
		}

		public IMethodReference Method
		{
			get	{ return _method; }
		}

		public CodeElementType CodeElementType
		{
			get { return CodeElementType.MethodReferenceExpression; } 
		}

		public void Accept(ICodeStructureVisitor visitor)
		{
			visitor.Visit(this);
		}
	}
}
