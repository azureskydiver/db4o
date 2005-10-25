<%

itemType = model.GetCollectionItemType(node)

%>using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;
using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.Impl.CodeStructure
{
	internal class ${node.Name} : CollectionBase, I${node.Name}
	{
		public ${itemType} this[int index]
		{
			get { return (${itemType}) InnerList[index]; }
		}

		public void Add(${itemType} element)
		{
			if (element == null) throw new ArgumentNullException("element");
			InnerList.Add(element);
		}
		
		public void Insert(int index, ${itemType} element)
		{
			if (element == null) throw new ArgumentNullException("element");
			InnerList.Insert(index, element);
		}
	}
}
