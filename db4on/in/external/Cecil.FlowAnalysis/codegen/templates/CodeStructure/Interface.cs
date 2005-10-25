using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface ${node.Name} : ${join(node.BaseTypes, ', ')}
	{<%
	for field in model.GetFields(node): %>
		${field.Type} ${field.Name} { get; }<%
	end	%>
	}
}
