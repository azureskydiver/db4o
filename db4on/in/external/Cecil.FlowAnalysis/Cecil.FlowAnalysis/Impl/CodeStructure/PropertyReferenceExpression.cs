using Mono.Cecil;
using Mono.Cecil.Cil;
using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.Impl.CodeStructure
{
	internal class PropertyReferenceExpression : IPropertyReferenceExpression
	{
		IExpression _target;
		IPropertyReference _property;

		public PropertyReferenceExpression(IExpression target, IPropertyReference property)
		{
			_target = target;
			_property = property;
		}

		public IExpression Target
		{
			get	{ return _target; }
		}

		public IPropertyReference Property
		{
			get	{ return _property; }
		}

		public CodeElementType CodeElementType
		{
			get { return CodeElementType.PropertyReferenceExpression; } 
		}

		public void Accept(ICodeStructureVisitor visitor)
		{
			visitor.Visit(this);
		}
	}
}
