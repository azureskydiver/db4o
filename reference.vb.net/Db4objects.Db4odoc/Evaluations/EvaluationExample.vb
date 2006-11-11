Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Evaluations
    Public Class EvaluationExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args As String())
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                StoreCars(db)
                QueryWithEvaluation(db)
            Finally
                db.Close()
            End Try
        End Sub
        ' end Main

        Public Shared Sub StoreCars(ByVal db As IObjectContainer)
            Dim pilot1 As Pilot = New Pilot("Michael Schumacher", 100)
            Dim car1 As Car = New Car("Ferrari")
            car1.Pilot = pilot1
            car1.Snapshot()
            db.[Set](car1)
            Dim pilot2 As Pilot = New Pilot("Rubens Barrichello", 99)
            Dim car2 As Car = New Car("BMW")
            car2.Pilot = pilot2
            car2.Snapshot()
            car2.Snapshot()
            db.[Set](car2)
        End Sub
        ' end StoreCars

        Public Shared Sub QueryWithEvaluation(ByVal db As IObjectContainer)
            Dim query As IQuery = db.Query()
            query.Constrain(GetType(Car))
            query.Constrain(New EvenHistoryEvaluation())
            Dim result As IObjectSet = query.Execute()
            ListResult(result)
        End Sub
        ' end QueryWithEvaluation

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace
