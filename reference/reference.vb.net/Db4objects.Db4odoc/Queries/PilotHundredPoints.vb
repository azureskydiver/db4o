' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Queries
    Public Class PilotHundredPoints
        Inherits Predicate
        Public Function Match(ByVal pilot As Pilot) As Boolean
            If pilot.Points = 100 Then
                Return True
            Else
                Return False
            End If
        End Function
    End Class
End Namespace