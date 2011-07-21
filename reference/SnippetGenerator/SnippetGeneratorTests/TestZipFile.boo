namespace SnippetGeneratorTests

import System
import System.IO
import NUnit.Framework
import SnippetGenerator
import Ionic.Zip

[TestFixture]
class TestZipFile:
	zipFileGenerator as CodeZip
	tsCondition as TimeStampStorage
	public def constructor():
		pass

	[SetUp]
	def Setup():		
		zipFileGenerator = CodeZip()
		tsCondition = TimeStampStorage()

	
	[Test]
	def CheckZip():		
		snippetGenerator = CodeToSnippets("../../../SnippetGenerator/CodeSnippetTemplate.flsnp",zipFileGenerator,tsCondition)
		snippetGenerator.CreateCodeSnippets("../../TestFiles/zipTest","./zipTest","cs")
		assert File.Exists("./zipTest/ExampleCode-Test.cs.flsnp")
		assert File.Exists("./zipTest/Example-TestFiles-zipTest-cs.zip")
		assert File.Exists("./zipTest/includeMe/ExpectMe-Tada.cs.flsnp")
		assert File.Exists("./zipTest/includeMe/Example-zipTest-includeMe-cs.zip")
		assert not Directory.Exists("./zipTest/doNotDescent")

		zipFile = ZipFile.Read("./zipTest/Example-TestFiles-zipTest-cs.zip")
		for entry as string in zipFile.EntryFileNames:
			assert not entry.EndsWith(".snippet-generator.nodescend")
		
