' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports com.db4o

Namespace com.db4odoc.f1.selpersist
    Public Class Test
        <Transient()> _
        Dim _transientField As String
        Dim _persistentField As String

        Public Sub New(ByVal transientField As String, ByVal persistentField As String)
            _transientField = transientField
            _persistentField = persistentField
        End Sub

        Public Overrides Function ToString() As String
            Return "Test: persistent: " + _persistentField + ", transient: " + _transientField
        End Function
    End Class
End Namespace
