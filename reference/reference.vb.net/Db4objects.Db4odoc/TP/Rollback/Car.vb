' Copyright (C) 2004 - 2008 db4objects Inc. http://www.db4o.com 

Imports System
Imports Db4objects.Db4o
Imports Db4objects.Db4o.TA
Imports Db4objects.Db4o.Activation

Namespace Db4objects.Db4odoc.TP.Rollback
    Public Class Car
        Implements IActivatable
        Private _model As String
        Private _pilot As Pilot
        'activator registered for this class

        <Transient()> _
        Public _activator As IActivator


        Public Sub New(ByVal model As String, ByVal pilot As Pilot)
            _model = model
            _pilot = pilot
        End Sub
        ' end Car

        'Bind the class to the specified object container, create the activator

        Public Sub Bind(ByVal activator As IActivator) Implements IActivatable.Bind
            If _activator Is activator Then
                Return
            End If
            If activator IsNot Nothing AndAlso _activator IsNot Nothing Then
                Throw New System.InvalidOperationException()
            End If
            _activator = activator
        End Sub
        ' end Bind

        Public Sub Activate(ByVal purpose As ActivationPurpose) Implements IActivatable.Activate
            If _activator Is Nothing Then
                Return
            End If
            _activator.Activate(purpose)
        End Sub
        ' end Activate

        Public Property Model() As String
            Get
                Activate(ActivationPurpose.Read)
                Return _model
            End Get
            Set(ByVal value As String)
                Activate(ActivationPurpose.Write)
                _model = value
            End Set
        End Property

        Public Property Pilot() As Pilot
            Get
                Activate(ActivationPurpose.Read)
                Return _pilot
            End Get
            Set(ByVal value As Pilot)
                Activate(ActivationPurpose.Write)
                _pilot = value
            End Set
        End Property

        Public Sub ChangePilot(ByVal name As String, ByVal id As Integer)
            _pilot.Name = name
            _pilot.Id.Change(id)
        End Sub

        Public Overloads Overrides Function ToString() As String
            Activate(ActivationPurpose.Read)
            Return String.Format("{0}[{1}]", _model, _pilot)
        End Function
        ' end ToString
    End Class
End Namespace
