' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Namespace Db4objects.Db4odoc.ClientServer
    Public Class PressureSensorReadout
        Inherits SensorReadout
        Private _pressure As Double

        Public Sub New(ByVal time As DateTime, ByVal car As Car, ByVal description As String, ByVal pressure As Double)
            MyBase.New(time, car, description)
            _pressure = pressure
        End Sub

        Public ReadOnly Property Pressure() As Double
            Get
                Return _pressure
            End Get
        End Property

        Public Overloads Overrides Function ToString() As String
            Return String.Format("{0} pressure : {1}", MyBase.ToString(), _pressure)
        End Function

    End Class
End Namespace
