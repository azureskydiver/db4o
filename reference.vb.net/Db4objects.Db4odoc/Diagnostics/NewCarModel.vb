' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Diagnostics

    Public Class NewCarModel
        Inherits Predicate
        Public Function Match(ByVal car As evaluations.Car) As Boolean
            Return car.Model.EndsWith("2002")
        End Function
    End Class
End Namespace
