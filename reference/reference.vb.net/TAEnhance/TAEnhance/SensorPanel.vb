' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 


Namespace Db4objects.Db4odoc.Activating
    Public Class SensorPanel
        Private _sensor As Object
        Private _next As SensorPanel

        ' default constructor for instantiation
        Public Sub New()
        End Sub

        Public Sub New(ByVal value As Integer)
            _sensor = value
        End Sub

        Public Function CreateList(ByVal length As Integer) As SensorPanel
            Return CreateList(length, 1)
        End Function

        Public Function CreateList(ByVal length As Integer, ByVal first As Integer) As SensorPanel
            Dim val As Integer = first
            Dim root As SensorPanel = NewElement(first)
            Dim list As SensorPanel = root
            While System.Threading.Interlocked.Decrement(length) > 0
                list._next = NewElement(System.Threading.Interlocked.Increment(val))
                list = list._next
            End While
            Return root
        End Function

        Public ReadOnly Property Sensor() As Object
            Get
                Return _sensor
            End Get
        End Property

        Public Property [Next]() As SensorPanel
            Get
                Return _next
            End Get

            Set(ByVal value As SensorPanel)
                _next = value
            End Set
        End Property

        Protected Function NewElement(ByVal value As Integer) As SensorPanel
            Return New SensorPanel(value)
        End Function

        Public Overloads Overrides Function ToString() As String
            Return "Sensor #" + _sensor.ToString
        End Function
    End Class
End Namespace
