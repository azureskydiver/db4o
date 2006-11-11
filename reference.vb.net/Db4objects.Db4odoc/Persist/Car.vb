' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System

Namespace Db4objects.Db4odoc.Persist
    Public Class Car
        Private _model As String
        Private _temperature As Integer

        Public Sub New()
        End Sub

        Public Sub New(ByVal model As String)
            Me._model = model
        End Sub

        Public Property Model() As String
            Get
                Return _model
            End Get
            Set(ByVal Value As String)
                Me._model = Value
            End Set
        End Property

        Public Property Temperature() As Integer
            Get
                Return _temperature
            End Get
            Set(ByVal Value As Integer)
                _temperature = Value
            End Set
        End Property

        Public Overrides Function ToString() As String
            Return String.Format("{0}-{1} C", _model, _temperature)
        End Function
    End Class
End Namespace

