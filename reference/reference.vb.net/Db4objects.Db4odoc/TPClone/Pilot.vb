' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Activation
Imports Db4objects.Db4o.TA

Namespace Db4objects.Db4odoc.TPClone

    Public Class Pilot
        Implements IActivatable
        Private _name As String
        <Transient()> Private _activator As Db4objects.Db4o.Activation.IActivator

        Public Sub New(ByVal name As String)
            _name = name
        End Sub

        ' Bind the class to an object container
        Public Sub Bind(ByVal activator As Activation.IActivator) Implements IActivatable.Bind
            If _activator Is activator Then
                Return
            End If
            If Not (activator Is Nothing Or _activator Is Nothing) Then
                Throw New System.InvalidOperationException()
            End If
            _activator = activator
        End Sub

        ' activate the object fields
        Public Sub Activate(ByVal purpose As ActivationPurpose) Implements IActivatable.Activate
            If _activator Is Nothing Then
                Return
            End If
            _activator.Activate(ActivationPurpose.Read)
        End Sub

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
            ' use Name property, which already contains activation call
            Return Name
        End Function

    End Class
End Namespace