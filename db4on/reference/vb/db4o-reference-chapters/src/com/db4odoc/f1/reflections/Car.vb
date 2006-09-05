' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System

Namespace com.db4odoc.f1.reflections

    Public Class Car
        Dim _model As String

        Public Sub New(ByVal model As String)
            _model = model
        End Sub

        Public Overrides Function ToString() As String
            Return _model
        End Function
    End Class
End Namespace