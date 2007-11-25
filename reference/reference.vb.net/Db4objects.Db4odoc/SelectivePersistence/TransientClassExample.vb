' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System.IO
Imports System.Collections

Imports Db4objects.Db4o

Namespace Db4objects.Db4odoc.SelectivePersistence

    Class TransientClassExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args As String())
            SaveObjects()
            RetrieveObjects()
        End Sub
        ' end Main

        Public Shared Sub SaveObjects()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                ' Save Test1 object with a NotStorable class field
                Dim test1 As Test1 = New Test1("Test1", New NotStorable)
                container.Set(test1)
                ' Save Test2 object with a NotStorable class field
                Dim test2 As Test2 = New Test2("Test2", New NotStorable, test1)
                container.Set(test2)
            Finally
                container.Close()
            End Try
        End Sub
        ' end SaveObjects

        Public Shared Sub RetrieveObjects()
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                ' retrieve the results and check if the NotStorable instances were saved
                Dim result As IList = container.Get(Nothing)
                ListResult(result)
            Finally
                container.Close()
            End Try
        End Sub
        ' end RetrieveObjects

        Public Shared Sub ListResult(ByVal result As IList)
            Console.WriteLine(result.Count)
            Dim x As Integer = 0
            While x < result.Count
                Console.WriteLine(result(x))
                System.Math.Min(System.Threading.Interlocked.Increment(x), x - 1)
            End While
        End Sub
        ' end ListResult
    End Class
End Namespace