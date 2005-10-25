using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface IBinaryExpression : IExpression
	{
		BinaryOperator Operator { get; }
		IExpression Left { get; }
		IExpression Right { get; }
	}
}
