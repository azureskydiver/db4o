using Mono.Cecil;
using Mono.Cecil.Cil;
using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.Impl.CodeStructure
{
	internal class FieldReferenceExpression : IFieldReferenceExpression
	{
		IExpression _target;
		IFieldReference _field;

		public FieldReferenceExpression(IExpression target, IFieldReference field)
		{
			_target = target;
			_field = field;
		}

		public IExpression Target
		{
			get	{ return _target; }
		}

		public IFieldReference Field
		{
			get	{ return _field; }
		}

		public CodeElementType CodeElementType
		{
			get { return CodeElementType.FieldReferenceExpression; } 
		}

		public void Accept(ICodeStructureVisitor visitor)
		{
			visitor.Visit(this);
		}
	}
}
