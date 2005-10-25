namespace Cecil.FlowAnalysis.CodeStructure
{
	public class AbstractCodeStructureVisitor : ICodeStructureVisitor
	{
		public virtual void Visit(ICodeElement node)
		{	
			if (null == node) return;
			node.Accept(this);
		}
		
		public virtual void Visit(System.Collections.ICollection collection)
		{	
			foreach (ICodeElement node in collection)
			{
				Visit(node);
			}
		}

		public virtual void Visit(IMethodInvocationExpression node)
		{
			Visit(node.Target);
			Visit(node.Arguments);
		}

		public virtual void Visit(IMethodReferenceExpression node)
		{
			Visit(node.Target);
		}

		public virtual void Visit(ILiteralExpression node)
		{
		}

		public virtual void Visit(IUnaryExpression node)
		{
			Visit(node.Operand);
		}

		public virtual void Visit(IBinaryExpression node)
		{
			Visit(node.Left);
			Visit(node.Right);
		}

		public virtual void Visit(IAssignExpression node)
		{
			Visit(node.Target);
			Visit(node.Expression);
		}

		public virtual void Visit(IArgumentReferenceExpression node)
		{
		}

		public virtual void Visit(IVariableReferenceExpression node)
		{
		}

		public virtual void Visit(IThisReferenceExpression node)
		{
		}

		public virtual void Visit(IFieldReferenceExpression node)
		{
			Visit(node.Target);
		}

		public virtual void Visit(IPropertyReferenceExpression node)
		{
			Visit(node.Target);
		}

		public virtual void Visit(IBlockStatement node)
		{
			Visit(node.Statements);
		}

		public virtual void Visit(IReturnStatement node)
		{
			Visit(node.Expression);
		}
	}
}
