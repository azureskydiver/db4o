' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Query
Imports Db4objects.Db4o.Ext


Namespace Db4objects.Db4odoc.Refactoring.Newclasses
    Public Class RefactoringExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args() As String)
            ReopenDB()
            TransferValues()
        End Sub
        ' end Main

        Private Shared Sub ReopenDB()
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.DetectSchemaChanges(False)
            Dim container As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            container.Close()
        End Sub
        ' end ReopenDB

        Private Shared Sub TransferValues()
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim sc As IStoredClass = container.Ext().StoredClass(GetType(Pilot))
                System.Console.WriteLine("Stored class:  " + sc.GetName())
                Dim sfOld As IStoredField = sc.StoredField("_name", GetType(String))
                System.Console.WriteLine("Old field:  " + sfOld.GetName() + ";" + sfOld.GetStoredType().GetName())
                Dim q As IQuery = container.Query()
                q.Constrain(GetType(Pilot))
                Dim result As IObjectSet = q.Execute()
                Dim obj As Object
                For Each obj In result
                    Dim pilot As Pilot = CType(obj, Pilot)
                    pilot.Name = New Identity(sfOld.Get(pilot).ToString(), "")
                    System.Console.WriteLine("Pilot=" + pilot.ToString())
                    container.Set(pilot)
                Next
            Finally
                container.Close()
            End Try
        End Sub
        ' end TransferValues
    End Class
End Namespace
