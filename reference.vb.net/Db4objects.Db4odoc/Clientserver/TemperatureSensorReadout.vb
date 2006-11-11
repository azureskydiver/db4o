Imports System

Namespace Db4objects.Db4odoc.ClientServer
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
            Return String.Concat(MyBase.ToString(), " temp: ", _temperature.ToString())
        End Function

    End Class
End Namespace
