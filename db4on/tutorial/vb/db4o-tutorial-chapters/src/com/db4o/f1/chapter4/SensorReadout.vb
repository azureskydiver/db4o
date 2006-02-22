Imports System
Namespace com.db4o.f1.chapter4
	Public Class SensorReadout
		Private _time As DateTime

		Private _car As Car

		Private _description As String

		Public Sub New(ByVal time As DateTime, ByVal car As Car, ByVal description As String)
			_time = time
			_car = car
			_description = description
		End Sub 

		Public ReadOnly Property Car() As Car
			Get
				Return _car
			End Get
		End Property 

		Public ReadOnly Property Time() As DateTime
			Get
				Return _time
			End Get
		End Property 

		Public ReadOnly Property Description() As String
			Get
				Return _description
			End Get
		End Property 

		Public Overloads Overrides Function ToString() As String
			Return String.Format("{0}:{1}:{2}", _car, _time, _description)
		End Function

	End Class
End Namespace
