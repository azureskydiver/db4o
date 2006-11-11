Imports System

Namespace Db4objects.Db4odoc.ClientServer
    Public Class Car
        Private _model As String

        Private _pilot As Pilot

        Private _history As SensorReadout

        Public Sub New(ByVal model As String)
            _model = model
            _pilot = Nothing
            _history = Nothing
        End Sub

        Public Property Pilot() As Pilot
            Get
                Return _pilot
            End Get
            Set(ByVal value As Pilot)
                _pilot = value
            End Set
        End Property

        Public ReadOnly Property Model() As String
            Get
                Return _model
            End Get
        End Property

        Public Function GetHistory() As SensorReadout
            Return _history
        End Function

        Public Sub Snapshot()
            AppendToHistory(New TemperatureSensorReadout(DateTime.Now, Me, "oil", PollOilTemperature()))
            AppendToHistory(New TemperatureSensorReadout(DateTime.Now, Me, "water", PollWaterTemperature()))
            AppendToHistory(New PressureSensorReadout(DateTime.Now, Me, "oil", PollOilPressure()))
        End Sub

        Protected Function PollOilTemperature() As Double
            Return 0.1 * CountHistoryElements()
        End Function

        Protected Function PollWaterTemperature() As Double
            Return 0.2 * CountHistoryElements()
        End Function

        Protected Function PollOilPressure() As Double
            Return 0.3 * CountHistoryElements()
        End Function

        Public Overloads Overrides Function ToString() As String
            Return String.Format("{0}[{1}]/{2}", _model, _pilot, CountHistoryElements())
        End Function

        Private Function CountHistoryElements() As Integer
            If _history Is Nothing Then
                Return 0
            End If
            Return _history.CountElements()
        End Function

        Private Sub AppendToHistory(ByVal readout As SensorReadout)
            If _history Is Nothing Then
                _history = readout
            Else
                _history.Append(readout)
            End If
        End Sub

    End Class
End Namespace
