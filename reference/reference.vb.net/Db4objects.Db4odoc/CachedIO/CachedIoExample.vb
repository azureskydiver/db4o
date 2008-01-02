' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com
Imports System
Imports System.IO
Imports Db4objects.Db4o.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.CachedIO

    Public Class CachedIOExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args As String())
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            SetObjects(configuration)
            GetObjects(configuration)
            configuration = ConfigureCache()
            SetObjects(configuration)
            GetObjects(configuration)
        End Sub
        ' end Main

        Private Shared Function ConfigureCache() As IConfiguration
            System.Console.WriteLine("Setting up cached io adapter")
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            ' new cached IO adapter with 256 pages 1024 bytes each
            Dim adapter As CachedIoAdapter = New CachedIoAdapter(New RandomAccessFileAdapter, 1024, 256)
            configuration.Io(adapter)
            Return configuration
        End Function
        ' end ConfigureCache

        Private Shared Function ConfigureRandomAccessAdapter() As IConfiguration
            System.Console.WriteLine("Setting up random access io adapter")
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.Io(New RandomAccessFileAdapter)
            Return configuration
        End Function
        ' end ConfigureRandomAccessAdapter

        Private Shared Sub SetObjects(ByVal configuration As IConfiguration)
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim i As Integer = 0
                While i < 50000
                    Dim pilot As Pilot = New Pilot("Pilot #" + i.ToString())
                    db.Set(pilot)
                    System.Math.Min(System.Threading.Interlocked.Increment(i), i - 1)
                End While
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                System.Console.WriteLine("Time elapsed for setting objects =" + diff.TotalMilliseconds.ToString() + " ms")
                dt1 = DateTime.UtcNow
                db.Commit()

                dt2 = DateTime.UtcNow
                diff = dt2 - dt1
                System.Console.WriteLine("Time elapsed for commit =" + diff.TotalMilliseconds.ToString() + " ms")
            Finally
                db.Close()
            End Try
        End Sub
        ' end SetObjects

        Private Shared Sub GetObjects(ByVal configuration As IConfiguration)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim result As IObjectSet = db.Get(Nothing)
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                System.Console.WriteLine("Time elapsed for the query =" + diff.TotalMilliseconds.ToString() + " ms")
                Console.WriteLine("Objects in the database: " + result.Count.ToString())
            Finally
                db.Close()
            End Try
        End Sub
        ' end GetObjects

    End Class
End Namespace