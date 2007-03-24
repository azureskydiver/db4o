Imports System
Imports System.IO
Imports System.Collections
Imports Db4objects.Db4o
Namespace Db4objects.Db4odoc.SelectivePersistence

    Class TransientClassExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args As String())
            SaveObjects()
            RetrieveObjects()
        End Sub
        ' end Main

        Public Shared Sub SaveObjects()
            File.Delete(YapFileName)
            Dim oc As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                ' Save Test1 object with a NotStorable class field
                Dim test1 As Test1 = New Test1("Test1", New NotStorable)
                oc.Set(test1)
                ' Save Test2 object with a NotStorable class field
                Dim test2 As Test2 = New Test2("Test2", New NotStorable, test1)
                oc.Set(test2)
            Finally
                oc.Close()
            End Try
        End Sub
        ' end SaveObjects

        Public Shared Sub RetrieveObjects()
            Dim oc As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                ' retrieve the results and check if the NotStorable instances were saved
                Dim result As IList = oc.Get(Nothing)
                ListResult(result)
            Finally
                oc.Close()
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