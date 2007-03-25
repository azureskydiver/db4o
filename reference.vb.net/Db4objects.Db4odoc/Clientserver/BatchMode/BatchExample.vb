' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Imports System
Imports Db4objects.Db4o
Namespace Db4objects.Db4odoc.ClientServer.BatchMode

    Class BatchExample
        Private Const FILE As String = "test.yap"
        Private Const PORT As Integer = &HDB40
        Private Const USER As String = "db4o"
        Private Const PASS As String = "db4o"
        Private Const HOST As String = "localhost"
        Private Const NO_OF_OBJECTS As Integer = 1000

        Public Shared Sub Main(ByVal Args As String())
            Dim db4oServer As IObjectServer = Db4oFactory.OpenServer(FILE, PORT)
            Try
                db4oServer.GrantAccess(USER, PASS)
                Dim container As IObjectContainer = Db4oFactory.OpenClient(HOST, PORT, USER, PASS)
                Try
                    FillUpDb(container)
                    container.Ext.Configure.ClientServer.BatchMessages(True)
                    FillUpDb(container)
                Finally
                    container.Close()
                End Try
            Finally
                db4oServer.Close()
            End Try
        End Sub
        ' end Main

        Private Shared Sub FillUpDb(ByVal container As IObjectContainer)
            Console.WriteLine("Testing inserts")
            Dim dt1 As DateTime = DateTime.UtcNow
            Dim i As Integer = 0
            While i < NO_OF_OBJECTS
                Dim pilot As Pilot = New Pilot("pilot #" + i.ToString(), i)
                container.Set(pilot)
                System.Math.Min(System.Threading.Interlocked.Increment(i), i - 1)
            End While
            Dim dt2 As DateTime = DateTime.UtcNow
            Dim diff As TimeSpan = dt2 - dt1
            Console.WriteLine("Operation time: " + diff.Milliseconds.ToString() + " ms.")
        End Sub
        ' end FillUpDb

    End Class
End Namespace