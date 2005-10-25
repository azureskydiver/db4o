using Mono.Cecil;
using Mono.Cecil.Cil;
using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.Impl.CodeStructure
{
	internal class VariableReferenceExpression : IVariableReferenceExpression
	{
		IVariableReference _variable;

		public VariableReferenceExpression(IVariableReference variable)
		{
			_variable = variable;
		}

		public IVariableReference Variable
		{
			get	{ return _variable; }
		}

		public CodeElementType CodeElementType
		{
			get { return CodeElementType.VariableReferenceExpression; } 
		}

		public void Accept(ICodeStructureVisitor visitor)
		{
			visitor.Visit(this);
		}
	}
}
