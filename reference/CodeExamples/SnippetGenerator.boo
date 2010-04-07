import System.IO



class SnippetGenerator:
	final SnippetPrefix = "#snippet:"
	final SnippetEnd = "#endsnippet"
	final Template as string
	
	def constructor(template as string):
		self.Template = template

	def HandleCodeFile(file as FileInfo, snippetDir as DirectoryInfo):
		allLines = File.ReadAllLines(file.FullName)
		inRecordingState = false
		snippetName = ""
		snippetText = ""
		for line in allLines:
			if IsSnippetStart(line):
				snippetName = ExtractName(line)
				inRecordingState = true
				snippetText = ""
			elif IsSnippetEnd(line):
				WriteSnippet(snippetText, file.Name,snippetName, snippetDir)
			elif inRecordingState:
				snippetText += line
				snippetText += "\n"
							
	def ExtractName(line as string):
		return line.Substring(line.IndexOf(SnippetPrefix)+SnippetPrefix.Length).Replace(' ','-')
			
	def IsSnippetStart(line as string):
		return line.Contains(SnippetPrefix)
		
		
	def IsSnippetEnd(line as string):
		return line.Contains(SnippetEnd)
		
	def WriteSnippet(snippetText as string, originalFileName as string,snippetName as string, location as DirectoryInfo):
		fileName = Path.GetFileNameWithoutExtension(originalFileName) + snippetName + Path.GetExtension(originalFileName) + ".flsnp"
		filePath = Path.Combine(location.FullName,fileName)
		content = string.Format(Template,snippetText)
		File.WriteAllText(filePath,content)
		
		



class CodeToSnippets:
	final _SnippetGeneration as SnippetGenerator

	def constructor(templatePath as string):
		_SnippetGeneration = SnippetGenerator(File.ReadAllText(templatePath))

	def cleanDirectory(sourceDir as DirectoryInfo):
		for file in sourceDir.GetFiles():
			file.Delete()

	def handleSubDirectory(sourceDir as DirectoryInfo, snippetDir as DirectoryInfo):
		directoryInSnippets=snippetDir.CreateSubdirectory(sourceDir.Name)
		cleanDirectory(directoryInSnippets)
		createCodeSnippets(sourceDir,directoryInSnippets)


		
	def createCodeSnippets(sourceDir as DirectoryInfo, snippetDir as DirectoryInfo):
		for file in sourceDir.GetFiles():
			_SnippetGeneration.HandleCodeFile(file,snippetDir)
		for dir in sourceDir.GetDirectories():
			handleSubDirectory(dir,snippetDir)

	def createCodeSnippets(sourceDir as string, snippetDir as string):
		createCodeSnippets(Directory.CreateDirectory(sourceDir),Directory.CreateDirectory(snippetDir))


snippetGenerator = CodeToSnippets("./CodeSnippetTemplate.flsnp")
snippetGenerator.createCodeSnippets("java/src/com/db4odoc","../Flare/Content/CodeExamples")
snippetGenerator.createCodeSnippets("dotNet/CSharpExamples/Code","../Flare/Content/CodeExamples")

snippetGeneratorForVB = CodeToSnippets("./CodeSnippetTemplateForVB.flsnp")
snippetGeneratorForVB.createCodeSnippets("dotNet/VisualBasicExamples/Code","../Flare/Content/CodeExamples")
