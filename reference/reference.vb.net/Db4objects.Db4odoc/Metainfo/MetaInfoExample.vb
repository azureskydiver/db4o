' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext

Namespace Db4objects.Db4odoc.MetaInfo

    Public Class MetaInfoExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args() As String)
            SetObjects()
            GetMetaObjects()
            GetMetaObjectsInfo()
        End Sub
        ' end Main

        Private Shared Sub SetObjects()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim car As Car = New Car("BMW", New Pilot("Rubens Barrichello"))
                container.Set(car)
                car = New Car("Ferrari", New Pilot("Michael Schumacher"))
                container.Set(car)
            Finally
                container.Close()
            End Try
        End Sub
        ' end SetObjects

        Private Shared Sub GetMetaObjects()
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                System.Console.WriteLine("Retrieve meta information for class: ")
                Dim sc As IStoredClass = container.Ext().StoredClass(GetType(Car))
                System.Console.WriteLine("Stored class:  " + sc.GetName())

                System.Console.WriteLine("Retrieve meta information for all classes in database: ")
                Dim sclasses() As IStoredClass = container.Ext().StoredClasses()
                Dim i As Integer
                For i = 0 To sclasses.Length - 1 Step i + 1
                    System.Console.WriteLine(sclasses(i).GetName())
                Next
            Finally
                container.Close()
            End Try
        End Sub
        ' end GetMetaObjects

        Private Shared Sub GetMetaObjectsInfo()
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                System.Console.WriteLine("Retrieve meta information for field: ")
                Dim sc As IStoredClass = container.Ext().StoredClass(GetType(Car))
                Dim sf As IStoredField = sc.StoredField("_pilot", GetType(Pilot))
                System.Console.WriteLine("Field info:  " + sf.GetName() + "/" + sf.GetStoredType().GetName() + "/IsArray=" + sf.IsArray().ToString())

                System.Console.WriteLine("Retrieve all fields: ")
                Dim sfields() As IStoredField = sc.GetStoredFields()
                Dim i As Integer
                For i = 0 To sfields.Length - 1 Step i + 1
                    System.Console.WriteLine("Stored field:  " + sfields(i).GetName() + "/" + sfields(i).GetStoredType().GetName())
                Next
            Finally
                container.Close()
            End Try
        End Sub
        ' end GetMetaObjectsInfo

    End Class
End Namespace
