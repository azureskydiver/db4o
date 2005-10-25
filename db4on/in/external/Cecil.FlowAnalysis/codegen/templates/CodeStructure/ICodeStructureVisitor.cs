namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface ICodeStructureVisitor
	{
<%
	for node in model.GetVisitableNodes():
%>		void Visit(I${node.Name} node);
<%
	end
%>	}
}
