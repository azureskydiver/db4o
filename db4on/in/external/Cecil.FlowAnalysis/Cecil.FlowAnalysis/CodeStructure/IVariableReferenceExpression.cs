using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface IVariableReferenceExpression : IExpression
	{
		IVariableReference Variable { get; }
	}
}
