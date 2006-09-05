' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System

Namespace com.db4odoc.f1.activating
    Public Class SensorPanel
        Public _sensor As Object
        Public _next As SensorPanel

        Public Sub New()
            ' default constructor for instantiation
        End Sub


        Public Sub New(ByVal Value As Integer)
            _sensor = Value
        End Sub

        Public Function CreateList(ByVal length As Integer) As SensorPanel
            Return CreateList(length, 1)
        End Function

        Public Function CreateList(ByVal length As Integer, ByVal first As Integer) As SensorPanel
            Dim val As Integer = first
            Dim root As SensorPanel = NewElement(first)
            Dim list As SensorPanel = root
            While (length - 1) > 0
                list._next = NewElement(val + 1)
                val = val + 1
                length = length - 1
                list = list._next
            End While
            Return root
        End Function

        Public Property NextSensor() As SensorPanel
            Get
                Return _next
            End Get
            Set(ByVal Value As SensorPanel)
                _next = value
            End Set
        End Property

        Protected Function NewElement(ByVal value As Integer) As SensorPanel
            Return New SensorPanel(value)
        End Function

        Public Overrides Function ToString() As String
            If _sensor Is Nothing Then
                Return "Null"
            End If
            Return "Sensor #" + _sensor.ToString()
        End Function
    End Class
End Namespace

