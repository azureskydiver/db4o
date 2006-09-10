' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System
Imports System.IO
Imports com.db4o.io
Imports com.db4o
Imports com.db4o.query

Namespace com.db4odoc.f1.ios

    Public Class IOExample
        Inherits Util

        Public Shared Sub main(ByVal args() As String)
            SetObjects()
            GetObjectsInMem()
            GetObjects()
            TestLoggingAdapter()
        End Sub

        Public Shared Sub SetObjects()
            File.Delete(Util.YapFileName)
            Dim db As ObjectContainer = Db4o.OpenFile(Util.YapFileName)
            Try
                Dim pilot As Pilot = New Pilot("Rubens Barrichello")
                db.Set(pilot)
            Finally
                db.Close()
            End Try
        End Sub

        Public Shared Sub GetObjectsInMem()
            System.Console.WriteLine("Setting up in-memory database")
            Dim adapter As MemoryIoAdapter = New MemoryIoAdapter()
            Try
                Dim raf As j4o.io.RandomAccessFile = New j4o.io.RandomAccessFile(Util.YapFileName, "r")
                adapter.GrowBy(100)

                Dim len As Integer = CType(raf.Length(), Integer)
                Dim b() As Byte = New Byte(len) {}
                raf.Read(b, 0, len)
                adapter.Put(Util.YapFileName, b)
                raf.Close()
            Catch ex As Exception
                System.Console.WriteLine("Exception: " + ex.Message)
            End Try

            Db4o.Configure().Io(adapter)
            Dim db As ObjectContainer = Db4o.OpenFile(Util.YapFileName)
            Try
                Dim result As ObjectSet = db.Get(GetType(Pilot))
                System.Console.WriteLine("Read stored results through memory file")
                ListResult(result)
                Dim pilotNew As Pilot = New Pilot("Michael Schumacher")
                db.Set(pilotNew)
                System.Console.WriteLine("New pilot added")
            Finally
                db.Close()
            End Try
            System.Console.WriteLine("Writing the database back to disc")
            Dim dbstream() As Byte = adapter.Get(Util.YapFileName)
            Try
                Dim file As j4o.io.RandomAccessFile = New j4o.io.RandomAccessFile(Util.YapFileName, "rw")
                file.Write(dbstream)
                file.Close()
            Catch ioex As IOException
                System.Console.WriteLine("Exception: " + ioex.Message)
            End Try
        End Sub

        Public Shared Sub GetObjects()
            Db4o.Configure().Io(New RandomAccessFileAdapter())
            Dim db As ObjectContainer = Db4o.OpenFile(Util.YapFileName)
            Try
                Dim result As ObjectSet = db.Get(GetType(Pilot))
                System.Console.WriteLine("Read stored results through disc file")
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub

        Public Shared Sub TestLoggingAdapter()
            Db4o.Configure().Io(New LoggingAdapter())
            Dim db As ObjectContainer = Db4o.OpenFile(Util.YapFileName)
            Try
                Dim pilot As Pilot = New Pilot("Michael Schumacher")
                db.Set(pilot)
                System.Console.WriteLine("New pilot added")
            Finally
                db.Close()
            End Try

            db = Db4o.OpenFile(Util.YapFileName)
            Try
                Dim result As ObjectSet = db.Get(GetType(Pilot))
                ListResult(result)
            Finally
                db.Close()
            End Try
            Db4o.Configure().Io(New RandomAccessFileAdapter())
        End Sub

    End Class
End Namespace

