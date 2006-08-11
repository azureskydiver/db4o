Imports System
Namespace com.db4odoc.f1.clientserver
    Public Class SensorReadout
        Private _time As DateTime

        Private _car As Car

        Private _description As String

        Private _next As SensorReadout

        Protected Sub New(ByVal time As DateTime, ByVal car As Car, ByVal description As String)
            _time = time
            _car = car
            _description = description
            _next = Nothing
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

        Public ReadOnly Property [Next]() As SensorReadout
            Get
                Return _next
            End Get
        End Property

        Public Sub Append(ByVal sensorReadout As SensorReadout)
            If _next Is Nothing Then
                _next = sensorReadout
            Else
                _next.Append(sensorReadout)
            End If
        End Sub

        Public Function CountElements() As Integer
            If _next Is Nothing Then
                Return 1
            End If
            Return _next.CountElements() + 1
        End Function

        Public Overloads Overrides Function ToString() As String
            Return String.Format("{0} : {1} : {2}", _car, _time, _description)
        End Function

    End Class
End Namespace
