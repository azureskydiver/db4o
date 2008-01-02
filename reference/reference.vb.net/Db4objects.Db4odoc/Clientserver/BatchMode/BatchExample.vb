' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Imports System
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config

Namespace Db4objects.Db4odoc.ClientServer.BatchMode

    Class BatchExample
        Private Const File As String = "reference.db4o"
        Private Const Port As Integer = &HDB40
        Private Const User As String = "db4o"
        Private Const Password As String = "db4o"
        Private Const Host As String = "localhost"
        Private Const NoOfObjects As Integer = 1000

        Public Shared Sub Main(ByVal Args As String())
            Dim db4oServer As IObjectServer = Db4oFactory.OpenServer(File, Port)
            Try
                db4oServer.GrantAccess(User, Password)
                Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
                FillUpDb(configuration)
                configuration.ClientServer.BatchMessages(True)
                FillUpDb(configuration)
            Finally
                db4oServer.Close()
            End Try
        End Sub
        ' end Main

        Private Shared Sub FillUpDb(ByVal configuration As IConfiguration)
            Dim container As IObjectContainer = Db4oFactory.OpenClient(configuration, Host, Port, User, Password)
            Try
                Console.WriteLine("Testing inserts")
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim i As Integer = 0
                While i < NoOfObjects
                    Dim pilot As Pilot = New Pilot("pilot #" + i.ToString(), i)
                    container.Set(pilot)
                    System.Math.Min(System.Threading.Interlocked.Increment(i), i - 1)
                End While
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                Console.WriteLine("Operation time: " + diff.TotalMilliseconds.ToString() + " ms.")
            Finally
                container.Close()
            End Try
        End Sub
        ' end FillUpDb

    End Class
End Namespace