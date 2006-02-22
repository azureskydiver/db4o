Imports com.db4o.query
Namespace com.db4o.f1.chapter6
	Public Class EvenHistoryEvaluation
        Implements Evaluation
        Public Sub Evaluate(ByVal candidate As Candidate) Implements Evaluation.Evaluate
            Dim car As chapter3.Car = DirectCast(candidate.GetObject(), chapter3.Car)
            candidate.Include(car.History.Count Mod 2 = 0)
        End Sub

	End Class
End Namespace
