' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports System.Collections

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.SelectivePersistence
    Public Class MarkTransientExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args() As String)
            Dim configuration As IConfiguration = ConfigureTransient()
            SaveObjects(configuration)
            RetrieveObjects()
        End Sub
        ' end Main

        Public Shared Function ConfigureTransient() As IConfiguration
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.MarkTransient("Db4objects.Db4odoc.SelectivePersistence.FieldTransient")
            Return configuration
        End Function
        ' end ConfigureTransient

        Public Shared Function ConfigureSaveTransient() As IConfiguration
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ObjectClass(GetType(Test)).StoreTransientFields(True)
            Return configuration
        End Function
        ' end ConfigureSaveTransient

        Public Shared Sub SaveObjects(ByVal configuration As IConfiguration)
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim test As Test = New Test("Transient string", "Persistent string")
                container.Set(test)
                Dim testc As TestCusomized = New TestCusomized("Transient string", "Persistent string")
                container.Set(testc)
            Finally
                container.Close()
            End Try
        End Sub
        ' end SaveObjects

        Public Shared Sub RetrieveObjects()
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim query As IQuery = container.Query()
                query.Constrain(GetType(Object))
                Dim result As IList = query.Execute()
                ListResult(result)
            Finally
                container.Close()
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

