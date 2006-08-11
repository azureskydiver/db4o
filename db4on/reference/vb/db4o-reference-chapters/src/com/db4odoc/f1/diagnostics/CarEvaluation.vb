' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports com.db4o.query

Namespace com.db4odoc.f1.diagnostics
    Public Class CarEvaluation
        Implements Evaluation
        Public Sub Evaluate(ByVal candidate As Candidate) Implements Evaluation.Evaluate
            Dim car As evaluations.Car = CType(candidate.GetObject(), evaluations.Car)
            candidate.Include(car.Model.EndsWith("2002"))
        End Sub

    End Class
End Namespace