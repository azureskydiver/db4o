namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface ICodeStructureVisitor
	{
		void Visit(IMethodInvocationExpression node);
		void Visit(IMethodReferenceExpression node);
		void Visit(ILiteralExpression node);
		void Visit(IUnaryExpression node);
		void Visit(IBinaryExpression node);
		void Visit(IAssignExpression node);
		void Visit(IArgumentReferenceExpression node);
		void Visit(IVariableReferenceExpression node);
		void Visit(IThisReferenceExpression node);
		void Visit(IFieldReferenceExpression node);
		void Visit(IPropertyReferenceExpression node);
		void Visit(IBlockStatement node);
		void Visit(IReturnStatement node);
	}
}
