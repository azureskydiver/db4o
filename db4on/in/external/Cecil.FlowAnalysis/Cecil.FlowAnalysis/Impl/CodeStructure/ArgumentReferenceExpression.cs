using Mono.Cecil;
using Mono.Cecil.Cil;
using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.Impl.CodeStructure
{
	internal class ArgumentReferenceExpression : IArgumentReferenceExpression
	{
		IParameterReference _parameter;

		public ArgumentReferenceExpression(IParameterReference parameter)
		{
			_parameter = parameter;
		}

		public IParameterReference Parameter
		{
			get	{ return _parameter; }
		}

		public CodeElementType CodeElementType
		{
			get { return CodeElementType.ArgumentReferenceExpression; } 
		}

		public void Accept(ICodeStructureVisitor visitor)
		{
			visitor.Visit(this);
		}
	}
}
