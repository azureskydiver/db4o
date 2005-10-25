using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface I${node.Name} : ICollection
	{
		${model.GetCollectionItemType(node)} this[int index] { get; }
	}
}
