Namespace com.db4odoc.f1.clientserver
    ''' <summary>
    ''' Configuration used for StartServer and StopServer.
    ''' </summary>
    Public Class ServerConfiguration
        ''' <summary>
        ''' the host to be used.
        ''' If you want to run the client server examples on two computers,
        ''' enter the computer name of the one that you want to use as server. 
        ''' </summary>
        Public Const HOST As String = "localhost"

        ''' <summary>
        ''' the database file to be used by the server.
        ''' </summary>
        Public Const FILE As String = "formula1.yap"

        ''' <summary>
        ''' the port to be used by the server.
        ''' </summary>
        Public Const PORT As Integer = 4488

        ''' <summary>
        ''' the user name for access control.
        ''' </summary>
        Public Const USER As String = "db4o"

        ''' <summary>
        ''' the pasword for access control.
        ''' </summary>
        Public Const PASS As String = "db4o"

    End Class
End Namespace
