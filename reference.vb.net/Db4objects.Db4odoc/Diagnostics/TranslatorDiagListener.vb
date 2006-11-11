' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System
Imports System.Diagnostics
Imports Db4objects.Db4o.Diagnostic

Namespace Db4objects.Db4odoc.Diagnostics
    Public Class TranslatorDiagListener
        Inherits DiagnosticToTrace
        Public Overrides Sub OnDiagnostic(ByVal d As IDiagnostic)
            If TypeOf d Is DescendIntoTranslator Then
                System.Console.WriteLine(d)
            End If
        End Sub
    End Class
End Namespace
