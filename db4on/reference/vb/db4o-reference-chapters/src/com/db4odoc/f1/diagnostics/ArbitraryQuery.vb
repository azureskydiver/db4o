' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports com.db4o.query

Namespace com.db4odoc.f1.diagnostics
    Public Class ArbitraryQuery
        Inherits Predicate
        Private _points() As Integer
        Public Sub New(ByVal points() As Integer)
            _points = points
        End Sub
        Public Function Match(ByVal pilot As evaluations.Pilot) As Boolean
            Dim points As Integer
            For Each points In _points
                If pilot.Points = points Then
                    Return True
                End If
            Next
            Return pilot.Name.StartsWith("Rubens")
        End Function
    End Class
End Namespace


