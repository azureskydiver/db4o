' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System
Imports System.Collections
Imports com.db4odoc.f1.evaluations

Namespace com.db4odoc.f1.lists

    Public Class VerboseList
        Inherits ArrayList
        Public Overrides Function ToString() As String
            Dim output As String = ""
            Dim pilot As evaluations.Pilot
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

