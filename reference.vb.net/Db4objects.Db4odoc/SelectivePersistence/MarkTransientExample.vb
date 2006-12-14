' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports System.Collections
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.SelectivePersistence
    Public Class MarkTransientExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args() As String)
            ConfigureTransient()
            SaveObjects()
            RetrieveObjects()
        End Sub
        ' end Main

        Public Shared Sub ConfigureTransient()
            Db4oFactory.Configure().MarkTransient("Db4objects.Db4odoc.SelectivePersistence.FieldTransient")
        End Sub
        ' end ConfigureTransient

        Public Shared Sub SaveObjects()
            File.Delete(YapFileName)
            Dim oc As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim test As Test = New Test("Transient string", "Persistent string")
                oc.Set(test)
                Dim testc As TestCusomized = New TestCusomized("Transient string", "Persistent string")
                oc.Set(testc)
            Finally
                oc.Close()
            End Try
        End Sub
        ' end SaveObjects

        Public Shared Sub RetrieveObjects()
            Dim oc As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim query As IQuery = oc.Query()
                query.Constrain(GetType(Object))
                Dim result As IList = query.Execute()
                ListResult(result)
            Finally
                oc.Close()
            End Try
        End Sub
        ' end RetrieveObjects

        Public Shared Sub ListResult(ByVal result As IList)
            Console.WriteLine(result.Count)
            Dim x As Integer
            For x = 0 To result.Count - 1 Step x + 1
                Console.WriteLine(result(x))
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace

