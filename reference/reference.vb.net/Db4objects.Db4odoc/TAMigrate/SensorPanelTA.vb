' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Activation
Imports Db4objects.Db4o.TA

Namespace Db4ojects.Db4odoc.TAMigrate

    Public Class SensorPanelTA ' must implement Activatable for TA
        Implements IActivatable

        Private _sensor As Object
        Private _next As SensorPanelTA

        ' activator registered for this class
        <Transient()> _
        Private _activator As IActivator

        Public Sub New()
        End Sub
        ' end New

        Public Sub New(ByVal value As Integer)
            _sensor = value
        End Sub
        ' end New

        ' Bind the class to the specified object container, create the activator 
        Public Sub Bind(ByVal activator As IActivator) Implements IActivatable.Bind
            If _activator Is activator Then
                Return
            End If
            If Not (activator Is Nothing Or _activator Is Nothing) Then
                Throw New System.InvalidOperationException()
            End If
            _activator = activator
        End Sub
        ' end Bind

        'Call the registered activator to activate the next level,
        ' the activator remembers the objects that were already 
        ' activated and won't activate them twice. 
        Public Sub Activate(ByVal purpose As ActivationPurpose) Implements IActivatable.Activate
            If _activator Is Nothing Then
                Return
            End If
            _activator.Activate(purpose)
        End Sub
        ' end Activate

        Public ReadOnly Property NextSensor() As SensorPanelTA
            Get
                ' activate direct members
                Activate(ActivationPurpose.Read)
                Return _next
            End Get
        End Property
        ' end NextSensor

        Public ReadOnly Property Sensor() As Object
            Get
                ' activate direct members
                Activate(ActivationPurpose.Read)
                Return _sensor
            End Get
        End Property
        ' end Sensor

        Public Function CreateList(ByVal length As Integer) As SensorPanelTA
            Return CreateList(length, 1)
        End Function
        ' end CreateList

        Public Function CreateList(ByVal length As Integer, ByVal first As Integer) As SensorPanelTA
            Dim val As Integer = first
            Dim root As SensorPanelTA = NewElement(first)
            Dim list As SensorPanelTA = root
            While System.Threading.Interlocked.Decrement(length) > 0
                list._next = NewElement(System.Threading.Interlocked.Increment(val))
                list = list.NextSensor
            End While
            Return root
        End Function
        ' end CreateList

        Protected Function NewElement(ByVal value As Integer) As SensorPanelTA
            Return New SensorPanelTA(value)
        End Function
        ' end NewElement

        Public Overloads Overrides Function ToString() As String
            Return "Sensor #" + Sensor.ToString()
        End Function
        ' end ToString
    End Class
End Namespace