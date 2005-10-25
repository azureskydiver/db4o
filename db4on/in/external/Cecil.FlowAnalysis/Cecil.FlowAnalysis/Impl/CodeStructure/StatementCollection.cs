using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;
using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.Impl.CodeStructure
{
	internal class StatementCollection : CollectionBase, IStatementCollection
	{
		public IStatement this[int index]
		{
			get { return (IStatement) InnerList[index]; }
		}

		public void Add(IStatement element)
		{
			if (element == null) throw new ArgumentNullException("element");
			InnerList.Add(element);
		}
		
		public void Insert(int index, IStatement element)
		{
			if (element == null) throw new ArgumentNullException("element");
			InnerList.Insert(index, element);
		}
	}
}
