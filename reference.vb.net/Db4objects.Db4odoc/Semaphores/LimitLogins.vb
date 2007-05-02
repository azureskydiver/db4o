' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports Db4objects.Db4o

Namespace Db4objects.Db4odoc.Semaphores
    ' 	
    ' 	 This class demonstrates the use of semaphores to limit the
    ' 	 number of logins to a server.
    ' 	
    Public Class LimitLogins

        Shared ReadOnly Host As String = "localhost"
        Shared ReadOnly Port As Integer = 4455
        Shared ReadOnly User As String = "db4o"
        Shared ReadOnly Password As String = "db4o"

        Shared ReadOnly MaximumUsers As Integer = 10

        Public Shared Function Login() As IObjectContainer

            Dim objectContainer As IObjectContainer
            Try
                objectContainer = Db4oFactory.OpenClient(Host, Port, User, Password)
            Catch e As IOException
                Return Nothing
            End Try

            Dim allowedToLogin As Boolean = False

            Dim i As Integer
            For i = 0 To MaximumUsers - 1 Step i + 1
                If objectContainer.Ext().SetSemaphore("max_user_check_" + i.ToString(), 0) Then
                    allowedToLogin = True
                    Exit For
                End If
            Next

            If Not allowedToLogin Then
                objectContainer.Close()
                Return Nothing
            End If

            Return objectContainer
        End Function
    End Class
End Namespace
