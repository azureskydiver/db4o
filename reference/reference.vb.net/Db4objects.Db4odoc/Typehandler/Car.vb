' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 

Imports System.Text

Namespace Db4objects.Db4odoc.Typehandler
    Public Class Car
        Private model As StringBuilder

        Public Sub New(ByVal model As String)
            Me.model = IIf(model Is Nothing, Nothing, New StringBuilder(model))
        End Sub


        Public Function getModel() As String
            Return model.ToString()
        End Function

        Public Overloads Overrides Function ToString() As String
            Return IIf(model Is Nothing, Nothing, model.ToString())
        End Function
    End Class
End Namespace
