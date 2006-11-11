' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Diagnostics
    Public Class ArbitraryQuery
        Inherits Predicate
        Private _points() As Integer

        Public Sub New(ByVal points() As Integer)
            _points = points
        End Sub

        Public Function Match(ByVal pilot As Evaluations.Pilot) As Boolean
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


