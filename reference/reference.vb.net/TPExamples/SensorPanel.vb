' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 

Imports Db4objects.Db4o

Namespace Db4objects.Db4odoc.TPBuildTime
    Public Class SensorPanel
        Private _sensor As Object

        Private _next As SensorPanel

        ' default constructor for instantiation
        Public Sub New()
        End Sub
        ' end SensorPanel

        Public Sub New(ByVal value As Integer)
            _sensor = value
        End Sub
        ' end SensorPanel

        Public ReadOnly Property NextSensor() As SensorPanel
            Get
                Return _next
            End Get
        End Property
        ' end Next

        Public Property Sensor() As Object
            Get
                Return _sensor
            End Get
            Set(ByVal value As Object)
                _sensor = value
            End Set
        End Property
        ' end Sensor

        Public Function CreateList(ByVal length As Integer) As SensorPanel
            Return CreateList(length, 1)
        End Function
        ' end CreateList

        Public Function CreateList(ByVal length As Integer, ByVal first As Integer) As SensorPanel
            Dim val As Integer = first
            Dim root As SensorPanel = NewElement(first)
            Dim list As SensorPanel = root
            While System.Threading.Interlocked.Decrement(length) > 0
                list._next = NewElement(System.Threading.Interlocked.Increment(val))
                list = list.NextSensor
            End While
            Return root
        End Function
        ' end CreateList

        Protected Function NewElement(ByVal value As Integer) As SensorPanel
            Return New SensorPanel(value)
        End Function
        ' end NewElement

        Public Overloads Overrides Function ToString() As String
            Return "Sensor #" + Sensor.ToString()
        End Function
        ' end ToString
    End Class

End Namespace
