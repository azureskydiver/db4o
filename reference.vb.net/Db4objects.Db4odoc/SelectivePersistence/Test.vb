' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports Db4objects.Db4o

Namespace Db4objects.Db4odoc.SelectivePersistence
    Public Class Test
        <Transient()> Dim _transientField As String
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
