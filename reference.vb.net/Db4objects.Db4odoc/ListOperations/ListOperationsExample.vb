' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Imports System
Imports System.Collections.Generic
Imports System.Diagnostics
Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.ListOperations

    Class ListOperationsExample
        Public Const DbFile As String = "Test.db"

        Public Shared Sub Main(ByVal args As String())
            FillUpDb(2)
            RemoveInsert()
            CheckResults()
            UpdateObject()
            CheckResults()
        End Sub
        ' end Main

        Private Shared Sub FillUpDb(ByVal listCount As Integer)
            Dim dataCount As Integer = 50000
            Dim sw As Stopwatch = New Stopwatch
            File.Delete(DbFile)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(DbFile)
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

        Private Shared Sub CheckResults()
            Dim sw As Stopwatch = New Stopwatch
            Dim db As IObjectContainer = Db4oFactory.OpenFile(DbFile)
            Try
                Dim result As IList(Of ListObject) = db.Query(Of ListObject)()
                If result.Count > 0 Then
                    ' activation depth should be enough to activate 
                    ' ListObject, DataObject and its list members
                    Dim activationDepth As Integer = 3
                    db.Ext.Configure.ActivationDepth(activationDepth)
                    Console.WriteLine("Result count was {0}, looping with activation depth {1}", result.Count, activationDepth)
                    sw.Start()
                    For Each lo As ListObject In result
                        Console.WriteLine("ListObj {0} has {1} objects", lo.Name, (Microsoft.VisualBasic.IIf((lo.Data Is Nothing), "<null>", lo.Data.Count.ToString)))
                        Console.WriteLine(" --- {0} at index 0", (Microsoft.VisualBasic.IIf((Not (lo.Data Is Nothing) AndAlso lo.Data.Count > 0), lo.Data(0).ToString, "<null>")))
                    Next
                    sw.Stop()
                End If
            Finally
                db.Close()
            End Try
            Console.WriteLine("Activation took {0}", sw.Elapsed.ToString)
        End Sub
        ' end CheckResults

        Private Shared Sub RemoveInsert()
            Dim sw As Stopwatch = New Stopwatch
            Dim db As IObjectContainer = Db4oFactory.OpenFile(DbFile)
            Try
                ' set update depth to 1 for the quickest execution
                db.Ext.Configure.UpdateDepth(1)
                Dim result As IList(Of ListObject) = db.Query(Of ListObject)()
                If result.Count = 2 Then
                    ' retrieve 2 ListObjects
                    Dim lo1 As ListObject = result(0)
                    Dim lo2 As ListObject = result(1)
                    Dim dataObject As DataObject = lo1.Data(0)
                    ' move the first object from the first
                    ' ListObject to the second ListObject
                    lo1.Data.Remove(dataObject)
                    lo2.Data.Add(dataObject)
                    Console.WriteLine("Removed from the first list, count is {0}, setting data...", lo1.Data.Count)
                    Console.WriteLine("Added to the second list, count is {0}, setting data...", lo2.Data.Count)
                    sw.Start()
                    db.Set(lo1)
                    db.Set(lo2)
                    db.Commit()
                    sw.Stop()
                End If
            Finally
                db.Close()
            End Try
            Console.WriteLine("Storing took {0}", sw.Elapsed.ToString)
        End Sub
        ' end RemoveInsert

        Private Shared Sub UpdateObject()
            Dim sw As Stopwatch = New Stopwatch
            Dim db As IObjectContainer = Db4oFactory.OpenFile(DbFile)
            Try
                ' we can set update depth to 0 
                ' as we update only the current object
                db.Ext.Configure.UpdateDepth(0)
                Dim result As IList(Of ListObject) = db.Query(Of ListObject)()
                If result.Count = 2 Then
                    Dim lo1 As ListObject = result(0)
                    Dim dataobject As DataObject = lo1.Data(0)
                    dataobject.Name = "Updated"
                    dataobject.Data = DateTime.Now.ToString + " ---- Updated Object "
                    Console.WriteLine("Updated list {0} dataobject {1}", lo1.Name, lo1.Data(0))
                    sw.Start()
                    db.Set(dataobject)
                    db.Commit()
                    sw.Stop()
                End If
            Finally
                db.Close()
            End Try
            Console.WriteLine("Storing took {0}", sw.Elapsed.ToString)
        End Sub
        ' end UpdateObject

    End Class
End Namespace