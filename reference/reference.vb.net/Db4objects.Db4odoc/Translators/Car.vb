' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System.Collections

Namespace Db4objects.Db4odoc.Translators
    Public Class Car
        Private _model As String

        Public Sub New(ByVal model As String)
            _model = model
        End Sub

        Public ReadOnly Property Model() As String
            Get
                Return _model
            End Get
        End Property

        Public Overloads Overrides Function ToString() As String
            Return _model
        End Function

    End Class
End Namespace
