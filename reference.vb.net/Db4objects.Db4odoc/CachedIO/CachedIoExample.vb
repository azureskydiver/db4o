' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com
Imports System
Imports System.IO
Imports Db4objects.Db4o.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.CachedIO

    Public Class CachedIOExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args As String())
            SetObjects()
            GetObjects()
            ConfigureCache()
            SetObjects()
            GetObjects()
        End Sub
        ' end Main

        Public Shared Sub ConfigureCache()
            System.Console.WriteLine("Setting up cached io adapter")
            ' new cached IO adapter with 256 pages 1024 bytes each
            Dim adapter As CachedIoAdapter = New CachedIoAdapter(New RandomAccessFileAdapter, 1024, 256)
            Db4oFactory.Configure.Io(adapter)
        End Sub
        ' end ConfigureCache

        Public Shared Sub ConfigureRandomAccessAdapter()
            System.Console.WriteLine("Setting up random access io adapter")
            Db4oFactory.Configure.Io(New RandomAccessFileAdapter)
        End Sub
        ' end ConfigureRandomAccessAdapter

        Public Shared Sub SetObjects()
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
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
                System.Console.WriteLine("Time elapsed for setting objects =" + diff.Milliseconds.ToString() + " ms")
                dt1 = DateTime.UtcNow
                db.Commit()

                dt2 = DateTime.UtcNow
                diff = dt2 - dt1
                System.Console.WriteLine("Time elapsed for commit =" + diff.Milliseconds.ToString() + " ms")
            Finally
                db.Close()
            End Try
        End Sub
        ' end SetObjects

        Public Shared Sub GetObjects()
            Db4oFactory.Configure.Io(New RandomAccessFileAdapter)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim result As IObjectSet = db.Get(Nothing)
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                System.Console.WriteLine("Time elapsed for the query =" + diff.Milliseconds.ToString() + " ms")
                Console.WriteLine("Objects in the database: " + result.Count.ToString())
            Finally
                db.Close()
            End Try
        End Sub
        ' end GetObjects

    End Class
End Namespace