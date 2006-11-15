' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.IO
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.IOs

    Public Class IOExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args() As String)
            SetObjects()
            GetObjectsInMem()
            GetObjects()
            TestLoggingAdapter()
        End Sub
        ' end Main

        Public Shared Sub SetObjects()
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim pilot As Pilot = New Pilot("Rubens Barrichello")
                db.Set(pilot)
            Finally
                db.Close()
            End Try
        End Sub
        ' end SetObjects

        Public Shared Sub GetObjectsInMem()
            System.Console.WriteLine("Setting up in-memory database")
            Dim adapter As MemoryIoAdapter = New MemoryIoAdapter()
            Try
                Dim raf As Sharpen.IO.RandomAccessFile = New Sharpen.IO.RandomAccessFile(YapFileName, "r")
                adapter.GrowBy(100)

                Dim len As Integer = CType(raf.Length(), Integer)
                Dim b() As Byte = New Byte(len) {}
                raf.Read(b, 0, len)
                adapter.Put(YapFileName, b)
                raf.Close()
            Catch ex As Exception
                System.Console.WriteLine("Exception: " + ex.Message)
            End Try

            Db4oFactory.Configure().Io(adapter)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim result As IObjectSet = db.Get(GetType(Pilot))
                System.Console.WriteLine("Read stored results through memory file")
                ListResult(result)
                Dim pilotNew As Pilot = New Pilot("Michael Schumacher")
                db.Set(pilotNew)
                System.Console.WriteLine("New pilot added")
            Finally
                db.Close()
            End Try
            System.Console.WriteLine("Writing the database back to disc")
            Dim dbstream() As Byte = adapter.Get(YapFileName)
            Try
                Dim file As Sharpen.IO.RandomAccessFile = New Sharpen.IO.RandomAccessFile(YapFileName, "rw")
                file.Write(dbstream)
                file.Close()
            Catch ioex As IOException
                System.Console.WriteLine("Exception: " + ioex.Message)
            End Try
        End Sub
        ' end GetObjectsInMem

        Public Shared Sub GetObjects()
            Db4oFactory.Configure().Io(New RandomAccessFileAdapter())
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim result As IObjectSet = db.Get(GetType(Pilot))
                System.Console.WriteLine("Read stored results through disc file")
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end GetObjects

        Public Shared Sub TestLoggingAdapter()
            Db4oFactory.Configure().Io(New LoggingAdapter())
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim pilot As Pilot = New Pilot("Michael Schumacher")
                db.Set(pilot)
                System.Console.WriteLine("New pilot added")
            Finally
                db.Close()
            End Try

            db = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim result As IObjectSet = db.Get(GetType(Pilot))
                ListResult(result)
            Finally
                db.Close()
            End Try
            Db4oFactory.Configure().Io(New RandomAccessFileAdapter())
        End Sub
        ' end TestLoggingAdapter

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace

