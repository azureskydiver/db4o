' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports System.Collections
Imports com.db4o
Imports com.db4o.query
Imports com.db4o.types

Namespace com.db4odoc.f1.activating

    Public Class ActivationExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args() As String)
            TestActivationDefault()
            TestActivationConfig()
            TestCascadeActivate()
            TestMaxActivate()
            TestMinActivate()
            TestActivateDeactivate()
            TestCollectionDef()
            TestCollectionActivation()
        End Sub
        ' end Main

        Public Shared Sub StoreSensorPanel()
            File.Delete(YapFileName)
            Dim db As ObjectContainer = Db4o.OpenFile(YapFileName)
            Try
                ' create a linked list with length 10
                Dim list As SensorPanel = New SensorPanel().CreateList(10)
                ' store all elements with one statement, since all elements are new		
                db.Set(list)
            Finally
                db.Close()
            End Try
        End Sub
        ' end StoreSensorPanel

        Public Shared Sub TestActivationConfig()
            StoreSensorPanel()
            Dim db As ObjectContainer = Db4o.OpenFile(YapFileName)
            Try
                db.Ext().Configure().ActivationDepth(1)
                Console.WriteLine("Object container activation depth = 1")
                Dim result As ObjectSet = db.Get(New SensorPanel(1))
                ListResult(result)
                If result.Count > 0 Then
                    Dim sensor As SensorPanel = CType(result(0), SensorPanel)
                    Dim nextSensor As SensorPanel = sensor.NextSensor
                    While Not nextSensor Is Nothing
                        Console.WriteLine(nextSensor)
                        nextSensor = nextSensor.NextSensor
                    End While
                End If
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestActivationConfig

        Public Shared Sub TestActivationDefault()
            StoreSensorPanel()
            Dim db As ObjectContainer = Db4o.OpenFile(YapFileName)
            Try
                Console.WriteLine("Default activation depth")
                Dim result As ObjectSet = db.Get(New SensorPanel(1))
                ListResult(result)
                If result.Count > 0 Then
                    Dim sensor As SensorPanel = CType(result(0), SensorPanel)
                    Dim nextSensor As SensorPanel = sensor.NextSensor
                    While Not nextSensor Is Nothing
                        Console.WriteLine(nextSensor)
                        nextSensor = nextSensor.NextSensor
                    End While
                End If
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestActivationDefault

        Public Shared Sub TestCascadeActivate()
            StoreSensorPanel()
            Dim db As ObjectContainer = Db4o.OpenFile(YapFileName)
            db.Ext().Configure().ObjectClass(GetType(SensorPanel)).CascadeOnActivate(True)
            Try
                Console.WriteLine("Cascade activation")
                Dim result As ObjectSet = db.Get(New SensorPanel(1))
                ListResult(result)
                If result.Count > 0 Then
                    Dim sensor As SensorPanel = CType(result(0), SensorPanel)
                    Dim nextSensor As SensorPanel = sensor.NextSensor
                    While Not nextSensor Is Nothing
                        Console.WriteLine(nextSensor)
                        nextSensor = nextSensor.NextSensor
                    End While
                End If
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestCascadeActivate

        Public Shared Sub TestMinActivate()
            StoreSensorPanel()
            ' note that the minimum applies for *all* instances in the hierarchy
            ' the system ensures that every instantiated List object will have it's 
            ' members set to a depth of 1
            Db4o.Configure().ObjectClass(GetType(SensorPanel)).MinimumActivationDepth(1)
            Dim db As ObjectContainer = Db4o.OpenFile(YapFileName)
            Try
                Console.WriteLine("Minimum activation depth = 1")
                Dim result As ObjectSet = db.Get(New SensorPanel(1))
                ListResult(result)
                If result.Count > 0 Then
                    Dim sensor As SensorPanel = CType(result(0), SensorPanel)
                    Dim nextSensor As SensorPanel = sensor.NextSensor
                    While Not nextSensor Is Nothing
                        Console.WriteLine(nextSensor)
                        nextSensor = nextSensor.NextSensor
                    End While
                End If
            Finally
                db.Close()
                Db4o.Configure().ObjectClass(GetType(SensorPanel)).MinimumActivationDepth(0)
            End Try
        End Sub
        ' end TestMinActivate

        Public Shared Sub TestMaxActivate()
            StoreSensorPanel()
            ' note that the maximum is applied to the retrieved root object and limits activation
            ' further down the hierarchy
            Db4o.Configure().ObjectClass(GetType(SensorPanel)).MaximumActivationDepth(2)

            Dim db As ObjectContainer = Db4o.OpenFile(YapFileName)
            Try
                Console.WriteLine("Maximum activation depth = 2 (default = 5)")
                Dim result As ObjectSet = db.Get(New SensorPanel(1))
                ListResult(result)
                If result.Count > 0 Then
                    Dim sensor As SensorPanel = CType(result(0), SensorPanel)
                    Dim nextSensor As SensorPanel = sensor.NextSensor
                    While Not nextSensor Is Nothing
                        Console.WriteLine(nextSensor)
                        nextSensor = nextSensor.NextSensor
                    End While
                End If
            Finally
                db.Close()
                Db4o.Configure().ObjectClass(GetType(SensorPanel)).MaximumActivationDepth(Int32.MaxValue)
            End Try
        End Sub
        ' end TestMaxActivate

        Public Shared Sub TestActivateDeactivate()
            StoreSensorPanel()
            Dim db As ObjectContainer = Db4o.OpenFile(YapFileName)
            db.Ext().Configure().ActivationDepth(0)
            Try
                Console.WriteLine("Object container activation depth = 0")
                Dim result As ObjectSet = db.Get(New SensorPanel(1))
                Console.WriteLine("Sensor1:")
                ListResult(result)
                Dim sensor1 As SensorPanel = CType(result(0), SensorPanel)
                TestActivated(sensor1)

                Console.WriteLine("Sensor1 activated:")
                db.Activate(sensor1, 4)
                TestActivated(sensor1)

                Console.WriteLine("Sensor5 activated:")
                result = db.Get(New SensorPanel(5))
                Dim sensor5 As SensorPanel = CType(result(0), SensorPanel)
                db.Activate(sensor5, 4)
                ListResult(result)
                TestActivated(sensor5)

                Console.WriteLine("Sensor1 deactivated:")
                db.Deactivate(sensor1, 5)
                TestActivated(sensor1)

                '			 	DANGER !!!.
                ' If you use Deactivate with a higher value than 1
                ' make sure that you know whereto members might branch
                ' Deactivating list1 also deactivated list5
                Console.WriteLine("Sensor 5 AFTER DEACTIVATE OF Sensor1.")
                TestActivated(sensor5)
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestActivateDeactivate

        Public Shared Sub TestActivated(ByVal sensor As SensorPanel)
            Dim nextSensor As SensorPanel = sensor
            Do
                nextSensor = nextSensor.NextSensor
                Console.WriteLine(nextSensor)
            Loop While Not nextSensor Is Nothing
        End Sub
        ' end TestActivated

        Public Shared Sub StoreCollection()
            File.Delete(YapFileName)
            Dim db As ObjectContainer = Db4o.OpenFile(YapFileName)
            Try
                Dim list As IList = db.Ext().Collections().NewLinkedList()
                Dim i As Integer
                For i = 0 To 10 - 1 Step i + 1
                    Dim sensor As SensorPanel = New SensorPanel(i)
                    list.Add(sensor)
                Next
                db.Set(list)
            Finally
                db.Close()
            End Try
        End Sub
        ' end StoreCollection

        Public Shared Sub TestCollectionDef()
            StoreCollection()
            Dim db As ObjectContainer = Db4o.OpenFile(YapFileName)
            db.Ext().Configure().ActivationDepth(5)
            Try
                Dim result As ObjectSet = db.Get(GetType(IList))
                ListResult(result)
                Dim list As Db4oList = CType(result(0), Db4oList)
                Dim i As Integer
                For i = 0 To list.Count - 1 Step i + 1
                    Console.WriteLine("List element: " + list(i).ToString())
                Next
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestCollectionDef


        Public Shared Sub TestCollectionActivation()
            StoreCollection()
            Dim db As ObjectContainer = Db4o.OpenFile(YapFileName)
            db.Ext().Configure().ActivationDepth(0)
            Try
                Dim result As ObjectSet = db.Get(GetType(IList))
                ListResult(result)

                Dim list As Db4oList = CType(result(0), Db4oList)
                Console.WriteLine("Setting list activation depth to 0 ")
                list.ActivationDepth(0)
                Dim i As Integer
                For i = 0 To list.Count - 1 Step i + 1
                    Console.WriteLine("List element: " + list(i).ToString())
                Next
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestCollectionActivation

        Public Shared Sub ListResult(ByVal result As ObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace

