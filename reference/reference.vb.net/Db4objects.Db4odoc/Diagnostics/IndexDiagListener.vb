' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Diagnostic

Namespace Db4objects.Db4odoc.Diagnostics
    Public Class IndexDiagListener

        Implements IDiagnosticListener
        Public Sub OnDiagnostic(ByVal d As IDiagnostic) Implements IDiagnosticListener.OnDiagnostic
            If TypeOf d Is LoadedFromClassIndex Then
                System.Console.WriteLine(d)
            End If
        End Sub
    End Class
End Namespace
