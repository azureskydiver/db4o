Imports com.db4o.query
Namespace com.db4odoc.f1.evaluations
    Public Class EvenHistoryEvaluation
        Implements evaluation
        Public Sub Evaluate(ByVal candidate As Candidate) Implements evaluation.Evaluate
            Dim car As Car = DirectCast(candidate.GetObject(), Car)
            candidate.Include(car.History.Count Mod 2 = 0)
        End Sub

    End Class
End Namespace
