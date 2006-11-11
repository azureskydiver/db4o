' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System

Namespace Db4objects.Db4odoc.Refactoring
    Public Class PilotNew
        Private _identity As String
        Private _points As Integer

        Public Sub New(ByVal name As String, ByVal points As Integer)
            _identity = name
            _points = points
        End Sub

        Public ReadOnly Property Identity() As String
            Get
                Return _identity
            End Get
        End Property

        Public Overrides Function ToString() As String
            Return String.Format("{0}/{1}", _identity, _points)
        End Function
    End Class
End Namespace
