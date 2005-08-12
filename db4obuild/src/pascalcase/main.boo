import System
import System.IO
import System.Text.RegularExpressions
import Useful.IO from "Boo.Lang.Useful"
import ICSharpCode.SharpRefactory.Parser
import ICSharpCode.SharpRefactory.Parser.AST
import ICSharpCode.SharpRefactory.PrettyPrinter

class PascalCasePrinter(PrettyPrintVisitor):
	def constructor(originalFileName as string):
		super(originalFileName)
		
	override def Visit(method as MethodDeclaration, data):
		method.Name = ToPascalCase(method.Name)
		return super(method, data)
		
	override def Visit(invocation as InvocationExpression, data):
		memberRef = invocation.TargetObject as FieldReferenceExpression
		if memberRef is not null:
			memberRef.FieldName = ToPascalCase(memberRef.FieldName)
		else:
			identifier = invocation.TargetObject as IdentifierExpression
			if identifier is not null:
				identifier.Identifier = ToPascalCase(identifier.Identifier)
		return super(invocation, data)
		
	def ToPascalCase(name as string):
		return name[:1].ToUpper() + name[1:]

def convert(fname as string):
	p = Parser()
	p.Parse(Lexer(FileReader(fname)))
	
	printer = PascalCasePrinter(fname)
	
	options = printer.PrettyPrintOptions
	options.PropertyGetBraceStyle = BraceStyle.NextLine
	options.PropertySetBraceStyle = BraceStyle.NextLine
	options.PropertyBraceStyle = BraceStyle.NextLine
	options.MethodBraceStyle = BraceStyle.NextLine
	options.PropertySetBraceStyle = BraceStyle.NextLine
	options.ConstructorBraceStyle = BraceStyle.NextLine
	# spaces read better in the docs
	options.IndentationChar = char(' ')
	options.IndentSize = 4
	options.TabSize = 4
	
	printer.Visit(p.compilationUnit, null)
	
	return printer.Text
	
def preprocess(fname as string, defines as (string)):
"""
SharpRefactory.Parser does not support preprocessor directives
which are used heavily in our source code.

Use Boo.Lang.Useful.IO.PreProcessor for the preprocessing
magic.
"""
	pp = PreProcessor()
	for define in defines:
		pp.Define(define)
	
	targetFile = Path.Combine(Path.GetTempPath(), Path.GetFileName(fname))
	using reader=File.OpenText(fname), writer=StreamWriter(targetFile):
		pp.Process(reader, writer)
	return targetFile
	
srcDir, targetDir = argv
defines = argv[2:]
for fname as string in listFiles(srcDir):
	if fname.EndsWith(".cs"):
		targetFile = Path.Combine(targetDir, fname[len(srcDir)+1:])
		// print fname, " --> ", targetFile
		Console.Write(".")
		Directory.CreateDirectory(Path.GetDirectoryName(targetFile))
		TextFile.WriteFile(targetFile, convert(preprocess(fname, defines)))
print
