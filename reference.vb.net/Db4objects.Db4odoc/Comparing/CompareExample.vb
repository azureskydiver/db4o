' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query
Imports Db4objects.Db4o.Config

Namespace Db4objects.Db4odoc.Comparing

    Class CompareExample

        Private Const FileName As String = "example.db"

        Public Shared Sub Main(ByVal args As String())
            Configure()
            StoreRecords()
            CheckRecords()
        End Sub
        ' end Main

        Public Shared Sub Configure()
            Db4oFactory.Configure.ObjectClass(GetType(MyString)).Compare(New MyStringAttribute)
        End Sub
        ' end Configure

        Public Shared Sub StoreRecords()
            File.Delete(FileName)
            Dim container As IObjectContainer = Db4oFactory.OpenFile(FileName)
            Try
                Dim record As Record = New Record("Michael Schumacher, points: 100")
                container.Set(record)
                record = New Record("Rubens Barrichello, points: 98")
                container.Set(record)
                record = New Record("Kimi Raikonnen, points: 55")
                container.Set(record)
            Finally
                container.Close()
            End Try
        End Sub
        ' end StoreRecords

        Public Shared Sub CheckRecords()
            Dim container As IObjectContainer = Db4oFactory.OpenFile(FileName)
            Try
                Dim q As IQuery = container.Query
                q.Constrain(New Record("Rubens"))
                q.Descend("_record").Constraints.Contains()
                Dim result As IObjectSet = q.Execute
                ListResult(result)
            Finally
                container.Close()
            End Try
        End Sub
        ' end CheckRecords

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            System.Console.WriteLine(result.Size)
            While result.HasNext
                System.Console.WriteLine(result.Next)
            End While
        End Sub
        ' end ListResult

        Private Class MyStringAttribute
            Implements IObjectAttribute

            Public Function Attribute(ByVal original As Object) As Object Implements IObjectAttribute.Attribute
                If TypeOf original Is MyString Then
                    Return CType(original, MyString).ToString
                End If
                Return original
            End Function
        End Class
        ' end MyStringAttribute

    End Class
End Namespace
