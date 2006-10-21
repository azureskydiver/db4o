Imports System
Imports System.Text
Namespace com.db4odoc.f1.diagnostics
    Public Class SensorReadout
        Private _values As Double()

        Private _time As DateTime

        Private _car As Car

        Public Sub New(ByVal values As Double(), ByVal time As DateTime, ByVal car As Car)
            _values = values
            _time = time
            _car = car
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

        Public ReadOnly Property NumValues() As Integer
            Get
                Return _values.Length
            End Get
        End Property

        Public ReadOnly Property Values() As Double()
            Get
                Return _values
            End Get
        End Property

        Public Function GetValue(ByVal idx As Integer) As Double
            Return _values(idx)
        End Function

        Public Overloads Overrides Function ToString() As String
            Dim builder As StringBuilder = New StringBuilder()
            builder.Append(_car)
            builder.Append(" : ")
            builder.Append(_time.TimeOfDay)
            builder.Append(" : ")
            Dim i As Integer = 0
            While i < _values.Length
                If i > 0 Then
                    builder.Append(", ")
                End If
                builder.Append(_values(i))
                System.Threading.Interlocked.Increment(i)
            End While
            Return builder.ToString()
        End Function

    End Class
End Namespace
