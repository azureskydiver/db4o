using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface IAssignExpression : IExpression
	{
		IExpression Target { get; }
		IExpression Expression { get; }
	}
}
