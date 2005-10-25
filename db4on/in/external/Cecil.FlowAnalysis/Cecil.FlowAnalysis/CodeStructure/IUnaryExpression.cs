using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface IUnaryExpression : IExpression
	{
		UnaryOperator Operator { get; }
		IExpression Operand { get; }
	}
}
