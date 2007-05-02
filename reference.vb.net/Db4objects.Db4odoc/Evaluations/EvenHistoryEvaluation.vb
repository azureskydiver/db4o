' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Evaluations
    Public Class EvenHistoryEvaluation
        Implements IEvaluation
        Public Sub Evaluate(ByVal candidate As ICandidate) Implements IEvaluation.Evaluate
            Dim car As Car = DirectCast(candidate.GetObject(), Car)
            candidate.Include(car.History.Count Mod 2 = 0)
        End Sub

    End Class
End Namespace
