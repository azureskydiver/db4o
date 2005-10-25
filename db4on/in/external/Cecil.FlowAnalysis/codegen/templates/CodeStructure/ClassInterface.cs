using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface I${node.Name} : ${join(node.BaseTypes, ', ')}
	{
<%
	for field in model.GetFields(node):		
%>		${GetFieldTypeName(field)} ${field.Name} { get; }
<%
	end	
%>	}
}
