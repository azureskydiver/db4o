using Mono.Cecil;
using Mono.Cecil.Cil;
using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.Impl.CodeStructure
{
	internal class LiteralExpression : ILiteralExpression
	{
		object _value;

		public LiteralExpression(object value)
		{
			_value = value;
		}

		public object Value
		{
			get	{ return _value; }
		}

		public CodeElementType CodeElementType
		{
			get { return CodeElementType.LiteralExpression; } 
		}

		public void Accept(ICodeStructureVisitor visitor)
		{
			visitor.Visit(this);
		}
	}
}
