namespace CSharpConverter.Tests

import System
import System.Collections
import System.IO
import NUnit.Framework

[TestFixture]
class ProgramTestFixture:
	
	static SourceDir = Path.Combine(Path.GetTempPath(), "src")
	static TargetDir = Path.Combine(Path.GetTempPath(), "target")
	
	[SetUp]
	def SetUp():
		DeleteIfExists(SourceDir)
		DeleteIfExists(TargetDir)
		Directory.CreateDirectory(SourceDir)
		CreateSourceFile("Foo.cs", """
public class Foo {
	public void methodOne() {
		// comments are preserved
		methodTwo();
	}
	
	public void methodTwo() {
	}
}		""")

		CreateSourceFile("Bar.cs", """
public class Bar {
	public void emptyMethod() {
	}
	public string propertiesDontChange {
		get { return null; }
	}
}		""")

	[Test]
	def CSharpPascalCase():
		args = (SourceDir, TargetDir)
		CSharpConverter.Program.Main(args)
		AssertTargetFile("Foo.cs", """
public class Foo
{
	public void MethodOne()
	{
		// comments are preserved
		MethodTwo();
	}

	public void MethodTwo()
	{
	}

}		""")
		AssertTargetFile("Bar.cs", """
public class Bar
{
	public void EmptyMethod()
	{
	}

	public string propertiesDontChange {
		get
		{
			return null;
		}
	}

}		""")
		
	[Test]
	def VBNetPascalCase():
		args = ("-vb", SourceDir, TargetDir)
		CSharpConverter.Program.Main(args)
		AssertTargetFile("Foo.vb", """
Public Class Foo
	Public Sub MethodOne()
		' comments are preserved
		MethodTwo()
	End Sub

	Public Sub MethodTwo()
	End Sub

End Class""")

		AssertTargetFile("Bar.vb", """
Public Class Bar
	Public Sub EmptyMethod()
	End Sub

	Public ReadOnly Property propertiesDontChange() As String
		Get
			Return Nothing
		End Get
	End Property

End Class""")
		
	def DeleteIfExists(dir as string):
		return if not Directory.Exists(dir)
		Directory.Delete(dir, true)
		
	def CreateSourceFile(fname as string, contents as string):
		Useful.IO.TextFile.WriteFile(Path.Combine(SourceDir, fname), contents)
		
	def AssertTargetFile(fname as string, expected as string):
		actual = Useful.IO.TextFile.ReadFile(Path.Combine(TargetDir, fname))
		Assert.AreEqual(Normalize(expected), Normalize(actual))
		
	def Normalize(s as string):
		return s.Trim().Replace("\r\n", "\n")
