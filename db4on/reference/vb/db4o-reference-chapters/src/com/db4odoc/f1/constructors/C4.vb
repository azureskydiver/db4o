' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System

Namespace com.db4odoc.f1.constructors
    <Serializable()> Class C4
        Private s As String
        <NonSerialized()> Private i As Integer

        Private Sub New(ByVal s As String)
            Me.s = s
            Me.i = s.Length
        End Sub

        Public Overrides Function ToString() As String
            Return s + i.ToString()
        End Function
    End Class
End Namespace
