using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;
using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.Impl.CodeStructure
{
	internal class ExpressionCollection : CollectionBase, IExpressionCollection
	{
		public IExpression this[int index]
		{
			get { return (IExpression) InnerList[index]; }
		}

		public void Add(IExpression element)
		{
			if (element == null) throw new ArgumentNullException("element");
			InnerList.Add(element);
		}
		
		public void Insert(int index, IExpression element)
		{
			if (element == null) throw new ArgumentNullException("element");
			InnerList.Insert(index, element);
		}
	}
}
