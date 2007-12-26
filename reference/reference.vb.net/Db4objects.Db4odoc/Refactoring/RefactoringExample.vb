' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Refactoring
    Public Class RefactoringExample
        Private Const Db4oFileName As String = "reference.db4o"

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

        Private Shared Sub SetObjects()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim pilot As Pilot = New Pilot("Rubens Barrichello")
                container.Set(pilot)
                pilot = New Pilot("Michael Schumacher")
                container.Set(pilot)
            Finally
                container.Close()
            End Try
        End Sub
        ' end SetObjects

        Private Shared Sub CheckDB()
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim result As IObjectSet = container.Get(GetType(Pilot))
                Dim obj As Object
                For Each obj In result
                    Dim pilot As Pilot = CType(obj, Pilot)
                    System.Console.WriteLine("Pilot=" + pilot.ToString())
                Next
            Finally
                container.Close()
            End Try
        End Sub
        ' end CheckDB

        Private Shared Sub SetNewObjects()
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim pilot As PilotNew = New PilotNew("Rubens Barrichello", 99)
                container.Set(pilot)
                pilot = New PilotNew("Michael Schumacher", 100)
                container.Set(pilot)
            Finally
                container.Close()
            End Try
        End Sub
        ' end SetNewObjects

        Private Shared Sub ChangeClass()
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ObjectClass(GetType(Pilot)).Rename("Db4objects.Db4odoc.Refactoring.PilotNew, Db4objects.Db4odoc")
            configuration.ObjectClass(GetType(PilotNew)).ObjectField("_name").Rename("_identity")
            Dim container As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            container.Close()
        End Sub
        ' end ChangeClass

        Private Shared Sub RetrievePilotNew()
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim q As IQuery = container.Query()
                q.Constrain(GetType(PilotNew))
                Dim result As IObjectSet = q.Execute()
                Dim obj As Object
                For Each obj In result
                    Dim pilot As PilotNew = CType(obj, PilotNew)
                    System.Console.WriteLine("Pilot=" + pilot.ToString())
                Next
            Finally
                container.Close()
            End Try
        End Sub
        ' end RetrievePilotNew

    End Class
End Namespace

