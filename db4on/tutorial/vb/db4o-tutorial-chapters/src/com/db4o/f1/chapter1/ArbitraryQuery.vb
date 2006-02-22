Imports com.db4o.query
Namespace com.db4o.f1.chapter1
	Public Class ArbitraryQuery
	Inherits Predicate
		Private _points As Integer()

		Public Sub New(ByVal points As Integer())
			_points = points
		End Sub 

		Public Function Match(ByVal pilot As Pilot) As Boolean
			For Each points As Integer In _points
				If pilot.Points = points Then
					Return True
				End If
			Next
			Return pilot.Name.StartsWith("Rubens")
		End Function

	End Class
End Namespace
