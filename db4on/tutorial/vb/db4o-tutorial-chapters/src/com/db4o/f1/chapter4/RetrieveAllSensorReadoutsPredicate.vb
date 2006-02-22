Imports com.db4o.query
Namespace com.db4o.f1.chapter4
	Public Class RetrieveAllSensorReadoutsPredicate
	Inherits Predicate
		Public Function Match(ByVal candidate As SensorReadout) As Boolean
			Return True
		End Function

	End Class
End Namespace
