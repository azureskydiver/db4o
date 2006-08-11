Imports System
Imports System.IO
Imports com.db4o

Namespace com.db4odoc.f1.clientserver
    Public Class TransactionExample
        Inherits Util
        Public Shared Sub Main(ByVal args As String())
            File.Delete(Util.YapFileName)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(Util.YapFileName)
            Try
                StoreCarCommit(db)
                db.Close()
                db = Db4oFactory.OpenFile(Util.YapFileName)
                ListAllCars(db)
                StoreCarRollback(db)
                db.Close()
                db = Db4oFactory.OpenFile(Util.YapFileName)
                ListAllCars(db)
                CarSnapshotRollback(db)
                CarSnapshotRollbackRefresh(db)
            Finally
                db.Close()
            End Try
        End Sub

        Public Shared Sub StoreCarCommit(ByVal db As ObjectContainer)
            Dim pilot As Pilot = New Pilot("Rubens Barrichello", 99)
            Dim car As Car = New Car("BMW")
            car.Pilot = pilot
            db.[Set](car)
            db.Commit()
        End Sub

        Public Shared Sub ListAllCars(ByVal db As ObjectContainer)
            Dim result As ObjectSet = db.[Get](GetType(Car))
            ListResult(result)
        End Sub

        Public Shared Sub StoreCarRollback(ByVal db As ObjectContainer)
            Dim pilot As Pilot = New Pilot("Michael Schumacher", 100)
            Dim car As Car = New Car("Ferrari")
            car.Pilot = pilot
            db.[Set](car)
            db.Rollback()
        End Sub

        Public Shared Sub CarSnapshotRollback(ByVal db As ObjectContainer)
            Dim result As ObjectSet = db.[Get](New Car("BMW"))
            Dim car As Car = DirectCast(result.[Next](), Car)
            car.Snapshot()
            db.[Set](car)
            db.Rollback()
            Console.WriteLine(car)
        End Sub

        Public Shared Sub CarSnapshotRollbackRefresh(ByVal db As ObjectContainer)
            Dim result As ObjectSet = db.[Get](New Car("BMW"))
            Dim car As Car = DirectCast(result.[Next](), Car)
            car.Snapshot()
            db.[Set](car)
            db.Rollback()
            db.Ext().Refresh(car, Integer.MaxValue)
            Console.WriteLine(car)
        End Sub

    End Class
End Namespace
