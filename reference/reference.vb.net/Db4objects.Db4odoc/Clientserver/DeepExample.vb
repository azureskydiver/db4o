' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config

Namespace Db4objects.Db4odoc.ClientServer
    Public Class DeepExample
        Private Const Db4oFileName As String = "reference.db4o"
        Public Shared Sub Main(ByVal args As String())
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                StoreCar(db)
                db.Close()
                Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
                configuration = SetCascadeOnUpdate()
                db = Db4oFactory.OpenFile(configuration, Db4oFileName)
                TakeManySnapshots(db)
                db.Close()
                db = Db4oFactory.OpenFile(configuration, Db4oFileName)
                RetrieveAllSnapshots(db)
                db.Close()
                db = Db4oFactory.OpenFile(configuration, Db4oFileName)
                RetrieveSnapshotsSequentially(db)
                RetrieveSnapshotsSequentiallyImproved(db)
                db.Close()
                configuration = SetActivationDepth()
                db = Db4oFactory.OpenFile(configuration, Db4oFileName)
                RetrieveSnapshotsSequentially(db)
            Finally
                db.Close()
            End Try
        End Sub
        ' end Main

        Private Shared Sub StoreCar(ByVal db As IObjectContainer)
            Dim pilot As Pilot = New Pilot("Rubens Barrichello", 99)
            Dim car As Car = New Car("BMW")
            car.Pilot = pilot
            db.Set(car)
        End Sub
        ' end StoreCar

        Private Shared Function SetCascadeOnUpdate() As IConfiguration
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ObjectClass(GetType(Car)).CascadeOnUpdate(True)
            Return configuration
        End Function
        ' end SetCascadeOnUpdate

        Private Shared Sub TakeManySnapshots(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(GetType(Car))
            Dim car As Car = DirectCast(result.Next(), Car)
            Dim i As Integer = 0
            While i < 5
                car.Snapshot()
                System.Math.Max(System.Threading.Interlocked.Increment(i), i - 1)
            End While
            db.Set(car)
        End Sub
        ' end TakeManySnapshots

        Private Shared Sub RetrieveAllSnapshots(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(GetType(SensorReadout))
            While result.HasNext()
                Console.WriteLine(result.Next())
            End While
        End Sub
        ' end RetrieveAllSnapshots

        Private Shared Sub RetrieveSnapshotsSequentially(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(GetType(Car))
            Dim car As Car = DirectCast(result.Next(), Car)
            Dim readout As SensorReadout = car.GetHistory()
            While Not readout Is Nothing
                Console.WriteLine(readout)
                readout = readout.[Next]
            End While
        End Sub
        ' end RetrieveSnapshotsSequentially

        Private Shared Sub RetrieveSnapshotsSequentiallyImproved(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(GetType(Car))
            Dim car As Car = DirectCast(result.Next(), Car)
            Dim readout As SensorReadout = car.GetHistory()
            While Not readout Is Nothing
                db.Activate(readout, 1)
                Console.WriteLine(readout)
                readout = readout.Next
            End While
        End Sub
        ' end RetrieveSnapshotsSequentiallyImproved

        Private Shared Function SetActivationDepth() As IConfiguration
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ObjectClass(GetType(TemperatureSensorReadout)).CascadeOnActivate(True)
            Return configuration
        End Function
        ' end SetActivationDepth

    End Class
End Namespace
