using System;
using System.Collections;
using System.IO;
using Cecil.FlowAnalysis.CecilUtilities;
using Cecil.FlowAnalysis.ControlFlow;
using Mono.Cecil;
using Mono.Cecil.Cil;
using NUnit.Framework;

namespace Cecil.FlowAnalysis.Tests
{
	public class AbstractControlFlowTestFixture : AbstractFlowAnalysisTestFixture
	{
		protected void RunTestCase(string name)
		{	
			IMethodDefinition method = LoadTestCaseMethod(name);
			IControlFlowGraph cfg = FlowGraphFactory.CreateControlFlowGraph(method);
			Assert.AreEqual(normalize(LoadExpectedControlFlowString(name)), normalize(ToString(cfg)));
		}

		public static string ToString(IControlFlowGraph cfg)
		{	
			StringWriter writer = new StringWriter();
			FormatControlFlowGraph(writer, cfg);
			return writer.ToString();
		}

		public static void FormatControlFlowGraph(TextWriter writer, IControlFlowGraph cfg)
		{
			int id = 1;
			foreach (IInstructionBlock block in cfg.Blocks)
			{
				writer.WriteLine("block {0}:", id);
				writer.WriteLine("\tbody:");
				foreach (IInstruction instruction in block)
				{
					writer.Write("\t\t");
					CecilFormatter.WriteInstruction(writer, instruction);
					writer.WriteLine();
				}
				IInstructionBlock[] successors = block.Successors;
				if (successors.Length > 0)
				{
					writer.WriteLine("\tsuccessors:");
					foreach (IInstructionBlock successor in successors)
					{
						writer.WriteLine("\t\tblock {0}", GetBlockId(cfg, successor));
					}
				}
				
				++id;
			}
		}

		private static int GetBlockId(IControlFlowGraph cfg, IInstructionBlock block)
		{
			return ((IList)cfg.Blocks).IndexOf(block) + 1;
		}

		private string LoadExpectedControlFlowString(string name)
		{
			return LoadTestCaseFile(name + "-cfg.txt");
		}
	}
}