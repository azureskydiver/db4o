using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface IReturnStatement : IStatement
	{
		IExpression Expression { get; }
	}
}
