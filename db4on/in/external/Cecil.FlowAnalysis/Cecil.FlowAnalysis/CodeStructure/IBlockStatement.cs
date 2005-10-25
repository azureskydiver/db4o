using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface IBlockStatement : IStatement
	{
		IStatementCollection Statements { get; }
	}
}
