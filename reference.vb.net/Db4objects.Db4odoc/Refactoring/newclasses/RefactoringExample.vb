' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query
Imports Db4objects.Db4o.Ext


Namespace Db4objects.Db4odoc.Refactoring.Newclasses
    Public Class RefactoringExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args() As String)
            ReopenDB()
            TransferValues()
        End Sub
        ' end Main

        Public Shared Sub ReopenDB()
            Db4o.Configure().DetectSchemaChanges(False)
            Dim oc As IObjectContainer = Db4o.OpenFile(YapFileName)
            oc.Close()
        End Sub
        ' end ReopenDB

        Public Shared Sub TransferValues()
            Dim oc As IObjectContainer = Db4o.OpenFile(YapFileName)
            Try
                Dim sc As IStoredClass = oc.Ext().StoredClass(GetType(Pilot))
                System.Console.WriteLine("Stored class:  " + sc.GetName())
                Dim sfOld As IStoredField = sc.StoredField("_name", GetType(String))
                System.Console.WriteLine("Old field:  " + sfOld.GetName() + ";" + sfOld.GetStoredType().GetName())
                Dim q As IQuery = oc.Query()
                q.Constrain(GetType(Pilot))
                Dim result As IObjectSet = q.Execute()
                Dim i As Integer
                For i = 0 To result.Size() - 1 Step i + 1
                    Dim pilot As Pilot = CType(result(i), Pilot)
                    pilot.Name = New Identity(sfOld.Get(pilot).ToString(), "")
                    System.Console.WriteLine("Pilot=" + pilot.ToString())
                    oc.Set(pilot)
                Next
            Finally
                oc.Close()
            End Try
        End Sub
        ' end TransferValues
    End Class
End Namespace
