Imports System
Imports System.Collections
Namespace com.db4o.f1.chapter4
	Public Class Car
		Private _model As String

		Private _pilot As Pilot

		Private _history As IList

		Public Sub New(ByVal model As String)
			_model = model
			_pilot = Nothing
			_history = New ArrayList()
		End Sub 

		Public Property Pilot() As Pilot
			Get
				Return _pilot
			End Get
			Set
				_pilot = value
			End Set
		End Property 

		Public ReadOnly Property Model() As String
			Get
				Return _model
			End Get
		End Property 

		Public Function GetHistory() As SensorReadout()
			Dim history As SensorReadout() = New SensorReadout(_history.Count) {}
			_history.CopyTo(history, 0)
			Return history
		End Function

		Public Sub Snapshot()
			_history.Add(New TemperatureSensorReadout(DateTime.Now, Me, "oil", PollOilTemperature()))
			_history.Add(New TemperatureSensorReadout(DateTime.Now, Me, "water", PollWaterTemperature()))
			_history.Add(New PressureSensorReadout(DateTime.Now, Me, "oil", PollOilPressure()))
		End Sub

		Protected Function PollOilTemperature() As Double
			Return 0.1 * _history.Count
		End Function

		Protected Function PollWaterTemperature() As Double
			Return 0.2 * _history.Count
		End Function

		Protected Function PollOilPressure() As Double
			Return 0.3 * _history.Count
		End Function

		Public Overloads Overrides Function ToString() As String
			Return String.Format("{0}[{1}]/{2}", _model, _pilot, _history.Count)
		End Function

	End Class
End Namespace
