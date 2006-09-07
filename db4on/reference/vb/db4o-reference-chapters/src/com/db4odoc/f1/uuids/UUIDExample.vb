' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports com.db4o
Imports com.db4o.query
Imports com.db4o.foundation
Imports com.db4o.ext


Namespace com.db4odoc.f1.uuids
    Public Class UUIDExample
        Inherits Util

        Public Shared Sub main(ByVal args() As String)
            TestChangeIdentity()
            SetObjects()
            TestGenerateUUID()
        End Sub

        Private Shared Function PrintSignature(ByVal Signature() As Byte) As String
            Dim str As String = ""
            Dim i As Integer
            For i = 0 To Signature.Length - 1 Step i + 1
                str = str + Signature(i).ToString()
            Next
            Return str
        End Function

        Public Shared Sub TestChangeIdentity()

            File.Delete(Util.YapFileName)
            Dim oc As ObjectContainer = Db4o.OpenFile(Util.YapFileName)
            Dim db As Db4oDatabase
            Dim oldSignature() As Byte
            Dim NewSignature() As Byte
            Try
                db = oc.Ext().Identity()
                oldSignature = db.GetSignature()
                Console.WriteLine("oldSignature: " + PrintSignature(oldSignature))
                Dim yf As YapFile = DirectCast(oc, YapFile)
                yf.GenerateNewIdentity()
            Finally
                oc.Close()
            End Try
            oc = Db4o.OpenFile(Util.YapFileName)
            Try
                db = oc.Ext().Identity()
                NewSignature = db.GetSignature()
                Console.WriteLine("newSignature: " + PrintSignature(NewSignature))
            Finally
                oc.Close()
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

        Public Shared Sub SetObjects()
            Db4o.Configure().ObjectClass(GetType(Pilot)).GenerateUUIDs(True)
            File.Delete(Util.YapFileName)
            Dim oc As ObjectContainer = Db4o.OpenFile(Util.YapFileName)
            Try
                Dim car As Car = New Car("BMW", New Pilot("Rubens Barrichello"))
                oc.Set(car)
            Finally
                oc.Close()
            End Try
        End Sub

        Public Shared Sub TestGenerateUUID()
            Dim oc As ObjectContainer = Db4o.OpenFile(Util.YapFileName)
            Try
                Dim query As Query = oc.Query()
                query.Constrain(GetType(car))
                Dim result As ObjectSet = query.Execute()
                Dim car As Car = CType(result(0), Car)
                Dim carInfo As ObjectInfo = oc.Ext().GetObjectInfo(car)
                Dim carUUID As Db4oUUID = carInfo.GetUUID()
                Console.WriteLine("UUID for Car class are not generated:")
                If carUUID Is Nothing Then
                    Console.WriteLine("Car UUID: null")
                Else
                    Console.WriteLine("Car UUID: " + carUUID.ToString())
                End If


                Dim pilot As Pilot = car.Pilot
                Dim pilotInfo As ObjectInfo = oc.Ext().GetObjectInfo(pilot)
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
                Dim pilotReturned As Pilot = CType(oc.Ext().GetByUUID(pilotUUID), Pilot)
                Console.WriteLine("Pilot from UUID: " + pilotReturned.ToString())
            Finally
                oc.Close()
            End Try
        End Sub

    End Class
End Namespace