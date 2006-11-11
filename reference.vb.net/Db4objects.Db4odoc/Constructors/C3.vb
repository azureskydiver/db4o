' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System

Namespace Db4objects.Db4odoc.Constructors
    Class C3
        Private s As String
        Private i As Integer

        Private Sub New(ByVal s As String)
            Me.s = s
            Me.i = s.Length
        End Sub

        Public Overrides Function ToString() As String
            Return s + i.ToString()
        End Function
    End Class
End Namespace
