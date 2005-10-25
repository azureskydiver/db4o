using System;
using Cecil.FlowAnalysis.ActionFlow;
using Cecil.FlowAnalysis.ControlFlow;

namespace Cecil.FlowAnalysis.Impl.ActionFlow
{
	internal class ActionFlowGraph : IActionFlowGraph
	{
		private ActionBlockCollection _blocks;
		private IControlFlowGraph _cfg;

		public ActionFlowGraph(IControlFlowGraph cfg, ActionBlockCollection blocks)
		{	
			if (null == cfg) throw new ArgumentNullException("cfg");
			if (null == blocks) throw new ArgumentNullException("blocks");

			_cfg = cfg;
			_blocks = blocks;
		}

		public IControlFlowGraph ControlFlowGraph
		{
			get { return _cfg; }
		}

		public IActionBlockCollection Blocks
		{
			get { return _blocks; }
		}

		public bool IsBranchTarget(IActionBlock block)
		{
			if (null == block) throw new ArgumentNullException("block");
			foreach (IActionBlock p in block.Predecessors)
			{
				switch (p.ActionType)
				{
					case ActionType.Branch:
						IBranchActionBlock br = (IBranchActionBlock) p;
						if (br.Target == block) return true;
						break;

					case ActionType.ConditionalBranch:
						IConditionalBranchActionBlock cbr = (IConditionalBranchActionBlock) p;
						if (cbr.Then == block) return true;
						break;
				}
			}
			return false;
		}

		public void ReplaceAt(int index, IActionBlock block)
		{	
			if (null == block) throw new ArgumentNullException("block");

			IActionBlock existing = _blocks[index];
			foreach (AbstractActionBlock p in existing.Predecessors.ToArray())
			{
				p.ReplaceSuccessor(existing, block);
			}
			Remove(existing);
			_blocks.Insert(index, block);
		}

		private void Remove(IActionBlock block)
		{
			foreach (AbstractActionBlock s in block.Successors)
			{
				s.RemovePredecessor(block);
				if (0 == s.Predecessors.Count)
				{
					Remove(s);
				}
			}
			_blocks.Remove(block);
		}
	}
}