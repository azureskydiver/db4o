namespace SnippetGenerator

import System
import System.IO
import ColorCode;

class SyntaxHighLighter:
	colorizer = CodeColorizer()
	countPrefix = """<div style="color:Black;background-color:White;"><pre>""".Length
	countSuffix = """</pre></div>""".Length


	def Translate(textToTranslate as string, fileName as string):
		language = TypeOfFile(Path.GetExtension(fileName))
		if language != null: 
			return StripDiv(colorizer.Colorize(textToTranslate,language ))			
		return textToTranslate 

	def StripDiv(code as string):
		length = code.Length-countPrefix-countSuffix
		return code.Substring(countPrefix,length)

	def TypeOfFile(fileEnding as string):
		if ".java" == fileEnding:
			return Languages.Java
		if ".cs" == fileEnding:
			return Languages.CSharp
		if ".vb" == fileEnding:
			return Languages.VbDotNet
		if ".xml" == fileEnding:
			return Languages.Xml
		return null


	
