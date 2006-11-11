' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System

Namespace Db4objects.Db4odoc.Diagnostics
    Public Class Empty
        Public Sub New()
        End Sub

        Public Function CurrentTime() As String
            Dim dt As DateTime = DateTime.Now
            Dim time As String = dt.ToString("d")
            Return time
        End Function

        Public Overrides Function ToString() As String
            Return CurrentTime()
        End Function
    End Class
End Namespace
