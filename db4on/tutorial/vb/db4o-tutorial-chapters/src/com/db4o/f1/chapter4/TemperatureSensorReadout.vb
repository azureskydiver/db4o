Imports System
Namespace com.db4o.f1.chapter4
	Public Class TemperatureSensorReadout
	Inherits SensorReadout
		Private _temperature As Double

		Public Sub New(ByVal time As DateTime, ByVal car As Car, ByVal description As String, ByVal temperature As Double)
			MyBase.New(time, car, description)
			_temperature = temperature
		End Sub 

		Public ReadOnly Property Temperature() As Double
			Get
				Return _temperature
			End Get
		End Property 

		Public Overloads Overrides Function ToString() As String
			Return String.Format("{0} temp: {1}", MyBase.ToString(), _temperature)
		End Function

	End Class
End Namespace
