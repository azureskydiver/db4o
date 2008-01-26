' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com
Imports System.Collections.Generic
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Activation
Imports Db4objects.Db4o.TA
Imports Db4objects.Db4o.Collections


Namespace Db4objects.Db4odoc.TPExample

    Public Class Team
        Implements IActivatable

        Private _pilots As IList(Of Pilot) = New ArrayList4(Of Pilot)
        Private _name As String

        ' TA Activator
        <Transient()> _
        Private _activator As IActivator

        Public ReadOnly Property Pilots() As IList(Of Pilot)
            Get
                Activate(ActivationPurpose.Read)
                Return _pilots
            End Get
        End Property


        ' Bind the class to an object container
        Public Sub Bind(ByVal activator As IActivator) Implements IActivatable.Bind
            If _activator Is activator Then
                Return
            End If
            If Not (activator Is Nothing Or _activator Is Nothing) Then
                Throw New System.InvalidOperationException()
            End If
            _activator = activator
        End Sub

        ' activate object fields 
        Public Sub Activate(ByVal purpose As ActivationPurpose) Implements IActivatable.Activate
            If _activator Is Nothing Then
                Return
            End If
            _activator.Activate(ActivationPurpose.Read)
        End Sub

        Public Sub AddPilot(ByVal pilot As Pilot)
            ' activate before adding new pilots
            Activate(ActivationPurpose.Read)
            _pilots.Add(pilot)
        End Sub

        Public Function Size() As Integer
            ' activate before returning
            Activate(ActivationPurpose.Read)
            Return _pilots.Count
        End Function

        Public Sub ListAllPilots()
            ' activate before printing the collection members
            Activate(ActivationPurpose.Read)
            Dim p As Pilot
            For Each p In _pilots
                System.Console.WriteLine(p)
            Next
        End Sub

    End Class
End Namespace