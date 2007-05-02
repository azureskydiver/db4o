' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Namespace Db4objects.Db4odoc.ClientServer
    ''' <summary>
    ''' Configuration used for StartServer and StopServer.
    ''' </summary>
    Public Class ServerConfiguration
        ''' <summary>
        ''' the host to be used.
        ''' If you want to run the client server examples on two computers,
        ''' enter the computer name of the one that you want to use as server. 
        ''' </summary>
        Public Const Host As String = "localhost"

        ''' <summary>
        ''' the database file to be used by the server.
        ''' </summary>
        Public Const File As String = "reference.db4o"

        ''' <summary>
        ''' the port to be used by the server.
        ''' </summary>
        Public Const Port As Integer = &HDB40

        ''' <summary>
        ''' the user name for access control.
        ''' </summary>
        Public Const User As String = "db4o"

        ''' <summary>
        ''' the pasword for access control.
        ''' </summary>
        Public Const Password As String = "db4o"

    End Class
End Namespace
