' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Refactoring
    Public Class RefactoringExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args() As String)
            System.Console.WriteLine("Correct sequence of actions: ")
            SetObjects()
            CheckDB()
            ChangeClass()
            SetNewObjects()
            RetrievePilotNew()

            '           System.Console.WriteLine("Incorrect sequence of actions: ")
            ' 			SetObjects()
            ' 			CheckDB()	
            ' 			SetNewObjects()
            ' 			ChangeClass()
            ' 			RetrievePilotNew()*/
        End Sub
        ' end Main

        Public Shared Sub SetObjects()
            File.Delete(YapFileName)
            Dim oc As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim pilot As Pilot = New Pilot("Rubens Barrichello")
                oc.Set(pilot)
                pilot = New Pilot("Michael Schumacher")
                oc.Set(pilot)
            Finally
                oc.Close()
            End Try
        End Sub
        ' end SetObjects

        Public Shared Sub CheckDB()
            Dim oc As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim result As IObjectSet = oc.Get(GetType(Pilot))
                Dim i As Integer
                For i = 0 To result.Size() - 1 Step i + 1
                    Dim pilot As Pilot = CType(result(i), Pilot)
                    System.Console.WriteLine("Pilot=" + pilot.ToString())
                Next
            Finally
                oc.Close()
            End Try
        End Sub
        ' end CheckDB

        Public Shared Sub SetNewObjects()
            Dim oc As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim pilot As PilotNew = New PilotNew("Rubens Barrichello", 99)
                oc.Set(pilot)
                pilot = New PilotNew("Michael Schumacher", 100)
                oc.Set(pilot)
            Finally
                oc.Close()
            End Try
        End Sub
        ' end SetNewObjects

        Public Shared Sub ChangeClass()
            Db4oFactory.Configure().ObjectClass(GetType(Pilot)).Rename("Db4objects.Db4odoc.Refactoring.PilotNew, Db4objects.Db4odoc")
            Db4oFactory.Configure().ObjectClass(GetType(PilotNew)).ObjectField("_name").Rename("_identity")
            Dim oc As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            oc.Close()
        End Sub
        ' end ChangeClass

        Public Shared Sub RetrievePilotNew()
            Dim oc As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim q As IQuery = oc.Query()
                q.Constrain(GetType(PilotNew))
                Dim result As IObjectSet = q.Execute()
                Dim i As Integer
                For i = 0 To result.Size() - 1 Step i + 1
                    Dim pilot As PilotNew = CType(result(i), PilotNew)
                    System.Console.WriteLine("Pilot=" + pilot.ToString())
                Next
            Finally
                oc.Close()
            End Try
        End Sub
        ' end RetrievePilotNew
    End Class
End Namespace

