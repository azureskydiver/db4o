' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System.Collections

Namespace Db4objects.Db4odoc.Lists
    Public Class VerboseList
        Inherits ArrayList
        Public Overrides Function ToString() As String
            Dim output As String = ""
            Dim pilot As Pilot
            For Each pilot In Me
                If output.Equals("") Then
                    output = pilot.ToString()
                Else
                    output = output + "," + pilot.ToString()
                End If
            Next
            Return output
        End Function
    End Class
End Namespace

