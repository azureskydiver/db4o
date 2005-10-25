using System;
using System.Collections;
using Cecil.FlowAnalysis.ActionFlow;

namespace Cecil.FlowAnalysis.Impl.ActionFlow
{
	/// <summary>
	/// </summary>
	internal class ActionBlockCollection : CollectionBase, IActionBlockCollection
	{
		public ActionBlockCollection()
		{
		}

		public IActionBlock this[int index]
		{
			get
			{
				return (IActionBlock)InnerList[index];
			}
		}

		public int IndexOf(IActionBlock block)
		{
			if (null == block) throw new ArgumentNullException("block");
			return InnerList.IndexOf(block);
		}

		public void Add(IActionBlock block)
		{
			if (null == block) throw new ArgumentNullException("block");
			InnerList.Add(block);
		}

		public void Insert(int index, IActionBlock block)
		{
			if (null == block) throw new ArgumentNullException("block");
			InnerList.Insert(index, block);
		}

		public void Remove(IActionBlock block)
		{
			if (null == block) throw new ArgumentNullException("block");
			InnerList.Remove(block);
		}

		public IActionBlock[] ToArray()
		{
			return (IActionBlock[])InnerList.ToArray(typeof(IActionBlock));
		}
	}
}
