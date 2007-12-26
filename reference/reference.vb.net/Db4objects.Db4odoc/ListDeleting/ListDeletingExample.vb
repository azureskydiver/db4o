' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Imports System
Imports System.Collections.Generic
Imports System.Diagnostics
Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.ListDeleting

    Class ListDeletingExample
        Public Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args As String())
            FillUpDb(1)
            DeleteTest()
            FillUpDb(1)
            RemoveAndDeleteTest()
            FillUpDb(1)
            RemoveTest()
        End Sub
        ' end Main


        Private Shared Sub RemoveAndDeleteTest()
            ' set update depth to 1 as we only 
            ' modify List field
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ObjectClass(GetType(ListObject)).UpdateDepth(1)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim result As IList(Of ListObject) = db.Query(Of ListObject)(GetType(ListObject))
                If result.Count > 0 Then
                    ' retrieve a ListObject
                    Dim lo1 As ListObject = result(0)
                    ' create a copy of the objects list
                    ' to memorize the objects to be deleted
                    Dim tempList As List(Of DataObject) = New List(Of DataObject)(lo1.Data)
                    ' remove all the objects from the list
                    lo1.Data.RemoveRange(0, lo1.Data.Count)
                    db.Set(lo1)
                    ' and delete them from the database
                    Dim obj As DataObject
                    For Each obj In tempList
                        db.Delete(obj)
                    Next
                    ' remove all the objects from the list
                    lo1.Data.RemoveRange(0, lo1.Data.Count)
                    db.Set(lo1)
                End If
            Finally
                db.Close()
            End Try
            ' check DataObjects in the list
            ' and DataObjects in the database
            db = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim result As IList(Of ListObject) = db.Query(Of ListObject)(GetType(ListObject))
                If result.Count > 0 Then
                    Dim lo1 As ListObject = result(0)
                    Console.WriteLine("DataObjects in the list: " + lo1.Data.Count.ToString())
                End If
                Dim removedObjects As IList(Of DataObject) = db.Query(Of DataObject)(GetType(DataObject))
                Console.WriteLine("DataObjects in the database: " + removedObjects.Count.ToString())
            Finally
                db.Close()
            End Try
        End Sub
        ' end RemoveAndDeleteTest

        Private Shared Sub RemoveTest()
            ' set update depth to 1 as we only 
            ' modify List field
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ObjectClass(GetType(ListObject)).UpdateDepth(1)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim result As IList(Of ListObject) = db.Query(Of ListObject)(GetType(ListObject))
                If result.Count > 0 Then
                    ' retrieve a ListObject
                    Dim lo1 As ListObject = result(0)
                    ' remove all the objects from the list
                    lo1.Data.RemoveRange(0, lo1.Data.Count)
                    db.Set(lo1)
                End If
            Finally
                db.Close()
            End Try
            ' check DataObjects in the list
            ' and DataObjects in the database
            db = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim result As IList(Of ListObject) = db.Query(Of ListObject)(GetType(ListObject))
                If result.Count > 0 Then
                    Dim lo1 As ListObject = result(0)
                    Console.WriteLine("DataObjects in the list: " + lo1.Data.Count.ToString())
                End If
                Dim removedObjects As IList(Of DataObject) = db.Query(Of DataObject)(GetType(DataObject))
                Console.WriteLine("DataObjects in the database: " + removedObjects.Count.ToString())
            Finally
                db.Close()
            End Try
        End Sub
        ' end RemoveTest

        Private Shared Sub DeleteTest()
            ' set cascadeOnDelete in order to delete member objects
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ObjectClass(GetType(ListObject)).CascadeOnDelete(True)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim result As IList(Of ListObject) = db.Query(Of ListObject)(GetType(ListObject))
                If result.Count > 0 Then
                    ' retrieve a ListObject
                    Dim lo1 As ListObject = result(0)
                    ' delete the ListObject with all the field objects
                    db.Delete(lo1)
                End If
            Finally
                db.Close()
            End Try
            ' check ListObjects and DataObjects in the database
            db = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim listObjects As IList(Of ListObject) = db.Query(Of ListObject)(GetType(ListObject))
                Console.WriteLine("ListObjects in the database: " + listObjects.Count.ToString())
                Dim dataObjects As IList(Of DataObject) = db.Query(Of DataObject)(GetType(DataObject))
                Console.WriteLine("DataObjects in the database: " + dataObjects.Count.ToString())
            Finally
                db.Close()
            End Try
        End Sub
        ' end DeleteTest

        Private Shared Sub FillUpDb(ByVal listCount As Integer)
            Dim dataCount As Integer = 50
            Dim sw As Stopwatch = New Stopwatch
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                sw.Start()
                Dim i As Integer = 0
                While i < listCount
                    Dim lo As ListObject = New ListObject
                    lo.Name = "list" + i.ToString("00")
                    Dim j As Integer = 0
                    While j < dataCount
                        Dim dataObject As DataObject = New DataObject
                        dataObject.Name = "data" + j.ToString("00000")
                        dataObject.Data = DateTime.Now.ToString + " ---- Data Object " + j.ToString("00000")
                        lo.Data.Add(dataObject)
                        System.Math.Min(System.Threading.Interlocked.Increment(j), j - 1)
                    End While
                    db.Set(lo)
                    System.Math.Min(System.Threading.Interlocked.Increment(i), i - 1)
                End While
                sw.Stop()
            Finally
                db.Close()
            End Try
            Console.WriteLine("Completed {0} lists of {1} objects each.", listCount, dataCount)
            Console.WriteLine("Elapsed time: {0}", sw.Elapsed.ToString)
        End Sub
        ' end FillUpDb

    End Class
End Namespace