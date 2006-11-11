' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Queries
    Public Class ComplexQuery
        Inherits Predicate
        Public Function Match(ByVal pilot As Pilot) As Boolean
            Return pilot.Points > 99 AndAlso pilot.Points < 199 OrElse pilot.Name = "Rubens Barrichello"
        End Function

    End Class
End Namespace
