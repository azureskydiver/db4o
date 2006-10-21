Imports System
Imports System.IO
Imports com.db4o
Namespace com.db4odoc.f1.clientserver
    Public Class DeepExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"
        Public Shared Sub Main(ByVal args As String())
            File.Delete(YapFileName)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                StoreCar(db)
                db.Close()
                SetCascadeOnUpdate()
                db = Db4oFactory.OpenFile(YapFileName)
                TakeManySnapshots(db)
                db.Close()
                db = Db4oFactory.OpenFile(YapFileName)
                RetrieveAllSnapshots(db)
                db.Close()
                db = Db4oFactory.OpenFile(YapFileName)
                RetrieveSnapshotsSequentially(db)
                RetrieveSnapshotsSequentiallyImproved(db)
                db.Close()
                SetActivationDepth()
                db = Db4oFactory.OpenFile(YapFileName)
                RetrieveSnapshotsSequentially(db)
            Finally
                db.Close()
            End Try
        End Sub
        ' end Main

        Public Shared Sub StoreCar(ByVal db As ObjectContainer)
            Dim pilot As Pilot = New Pilot("Rubens Barrichello", 99)
            Dim car As Car = New Car("BMW")
            car.Pilot = pilot
            db.[Set](car)
        End Sub
        ' end StoreCar

        Public Shared Sub SetCascadeOnUpdate()
            Db4oFactory.Configure().ObjectClass(GetType(Car)).CascadeOnUpdate(True)
        End Sub
        ' end SetCascadeOnUpdate

        Public Shared Sub TakeManySnapshots(ByVal db As ObjectContainer)
            Dim result As ObjectSet = db.[Get](GetType(Car))
            Dim car As Car = DirectCast(result.[Next](), Car)
            Dim i As Integer = 0
            While i < 5
                car.Snapshot()
                System.Math.Max(System.Threading.Interlocked.Increment(i), i - 1)
            End While
            db.[Set](car)
        End Sub
        ' end TakeManySnapshots

        Public Shared Sub RetrieveAllSnapshots(ByVal db As ObjectContainer)
            Dim result As ObjectSet = db.[Get](GetType(SensorReadout))
            While result.HasNext()
                Console.WriteLine(result.[Next]())
            End While
        End Sub
        ' end RetrieveAllSnapshots

        Public Shared Sub RetrieveSnapshotsSequentially(ByVal db As ObjectContainer)
            Dim result As ObjectSet = db.[Get](GetType(Car))
            Dim car As Car = DirectCast(result.[Next](), Car)
            Dim readout As SensorReadout = car.GetHistory()
            While Not readout Is Nothing
                Console.WriteLine(readout)
                readout = readout.[Next]
            End While
        End Sub
        ' end RetrieveSnapshotsSequentially

        Public Shared Sub RetrieveSnapshotsSequentiallyImproved(ByVal db As ObjectContainer)
            Dim result As ObjectSet = db.[Get](GetType(Car))
            Dim car As Car = DirectCast(result.[Next](), Car)
            Dim readout As SensorReadout = car.GetHistory()
            While Not readout Is Nothing
                db.Activate(readout, 1)
                Console.WriteLine(readout)
                readout = readout.[Next]
            End While
        End Sub
        ' end RetrieveSnapshotsSequentiallyImproved

        Public Shared Sub SetActivationDepth()
            Db4oFactory.Configure().ObjectClass(GetType(TemperatureSensorReadout)).CascadeOnActivate(True)
        End Sub
        ' end SetActivationDepth
    End Class
End Namespace
