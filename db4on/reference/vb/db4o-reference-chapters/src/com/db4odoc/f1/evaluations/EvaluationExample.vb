Imports System.IO
Imports com.db4o.query
Imports com.db4o

Namespace com.db4odoc.f1.evaluations
    Public Class EvaluationExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args As String())
            File.Delete(YapFileName)
            Dim db As Global.com.db4o.ObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                StoreCars(db)
                QueryWithEvaluation(db)
            Finally
                db.Close()
            End Try
        End Sub
        ' end Main

        Public Shared Sub StoreCars(ByVal db As ObjectContainer)
            Dim pilot1 As evaluations.Pilot = New evaluations.Pilot("Michael Schumacher", 100)
            Dim car1 As evaluations.Car = New evaluations.Car("Ferrari")
            car1.Pilot = pilot1
            car1.Snapshot()
            db.[Set](car1)
            Dim pilot2 As evaluations.Pilot = New evaluations.Pilot("Rubens Barrichello", 99)
            Dim car2 As evaluations.Car = New evaluations.Car("BMW")
            car2.Pilot = pilot2
            car2.Snapshot()
            car2.Snapshot()
            db.[Set](car2)
        End Sub
        ' end StoreCars

        Public Shared Sub QueryWithEvaluation(ByVal db As ObjectContainer)
            Dim query As Query = db.Query()
            query.Constrain(GetType(evaluations.Car))
            query.Constrain(New EvenHistoryEvaluation())
            Dim result As ObjectSet = query.Execute()
            Util.ListResult(result)
        End Sub
        ' end QueryWithEvaluation
    End Class
End Namespace
