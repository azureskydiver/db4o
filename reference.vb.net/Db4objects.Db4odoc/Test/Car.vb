' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System

Namespace Db4objects.Db4odoc.Test
    Public Class Car
        Public _model As String
        Public _pilot As Pilot

        Public Sub New(ByVal model As String)
            _model = model
            _pilot = Nothing
        End Sub

        Public Sub New(ByVal model As String, ByVal pilot As Pilot)
            _model = model
            _pilot = pilot
        End Sub

        Public Property Pilot() As Pilot
            Get
                Return _pilot
            End Get
            Set(ByVal value As Pilot)
                _pilot = value
            End Set
        End Property

        Public Overrides Function ToString() As String
            Return String.Format("{0}({1})", _model, _pilot)
        End Function
    End Class


End Namespace