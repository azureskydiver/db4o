' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports Db4objects.Db4o

Namespace Db4objects.Db4odoc.Constructors
    Class C2
        <Transient()> Private x As String
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
