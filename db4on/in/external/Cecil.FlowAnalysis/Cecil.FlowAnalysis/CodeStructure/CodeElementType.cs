namespace Cecil.FlowAnalysis.CodeStructure
{
	using System;
	
	[Serializable]
	public enum CodeElementType
	{
		MethodInvocationExpression,
		MethodReferenceExpression,
		LiteralExpression,
		UnaryExpression,
		BinaryExpression,
		AssignExpression,
		ArgumentReferenceExpression,
		VariableReferenceExpression,
		ThisReferenceExpression,
		FieldReferenceExpression,
		PropertyReferenceExpression,
		BlockStatement,
		ReturnStatement
	}
}

