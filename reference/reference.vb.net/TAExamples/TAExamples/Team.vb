' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com
Imports System.Collections
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Activation
Imports Db4objects.Db4o.TA
Imports Db4objects.Db4o.Collections


Namespace Db4ojects.Db4odoc.TAExamples

    Public Class Team
        Implements IActivatable

        Private _pilots As IList = New ArrayList4(Of Pilot)
        Private _name As String

        ' TA Activator
        <Transient()> _
        Private _activator As IActivator

        ' Bind the class to an object container
        Public Sub Bind(ByVal activator As IActivator) Implements IActivatable.Bind
            If Not Nothing Is _activator Then
                Throw New System.InvalidOperationException()
            End If
            _activator = activator
        End Sub

        ' activate object fields 
        Public Sub Activate() Implements IActivatable.Activate
            If _activator Is Nothing Then
                Return
            End If
            _activator.Activate()
        End Sub

        Public Sub AddPilot(ByVal pilot As Pilot)
            ' activate before adding new pilots
            Activate()
            _pilots.Add(pilot)
        End Sub

        Public Sub ListAllPilots()
            ' activate before printing the collection members
            Activate()
            Dim iter As IEnumerator = _pilots.GetEnumerator
            While iter.MoveNext
                Dim pilot As Pilot = CType(iter.Current, Pilot)
                System.Console.WriteLine(pilot)
            End While
        End Sub

    End Class
End Namespace