' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Imports System
Imports System.IO
Imports Db4objects.Db4o
Namespace Db4objects.Db4odoc.marshal

    Class CustomMarshallerExample
        Private Const DbFile As String = "test.db"
        Private Shared marshaller As ItemMarshaller = Nothing

        Public Shared Sub Main(ByVal args As String())
            ' store objects using standard mashaller
            StoreObjects()
            ' retrieve objects using standard marshaller
            RetrieveObjects()
            ' store and retrieve objects using the customized Item class marshaller
            ConfigureMarshaller()
            StoreObjects()
            RetrieveObjects()
        End Sub
        ' end Main

        Private Shared Sub ConfigureMarshaller()
            marshaller = New ItemMarshaller
            Db4oFactory.Configure.ObjectClass(GetType(Item)).MarshallWith(marshaller)
        End Sub
        ' end ConfigureMarshaller

        Private Shared Sub StoreObjects()
            File.Delete(DbFile)
            Dim container As IObjectContainer = Db4oFactory.OpenFile(DbFile)
            Try
                Dim item As Item
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim i As Integer = 0
                While i < 500000
                    item = New Item(&HFFAF, &HFFFFFFA, 120)
                    container.Set(item)
                    System.Math.Min(System.Threading.Interlocked.Increment(i), i - 1)
                End While
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                System.Console.WriteLine("Time to store the objects =" + diff.Milliseconds.ToString() + " ms")
            Finally
                container.Close()
            End Try
        End Sub
        ' end StoreObjects

        Private Shared Sub RetrieveObjects()
            Dim container As IObjectContainer = Db4oFactory.OpenFile(DbFile)
            Try
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim result As IObjectSet = container.Get(GetType(Item))
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                System.Console.WriteLine("Time elapsed for the query =" + diff.Milliseconds.ToString() + " ms")
                ListResult(result)
            Finally
                container.Close()
            End Try
        End Sub
        ' end RetrieveObjects

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            System.Console.WriteLine(result.Size)
            ' print only the first result
            If result.HasNext Then
                System.Console.WriteLine(result.Next)
            End If
        End Sub
        ' end ListResult

    End Class
End Namespace