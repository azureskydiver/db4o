Imports com.db4o.query
Namespace com.db4o.f1.chapter1
	Public Class ComplexQuery
	Inherits Predicate
		Public Function Match(ByVal pilot As Pilot) As Boolean
			Return pilot.Points > 99 AndAlso pilot.Points < 199 OrElse pilot.Name = "Rubens Barrichello"
		End Function

	End Class
End Namespace
