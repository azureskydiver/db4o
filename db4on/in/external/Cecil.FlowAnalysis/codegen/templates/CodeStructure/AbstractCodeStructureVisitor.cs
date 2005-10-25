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
<%
	for node in model.GetVisitableNodes():
%>
		public virtual void Visit(I${node.Name} node)
		{
<%
		for field in model.GetVisitableFields(node):
%>			Visit(node.${field.Name});
<%
		end
%>		}
<%
	end
%>	}
}
