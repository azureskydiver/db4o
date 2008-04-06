' Copyright (C) 2004 - 2008 db4objects Inc. http://www.db4o.com 

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Activation
Imports Db4objects.Db4o.TA

Namespace Db4objects.Db4odoc.TP.Rollback
    Public Class Pilot
        Implements IActivatable
        Private _name As String
        Private _id As Id

        <Transient()> _
        Private _activator As IActivator

        Public Sub New(ByVal name As String, ByVal id As Integer)
            _name = name
            _id = New Id(id)
        End Sub

        ' Bind the class to an object container
        Public Sub Bind(ByVal activator As IActivator) Implements IActivatable.Bind
            If _activator Is activator Then
                Return
            End If
            If activator IsNot Nothing AndAlso _activator IsNot Nothing Then
                Throw New System.InvalidOperationException()
            End If
            _activator = activator
        End Sub

        ' activate the object fields
        Public Sub Activate(ByVal purpose As ActivationPurpose) Implements IActivatable.Activate
            If _activator Is Nothing Then
                Return
            End If
            _activator.Activate(purpose)
        End Sub

        Public Property Id() As Id
            Get
                Activate(ActivationPurpose.Read)
                Return _id
            End Get
            Set(ByVal value As Id)
                Activate(ActivationPurpose.Write)
                _id = value
            End Set
        End Property

        Public Property Name() As String
            Get
                ' even simple string needs to be activated
                Activate(ActivationPurpose.Read)
                Return _name
            End Get
            Set(ByVal value As String)
                Activate(ActivationPurpose.Write)
                _name = value
            End Set
        End Property

        Public Overloads Overrides Function ToString() As String
            Return String.Format("{0}[{1}]", Name, Id)
        End Function
    End Class

End Namespace
