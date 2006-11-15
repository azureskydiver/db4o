' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext

Namespace Db4objects.Db4odoc.MetaInfo

    Public Class MetaInfoExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args() As String)
            SetObjects()
            GetMetaObjects()
            GetMetaObjectsInfo()
        End Sub
        ' end Main

        Public Shared Sub SetObjects()
            File.Delete(YapFileName)
            Dim oc As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim car As Car = New Car("BMW", New Pilot("Rubens Barrichello"))
                oc.Set(car)
                car = New Car("Ferrari", New Pilot("Michael Schumacher"))
                oc.Set(car)
            Finally
                oc.Close()
            End Try
        End Sub
        ' end SetObjects

        Public Shared Sub GetMetaObjects()
            Dim oc As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                System.Console.WriteLine("Retrieve meta information for class: ")
                Dim sc As IStoredClass = oc.Ext().StoredClass(GetType(Car))
                System.Console.WriteLine("Stored class:  " + sc.GetName())

                System.Console.WriteLine("Retrieve meta information for all classes in database: ")
                Dim sclasses() As IStoredClass = oc.Ext().StoredClasses()
                Dim i As Integer
                For i = 0 To sclasses.Length - 1 Step i + 1
                    System.Console.WriteLine(sclasses(i).GetName())
                Next
            Finally
                oc.Close()
            End Try
        End Sub
        ' end GetMetaObjects

        Public Shared Sub GetMetaObjectsInfo()
            Dim oc As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                System.Console.WriteLine("Retrieve meta information for field: ")
                Dim sc As IStoredClass = oc.Ext().StoredClass(GetType(Car))
                Dim sf As IStoredField = sc.StoredField("_pilot", GetType(Pilot))
                System.Console.WriteLine("Field info:  " + sf.GetName() + "/" + sf.GetStoredType().GetName() + "/IsArray=" + sf.IsArray().ToString())

                System.Console.WriteLine("Retrieve all fields: ")
                Dim sfields() As IStoredField = sc.GetStoredFields()
                Dim i As Integer
                For i = 0 To sfields.Length - 1 Step i + 1
                    System.Console.WriteLine("Stored field:  " + sfields(i).GetName() + "/" + sfields(i).GetStoredType().GetName())
                Next
            Finally
                oc.Close()
            End Try
        End Sub
        ' end GetMetaObjectsInfo
    End Class
End Namespace
