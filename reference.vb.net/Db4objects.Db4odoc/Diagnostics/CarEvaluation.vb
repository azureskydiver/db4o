' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Diagnostics
    Public Class CarEvaluation
        Implements IEvaluation
        Public Sub Evaluate(ByVal candidate As ICandidate) Implements IEvaluation.Evaluate
            Dim car As Car = CType(candidate.GetObject(), Car)
            candidate.Include(car.Model.EndsWith("2002"))
        End Sub

    End Class
End Namespace