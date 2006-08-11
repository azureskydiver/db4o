' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System
Imports System.Diagnostics
Imports com.db4o.diagnostic
Imports com.db4o.diagnostic.LoadedFromClassIndex

Namespace com.db4odoc.f1.diagnostics
    Public Class IndexDiagListener
        Implements DiagnosticListener
        Public Sub OnDiagnostic(ByVal d As Diagnostic) Implements DiagnosticListener.OnDiagnostic
            Dim dbase As DiagnosticBase = DirectCast(d, DiagnosticBase)
            If (dbase.Problem().StartsWith("Query candidate set could not be loaded from a field index") = True) Then
                System.Diagnostics.Trace.WriteLine(d)
            End If
        End Sub
    End Class
End Namespace
