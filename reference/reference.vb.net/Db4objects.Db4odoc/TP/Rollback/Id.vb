' Copyright (C) 2004 - 2008 db4objects Inc. http://www.db4o.com 

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Activation
Imports Db4objects.Db4o.TA

Namespace Db4objects.Db4odoc.TP.Rollback
    Public Class Id
        Implements IActivatable
        Private _number As Integer = 0

        <Transient()> _
        Private _activator As IActivator

        Public Sub New(ByVal number As Integer)
            _number = number
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

        Public Sub Change(ByVal number As Integer)
            Activate(ActivationPurpose.Write)
            _number = number
        End Sub

        Public Overloads Overrides Function ToString() As String
            Activate(ActivationPurpose.Read)
            Return _number.ToString()
        End Function
    End Class

End Namespace
