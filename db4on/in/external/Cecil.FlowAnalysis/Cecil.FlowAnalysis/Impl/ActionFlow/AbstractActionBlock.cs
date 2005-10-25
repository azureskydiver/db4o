using System;
using Cecil.FlowAnalysis.ActionFlow;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.Impl.ActionFlow
{
	/// <summary>
	/// </summary>
	internal abstract class AbstractActionBlock : IActionBlock
	{
		ActionBlockCollection _predecessors = new ActionBlockCollection();
		private IInstruction _sourceInstruction;

		public AbstractActionBlock(IInstruction sourceInstruction)
		{
			if (null == sourceInstruction) throw new ArgumentNullException("sourceInstruction");
			_sourceInstruction = sourceInstruction;
		}

		public IInstruction SourceInstruction
		{
			get { return _sourceInstruction; }
		}

		public virtual IActionBlockCollection Predecessors
		{
			get { return _predecessors; }
		}

		public abstract ActionType ActionType
		{
			get;
		}

		public abstract IActionBlock[] Successors
		{
			get;
		}

		protected void AddAsPredecessorOf(IActionBlock block)
		{
			((ActionBlockCollection)block.Predecessors).Add(this);
		}

		public void RemovePredecessor(IActionBlock block)
		{
			_predecessors.Remove(block);
		}

		public abstract void ReplaceSuccessor(IActionBlock existing, IActionBlock newBlock);
	}
}
