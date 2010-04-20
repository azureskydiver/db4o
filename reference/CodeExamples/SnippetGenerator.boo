import System.IO
import Ionic.Zip
import System
import System.Collections.Generic.IEnumerable
import System.Linq.Enumerable from 'System.Core'
import System.Linq from 'System.Core'
import System.Web from 'System.Web'

# Generate snippets out of the source-code

class CodeZip:
	final allreadyGenerated = {}
	
	def ZipDirectory(directoryToZip as DirectoryInfo, directoryToStore as DirectoryInfo, name as string):
		fileName = directoryToStore.FullName+"/"+ BuildName(directoryToZip)+"-${name}.zip"
		if allreadyGenerated.Contains(directoryToZip.FullName):
			return allreadyGenerated[directoryToZip.FullName]
		allreadyGenerated.Add(directoryToZip.FullName,Path.GetFileName(fileName));
		zipFile= ZipFile(fileName)
		zipFile.AddDirectory(directoryToZip.FullName);
		zipFile.Save();
		zipFile.Dispose();
		return Path.GetFileName(fileName);
		
	def BuildName(directoryToZip as DirectoryInfo):
		return "Example-"+directoryToZip.Parent.Name +"-"+ directoryToZip.Name
	


class SnippetGenerator:
	final SnippetPrefix = "#example:"
	final SnippetEnd = "#end example"
	final Template as string
	final zipGenerator  as CodeZip
	
	final languageToCondition = {"java":"Primary.Java","csharp":"Primary.c#","vb":"Primary.VB.NET"}
	
	def constructor(template as string, zipGenerator as CodeZip):
		self.Template = template
		self.zipGenerator = zipGenerator

	def HandleCodeFile(file as FileInfo, snippetDir as DirectoryInfo, language as string):
		allLines = File.ReadAllLines(file.FullName)
		inRecordingState = false
		snippetName = ""
		snippetText = []		
		for line in allLines:
			if IsSnippetStart(line):
				snippetName = ExtractName(line)
				inRecordingState = true
				snippetText = []
			elif IsSnippetEnd(line):
				zipFile = zipGenerator.ZipDirectory(file.Directory,snippetDir,language)
				normalizedCodeLines = NormalizeText(array(string,snippetText))
				normalizedCode = join(normalizedCodeLines,"\n")
				filePath = SnippetName(file.Name,snippetName.Replace(' ','-'),snippetDir.FullName)
				snippetTitle = file.Name+":${snippetName}"
				WriteSnippet(normalizedCode, filePath, zipFile,language,snippetTitle)
			elif inRecordingState:
				snippetText.Add(line)
							
	def ExtractName(line as string):
		return line.Substring(line.IndexOf(SnippetPrefix)+SnippetPrefix.Length)
			
	def IsSnippetStart(line as string):
		return line.Contains(SnippetPrefix)
		
		
	def IsSnippetEnd(line as string):
		return line.Contains(SnippetEnd)
		
	def WriteSnippet(snippetText as string, filePath as string, zipFile as string, language as string, snippetTitle as string):
		snippetTextHtml = HttpUtility.HtmlEncode(snippetText)
		content = string.Format(Template,snippetTextHtml,zipFile,languageToCondition[language],snippetTitle)
		File.WriteAllText(filePath,content)
	
	def SnippetName(originalFileName as string, snippetName as string, location as string):
		fileName = Path.GetFileNameWithoutExtension(originalFileName) + snippetName + Path.GetExtension(originalFileName) + ".flsnp"
		return Path.Combine(location,fileName)
	
	def NormalizeText(text  as (string)):
		numberOfIndent = int.MaxValue
		for line in text:
			if not string.IsNullOrEmpty(line):
				currentLineNumber = line.TakeWhile({c | c == char(' ') or c==char('\t')}).Count()
				numberOfIndent = Math.Min(currentLineNumber,numberOfIndent)
		result = []
		for  line in text:
			if numberOfIndent <= line.Length:
				result.Add(line.Substring(numberOfIndent))
			else:
				result.Add(line)
		return array(string,result)



class CodeToSnippets:
	final _SnippetGeneration as SnippetGenerator

	def constructor(templatePath as string, zipGenerator as CodeZip):
		_SnippetGeneration = SnippetGenerator(File.ReadAllText(templatePath),zipGenerator)

	def HandleSubDirectory(sourceDir as DirectoryInfo, snippetDir as DirectoryInfo, language as string):
		directoryInSnippets=snippetDir.CreateSubdirectory(sourceDir.Name)
		CreateCodeSnippets(sourceDir,directoryInSnippets,language)

		
	def CreateCodeSnippets(sourceDir as DirectoryInfo, snippetDir as DirectoryInfo, language as string):
		for file in sourceDir.GetFiles():
			_SnippetGeneration.HandleCodeFile(file,snippetDir,language)
		for dir in sourceDir.GetDirectories():
			HandleSubDirectory(dir,snippetDir,language)

	def CreateCodeSnippets(sourceDir as string, snippetDir as string, language as string):
		CreateCodeSnippets(Directory.CreateDirectory(sourceDir),Directory.CreateDirectory(snippetDir),language)

class SnippetAggregator:
	final Template as string
			
	def constructor(template as string):
		Template = template	
		
	def CreateAggregateSnippet(name as string, files as List):
		linkString = ""
		for file as FileInfo in files:
			fileName = file.Name;
			linkString += 	"""<MadCap:snippetBlock src="${fileName}"/>"""
		filePath = cast(FileInfo, files[0]).Directory.FullName +"\\${name}.all.flsnp"
		content = string.Format(Template,linkString)
		File.WriteAllText(filePath,content)

			
	def BuildAggregateSnippets(directory as DirectoryInfo):
		filesToConsider = {}
		for file in directory.GetFiles("*.flsnp"):
			name = file.Name.Split(char('.'))[0]
			if(filesToConsider.Contains(name)):
				(filesToConsider[name] as List).Add(file)
			else:
				filesToConsider[name] = [file]
		for	entry in filesToConsider:
			CreateAggregateSnippet(entry.Key,entry.Value as List)
		for	subDir in directory.GetDirectories():
			BuildAggregateSnippets(subDir)
			
	def BuildAggregateSnippets(directory as string, snippteTemplate as string):
		BuildAggregateSnippets(Directory.CreateDirectory(directory))
	
Directory.CreateDirectory("../Flare/Content/CodeExamples").Delete(true)
		
zipFileGenerator = CodeZip()

snippetGenerator = CodeToSnippets("./CodeSnippetTemplate.flsnp",zipFileGenerator)
snippetGenerator.CreateCodeSnippets("java/src/com/db4odoc","../Flare/Content/CodeExamples","java")
snippetGenerator.CreateCodeSnippets("dotNet/CSharpExamples/Code","../Flare/Content/CodeExamples","csharp")

snippetGeneratorForVB = CodeToSnippets("./CodeSnippetTemplateForVB.flsnp",zipFileGenerator)
snippetGeneratorForVB.CreateCodeSnippets("dotNet/VisualBasicExamples/Code","../Flare/Content/CodeExamples","vb")

aggreatedSnippet = SnippetAggregator(File.ReadAllText("./AggregateSnippetTemplate.flsnp"))
aggreatedSnippet.BuildAggregateSnippets(Directory.CreateDirectory("../Flare/Content/CodeExamples"))
