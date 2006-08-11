' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System
Imports System.Diagnostics
Imports com.db4o.diagnostic
Imports com.db4o.diagnostic.DescendIntoTranslator


Namespace com.db4odoc.f1.diagnostics
    Public Class TranslatorDiagListener
        Inherits DiagnosticToConsole
        Public Overrides Sub OnDiagnostic(ByVal d As Diagnostic)
            Dim dbase As DiagnosticBase = DirectCast(d, DiagnosticBase)
            If (dbase.Problem().StartsWith("Query descends") = True) Then
                System.Diagnostics.Trace.WriteLine(d)
            End If
        End Sub
    End Class
End Namespace
