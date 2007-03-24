' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.ClassMapping

    Class MappingExample
        Private Const FileName As String = "test.db"

        Public Shared Sub Main(ByVal args As String())
            StoreObjects()
            RetrieveObjects()
        End Sub
        ' end Main

        Private Shared Sub StoreObjects()
            File.Delete(FileName)
            Dim container As IObjectContainer = Db4oFactory.OpenFile(FileName)
            Try
                Dim pilot As Pilot = New Pilot("Michael Schumacher", 100)
                container.Set(pilot)
                pilot = New Pilot("Rubens Barichello", 99)
                container.Set(pilot)
            Finally
                container.Close()
            End Try
        End Sub
        ' end StoreObjects

        Private Shared Sub RetrieveObjects()
            Db4oFactory.Configure.ObjectClass(GetType(Pilot)).ReadAs(GetType(PilotReplacement))
            Dim container As IObjectContainer = Db4oFactory.OpenFile(FileName)
            Try
                Dim query As IQuery = container.Query
                query.Constrain(GetType(PilotReplacement))
                Dim result As IObjectSet = query.Execute
                ListResult(result)
            Finally
                container.Close()
            End Try
        End Sub
        ' end RetrieveObjects

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            While result.HasNext
                Console.WriteLine(result.Next)
            End While
        End Sub
        ' end ListResult

    End Class
End Namespace