' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config

Namespace Db4objects.Db4odoc.Utility
    Public Class UtilityExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args() As String)
            TestDescend()
            CheckActive()
            CheckStored()
        End Sub
        ' end Main

        Public Shared Sub StoreSensorPanel()
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
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

        Public Shared Sub TestDescend()
            StoreSensorPanel()
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ActivationDepth(1)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                System.Console.WriteLine("Object container activation depth = 1")
                Dim result As IObjectSet = db.Get(New SensorPanel(1))
                Dim spParent As SensorPanel = CType(result(0), SensorPanel)
                Dim fields() As String = {"_next", "_next", "_next", "_next", "_next"}
                Dim spDescend As SensorPanel = CType(db.Ext().Descend(CType(spParent, Object), fields), Object)
                db.Ext().Activate(spDescend, 5)
                System.Console.WriteLine(spDescend)
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestDescend

        Public Shared Sub CheckActive()
            StoreSensorPanel()
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ActivationDepth(2)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                System.Console.WriteLine("Object container activation depth = 2")
                Dim result As IObjectSet = db.Get(New SensorPanel(1))
                Dim sensor As SensorPanel = CType(result(0), SensorPanel)
                Dim NextSensor As SensorPanel = sensor.NextSensor
                While Not NextSensor Is Nothing
                    System.Console.WriteLine("Object " + NextSensor.ToString() + " is active: " + db.Ext().IsActive(NextSensor).ToString())
                    NextSensor = NextSensor.NextSensor
                End While
            Finally
                db.Close()
            End Try
        End Sub
        ' end CheckActive

        Public Shared Sub CheckStored()
            ' create a linked list with length 10
            Dim list As SensorPanel = New SensorPanel().CreateList(10)
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                ' store all elements with one statement, since all elements are new		
                db.Set(list)
                Dim sensor As Object = CType(list.Sensor, Object)
                Dim sp5 As SensorPanel = list.NextSensor.NextSensor.NextSensor.NextSensor
                System.Console.WriteLine("Root element " + list.ToString() + " isStored: " + db.Ext().IsStored(list).ToString())
                System.Console.WriteLine("Simple type  " + sensor.ToString() + " isStored: " + db.Ext().IsStored(sensor).ToString())
                System.Console.WriteLine("Descend element  " + sp5.ToString() + " isStored: " + db.Ext().IsStored(sp5).ToString())
                db.Delete(list)
                System.Console.WriteLine("Root element " + list.ToString() + " isStored: " + db.Ext().IsStored(list).ToString())
            Finally
                db.Close()
            End Try
        End Sub
        ' end CheckStored
    End Class
End Namespace
