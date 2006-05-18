"""
Generates regression tests for FlowGraphFactory.CreateControlFlowGraph
by parsing FlowGraphExamples and outputting all of its methods
and their corresponding flow graphs.
"""
import System.IO
import Mono.Cecil
import Cecil.FlowAnalysis
import Cecil.FlowAnalysis.CecilUtilities
import Cecil.FlowAnalysis.Tests

def WriteMethod(writer as TextWriter, method as IMethodDefinition):
	
	returnType = CecilFormatter.FormatTypeReference(method.ReturnType.ReturnType)
	args = "${CecilFormatter.FormatTypeReference(p.ParameterType)} ${p.Name}" for p as IParameterReference in method.Parameters
	
	writer.WriteLine(""".assembly TestCase {}

.class public auto ansi beforefieldinit TestCase
       extends [mscorlib]System.Object
{
	.method public hidebysig static ${returnType} Main(${join(args, ', ')}) cil managed
	{""")
	
	for i in method.Body.Instructions:
		writer.WriteLine("\t\t${CecilFormatter.FormatInstruction(i)}")
		
	writer.WriteLine("""	}
}""")

def WriteCFG(writer as TextWriter, method as IMethodDefinition):
	cfg = FlowGraphFactory.CreateControlFlowGraph(method)
	AbstractControlFlowTestFixture.FormatControlFlowGraph(writer, cfg)

def WriteTestFixture(testcases):
	for test in testcases:
		print """
	[Test]
	public void ${test}()
	{
		RunTestCase("${test}");
	}"""

if len(argv) != 2:
	print "gen-ControlFlowGraphRegression <assembly> <typename>"
	return

assemblyPath, typeName = argv

assembly = AssemblyFactory.GetAssembly(assemblyPath)
examples = assembly.MainModule.Types[typeName]

generated = []
for method as IMethodDefinition in examples.Methods:
	fname = "testcases/FlowAnalysis/${method.Name}.il"
	if File.Exists(fname):
		print fname, "already exists, skipping..."
		continue

	generated.Add(method.Name)
	using writer=StreamWriter(fname):
		WriteMethod(writer, method)
	fname = "testcases/FlowAnalysis/${method.Name}-cfg.txt"
	using writer=StreamWriter(fname):
		WriteCFG(writer, method)
		
WriteTestFixture(generated)
