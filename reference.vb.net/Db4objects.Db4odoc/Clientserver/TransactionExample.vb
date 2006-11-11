Imports System
Imports System.IO
Imports Db4objects.Db4o

Namespace Db4objects.Db4odoc.ClientServer
    Public Class TransactionExample

        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args As String())
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                StoreCarCommit(db)
                db.Close()
                db = Db4oFactory.OpenFile(YapFileName)
                ListAllCars(db)
                StoreCarRollback(db)
                db.Close()
                db = Db4oFactory.OpenFile(YapFileName)
                ListAllCars(db)
                CarSnapshotRollback(db)
                CarSnapshotRollbackRefresh(db)
            Finally
                db.Close()
            End Try
        End Sub
        ' end Main

        Public Shared Sub StoreCarCommit(ByVal db As IObjectContainer)
            Dim pilot As Pilot = New Pilot("Rubens Barrichello", 99)
            Dim car As Car = New Car("BMW")
            car.Pilot = pilot
            db.[Set](car)
            db.Commit()
        End Sub
        ' end StoreCarCommit

        Public Shared Sub ListAllCars(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.[Get](GetType(Car))
            ListResult(result)
        End Sub
        ' end ListAllCars

        Public Shared Sub StoreCarRollback(ByVal db As IObjectContainer)
            Dim pilot As Pilot = New Pilot("Michael Schumacher", 100)
            Dim car As Car = New Car("Ferrari")
            car.Pilot = pilot
            db.[Set](car)
            db.Rollback()
        End Sub
        ' end StoreCarRollback

        Public Shared Sub CarSnapshotRollback(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.[Get](New Car("BMW"))
            Dim car As Car = DirectCast(result.[Next](), Car)
            car.Snapshot()
            db.[Set](car)
            db.Rollback()
            Console.WriteLine(car)
        End Sub
        ' end CarSnapshotRollback

        Public Shared Sub CarSnapshotRollbackRefresh(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.[Get](New Car("BMW"))
            Dim car As Car = DirectCast(result.[Next](), Car)
            car.Snapshot()
            db.[Set](car)
            db.Rollback()
            db.Ext().Refresh(car, Integer.MaxValue)
            Console.WriteLine(car)
        End Sub
        ' end CarSnapshotRollbackRefresh

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace
