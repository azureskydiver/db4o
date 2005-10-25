namespace Cecil.FlowAnalysis.CodeStructure
{
	using System;
	
	[Serializable]
	public enum CodeElementType
	{
<%
nodes = array(model.GetVisitableNodes())
last = nodes[-1]
separator = ","
for item in nodes:	
	separator = "" if item is last
%>		${item.Name}${separator}
<%
end
%>	}
}

