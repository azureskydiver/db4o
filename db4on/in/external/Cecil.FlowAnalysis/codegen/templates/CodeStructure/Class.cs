<%

fields = model.GetFields(node)

%>using Mono.Cecil;
using Mono.Cecil.Cil;
using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.Impl.CodeStructure
{
	internal class ${node.Name} : I${node.Name}
	{
<%	for field in fields:
%>		${GetFieldTypeName(field)} ${ToFieldName(field.Name)};
<%	end %>
<%
	args = join("${GetFieldTypeName(field)} ${ToParamName(field.Name)}" for field in fields, ", ")
%>		public ${node.Name}(${args})
		{
<%	for field in fields:
%>			${ToFieldName(field.Name)} = ${ToParamName(field.Name)};
<%	end
%>		}
<%
	for field in fields:
%>
		public ${GetFieldTypeName(field)} ${field.Name}
		{
			get	{ return ${ToFieldName(field.Name)}; }
		}
<%	end %>
		public CodeElementType CodeElementType
		{
			get { return CodeElementType.${node.Name}; } 
		}

		public void Accept(ICodeStructureVisitor visitor)
		{
			visitor.Visit(this);
		}
	}
}
