' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports System.Threading
Imports Db4objects.Db4o
Namespace Db4objects.Db4odoc.Concurrency

    Class ConcurrencyExample
        Private Shared ReadOnly YapFileName As String = "formula1.yap"
        Private Shared _server As IObjectServer

        Public Shared Sub Main(ByVal args As String())
            Connect()
            Try
                SavePilots()
                ModifyPilotsOptimistic()
                ModifyPilotsPessimistic()
            Finally
                Disconnect()
            End Try
        End Sub
        ' end Main

        Public Shared Sub Connect()
            If _server Is Nothing Then
                File.Delete(YapFileName)
                Db4oFactory.Configure.GenerateVersionNumbers(Int32.MaxValue)
                _server = Db4oFactory.OpenServer(YapFileName, 0)
            End If
        End Sub
        ' end Connect

        Public Shared Sub Disconnect()
            _server.Close()
        End Sub
        ' end Disconnect

        Public Shared Sub SavePilots()
            Dim db As IObjectContainer = _server.OpenClient
            Try
                Dim pilot As Pilot = New Pilot("Kimi Raikkonnen", 0)
                db.Set(pilot)
                pilot = New Pilot("David Barrichello", 0)
                db.Set(pilot)
                pilot = New Pilot("David Coulthard", 0)
                db.Set(pilot)
            Finally
                db.Close()
            End Try
        End Sub
        ' end SavePilots

        Public Shared Sub ModifyPilotsOptimistic()
            Console.WriteLine("Optimistic locking example")
            ' create threads for concurrent modifications
            Dim t1 As OptimisticThread = New OptimisticThread("t1: ", _server)
            Dim t2 As OptimisticThread = New OptimisticThread("t2: ", _server)
            Dim thread1 As Thread = New Thread(New ThreadStart(AddressOf t1.Run))
            Dim thread2 As Thread = New Thread(New ThreadStart(AddressOf t2.Run))
            RunThreads(thread1, thread2)
        End Sub
        ' end ModifyPilotsOptimistic

        Public Shared Sub ModifyPilotsPessimistic()
            Console.WriteLine()
            Console.WriteLine("Pessimistic locking example")
            ' create threads for concurrent modifications
            Dim t1 As PessimisticThread = New PessimisticThread("t1: ", _server)
            Dim t2 As PessimisticThread = New PessimisticThread("t2: ", _server)
            Dim thread1 As Thread = New Thread(New ThreadStart(AddressOf t1.Run))
            Dim thread2 As Thread = New Thread(New ThreadStart(AddressOf t2.Run))
            RunThreads(thread1, thread2)
        End Sub
        ' end ModifyPilotsPessimistic

        Public Shared Sub RunThreads(ByVal thread1 As Thread, ByVal thread2 As Thread)
            thread1.Start()
            thread2.Start()
            Dim thread1IsAlive As Boolean = True
            Dim thread2IsAlive As Boolean = True
            Do
                If thread1IsAlive AndAlso Not thread1.IsAlive Then
                    thread1IsAlive = False
                    Console.WriteLine("t1 is dead.")
                End If
                If thread2IsAlive AndAlso Not thread2.IsAlive Then
                    thread2IsAlive = False
                    Console.WriteLine("t2 is dead.")
                End If
            Loop While thread1IsAlive OrElse thread2IsAlive
        End Sub
        ' end RunThreads

    End Class
End Namespace