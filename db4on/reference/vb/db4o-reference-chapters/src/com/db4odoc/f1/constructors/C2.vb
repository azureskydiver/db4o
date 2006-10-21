' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports com.db4o.config

Namespace com.db4odoc.f1.constructors
    <System.Serializable()> Class C2
        <System.NonSerialized()> Private x As String
        Private s As String

        Private Sub New(ByVal s As String)
            Me.s = s
            Me.x = "x"
        End Sub

        Public Overrides Function ToString() As String
            Return s + x.Length.ToString
        End Function
    End Class
End Namespace
