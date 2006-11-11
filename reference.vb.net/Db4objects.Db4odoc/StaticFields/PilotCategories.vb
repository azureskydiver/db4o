' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com
Imports System

Namespace Db4objects.Db4odoc.StaticFields
    Public Class PilotCategories
        Private _qualification As String = Nothing
        Public Shared WINNER As PilotCategories = New PilotCategories("WINNER")
        Public Shared TALENTED As PilotCategories = New PilotCategories("TALENTED")
        Public Shared AVERAGE As PilotCategories = New PilotCategories("AVERAGE")
        Public Shared DISQUALIFIED As PilotCategories = New PilotCategories("DISQUALIFIED")

        Private Sub New(ByVal qualification As String)
            Me._qualification = qualification
        End Sub

        Public Sub New()

        End Sub

        Public Sub TestChange(ByVal qualification As String)
            Me._qualification = qualification
        End Sub

        Public Overrides Function ToString() As String
            Return _qualification
        End Function
    End Class
End Namespace

