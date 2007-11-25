' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Query
Imports Db4objects.Db4o.Foundation
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.Internal


Namespace Db4objects.Db4odoc.UUIDs
    Public Class UUIDExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args() As String)
            TestChangeIdentity()
            SetObjects()
            TestGenerateUUID()
        End Sub
        ' end Main

        Private Shared Function PrintSignature(ByVal Signature() As Byte) As String
            Dim str As String = ""
            Dim i As Integer
            For i = 0 To Signature.Length - 1 Step i + 1
                str = str + Signature(i).ToString()
            Next
            Return str
        End Function
        ' end PrintSignature

        Public Shared Sub TestChangeIdentity()

            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Dim db As Db4oDatabase
            Dim oldSignature() As Byte
            Dim NewSignature() As Byte
            Try
                db = container.Ext().Identity()
                oldSignature = db.GetSignature()
                Console.WriteLine("oldSignature: " + PrintSignature(oldSignature))
                Dim yf As LocalObjectContainer = DirectCast(container, LocalObjectContainer)
                yf.GenerateNewIdentity()
            Finally
                container.Close()
            End Try
            container = Db4oFactory.OpenFile(Db4oFileName)
            Try
                db = container.Ext().Identity()
                NewSignature = db.GetSignature()
                Console.WriteLine("newSignature: " + PrintSignature(NewSignature))
            Finally
                container.Close()
            End Try

            Dim same As Boolean = True

            Dim i As Integer
            For i = 0 To oldSignature.Length - 1 Step i + 1
                If oldSignature(i) <> NewSignature(i) Then

                    same = False
                End If
            Next

            If (same) Then
                Console.WriteLine("Database signatures are identical")
            Else
                Console.WriteLine("Database signatures are different")
            End If
        End Sub
        ' end TestChangeIdentity

        Public Shared Sub SetObjects()
            File.Delete(Db4oFileName)
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration
            configuration.ObjectClass(GetType(Pilot)).GenerateUUIDs(True)
            Dim container As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim car As Car = New Car("BMW", New Pilot("Rubens Barrichello"))
                container.Set(car)
            Finally
                container.Close()
            End Try
        End Sub
        ' end SetObjects

        Public Shared Sub TestGenerateUUID()
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim query As IQuery = container.Query()
                query.Constrain(GetType(car))
                Dim result As IObjectSet = query.Execute()
                Dim car As Car = CType(result(0), Car)
                Dim carInfo As IObjectInfo = container.Ext().GetObjectInfo(car)
                Dim carUUID As Db4oUUID = carInfo.GetUUID()
                Console.WriteLine("UUID for Car class are not generated:")
                If carUUID Is Nothing Then
                    Console.WriteLine("Car UUID: null")
                Else
                    Console.WriteLine("Car UUID: " + carUUID.ToString())
                End If


                Dim pilot As Pilot = car.Pilot
                Dim pilotInfo As IObjectInfo = container.Ext().GetObjectInfo(pilot)
                Dim pilotUUID As Db4oUUID = pilotInfo.GetUUID()
                Console.WriteLine("UUID for Car class are not generated:")
                If pilotUUID Is Nothing Then
                    Console.WriteLine("Pilot UUID: null")
                Else
                    Console.WriteLine("Pilot UUID: " + pilotUUID.ToString())
                End If

                Console.WriteLine("long part: " + pilotUUID.GetLongPart().ToString() + "; signature: " + PrintSignature(pilotUUID.GetSignaturePart()))
                Dim ms As Long = TimeStampIdGenerator.IdToMilliseconds(pilotUUID.GetLongPart())
                Console.WriteLine("Pilot object was created: " + (New DateTime(1970, 1, 1)).AddMilliseconds(ms).ToString())
                Dim pilotReturned As Pilot = CType(container.Ext().GetByUUID(pilotUUID), Pilot)
                Console.WriteLine("Pilot from UUID: " + pilotReturned.ToString())
            Finally
                container.Close()
            End Try
        End Sub
        ' end TestGenerateUUID
    End Class
End Namespace