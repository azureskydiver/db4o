import System
import System.IO
import Useful.BooTemplate from Boo.Lang.Useful

class InstructionInfo:

	[getter(OpCodes)]
	_opcodes as (string)
	
	def constructor(line as string):
		_opcodes = array(formatOpCode(opcode) for opcode in /\s+/.Split(line.Trim()))
		
class CodeTemplate(AbstractTemplate):
	
	[property(Instructions)]
	_instructions as (InstructionInfo)
		
def formatOpCode(opcode as string):
	parts = /\./.Split(opcode)
	return join(capitalize(part) for part in parts, '_')
	
def capitalize(s as string):
	return s[:1].ToUpper() + s[1:]
	
def parse(fname as string):
	for line in File.OpenText(fname):
		yield InstructionInfo(line)
		
def applyTemplate(instructions as (InstructionInfo), fname as string):
	compiler = TemplateCompiler(TemplateBaseClass: CodeTemplate)
	result = compiler.CompileFile(Path.Combine("codegen/Templates/CecilUtilities", fname))
	assert 0 == len(result.Errors), result.Errors.ToString()

	templateType = result.GeneratedAssembly.GetType("Template")
	template as CodeTemplate = templateType()
	template.Instructions = instructions
	
	print fname
	using writer=StreamWriter(Path.Combine("Cecil.FlowAnalysis/CecilUtilities", fname)):
		template.Output = writer
		template.Execute()
		
instructions = array(parse("codegen/instructions.txt"))
applyTemplate(instructions, "InstructionDispatcher.cs")
applyTemplate(instructions, "IInstructionVisitor.cs")
applyTemplate(instructions, "AbstractInstructionVisitor.cs")
	
			
