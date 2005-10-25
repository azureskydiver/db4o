using Mono.Cecil;
using Mono.Cecil.Cil;
using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.Impl.CodeStructure
{
	internal class ThisReferenceExpression : IThisReferenceExpression
	{

		public ThisReferenceExpression()
		{
		}

		public CodeElementType CodeElementType
		{
			get { return CodeElementType.ThisReferenceExpression; } 
		}

		public void Accept(ICodeStructureVisitor visitor)
		{
			visitor.Visit(this);
		}
	}
}
