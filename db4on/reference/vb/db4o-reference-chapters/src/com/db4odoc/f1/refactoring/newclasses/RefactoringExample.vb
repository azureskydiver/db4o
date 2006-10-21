' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports com.db4o
Imports com.db4o.query
Imports com.db4o.ext

Namespace com.db4odoc.f1.refactoring.Newclasses
    Public Class RefactoringExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args() As String)
            ReopenDB()
            TransferValues()
        End Sub
        ' end Main

        Public Shared Sub ReopenDB()
            Db4o.Configure().DetectSchemaChanges(False)
            Dim oc As ObjectContainer = Db4o.OpenFile(YapFileName)
            oc.Close()
        End Sub
        ' end ReopenDB

        Public Shared Sub TransferValues()
            Dim oc As ObjectContainer = Db4o.OpenFile(YapFileName)
            Try
                Dim sc As StoredClass = oc.Ext().StoredClass(GetType(Pilot))
                System.Console.WriteLine("Stored class:  " + sc.GetName())
                Dim sfOld As StoredField = sc.StoredField("_name", GetType(String))
                System.Console.WriteLine("Old field:  " + sfOld.GetName() + ";" + sfOld.GetStoredType().GetName())
                Dim q As Query = oc.Query()
                q.Constrain(GetType(Pilot))
                Dim result As ObjectSet = q.Execute()
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
