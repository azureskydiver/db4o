' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Reflect
Imports Db4objects.Db4o.Reflect.Net
Imports Db4objects.Db4o.Reflect.Generic
Imports Db4objects.Db4o.Query



Namespace Db4objects.Db4odoc.Reflections
    Public Class ReflectorExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Sub New()

        End Sub

        Public Shared Sub Main(ByVal args As String())
            SetCars()
            GetReflectorInfo()
            GetCars()
            GetCarInfo()
        End Sub
        ' end Main

        Public Shared Sub SetCars()
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim car1 As Car = New Car("BMW")
                db.Set(car1)
                Dim car2 As Car = New Car("Ferrari")
                db.Set(car2)

                Console.WriteLine("Saved:")
                Dim query As IQuery = db.Query()
                query.Constrain(GetType(Car))
                Dim results As IObjectSet = query.Execute()
                ListResult(results)
            Finally
                db.Close()
            End Try
        End Sub
        ' end SetCars

        Public Shared Sub GetCars()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim query As IQuery = db.Query()
                query.Constrain(GetType(Car))
                Dim result As IObjectSet = query.Execute()
                ListResult(result)
                Dim car As Car = CType(result(0), Car)
                Dim reflector As GenericReflector = New GenericReflector(Nothing, db.Ext().Reflector())
                Dim carClass As IReflectClass = reflector.ForObject(car)
                Console.WriteLine("Reflected class " + carClass.GetName())
            Finally
                db.Close()
            End Try
        End Sub
        ' end GetCars

        Public Shared Sub GetCarInfo()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim result As IObjectSet = db.Get(New Car("BMW"))
                If result.Size() < 1 Then
                    Return
                End If
                Dim car As Car = CType(result(0), Car)
                Dim reflector As GenericReflector = New GenericReflector(Nothing, db.Ext().Reflector())
                Dim carClass As IReflectClass = reflector.ForObject(car)
                Console.WriteLine("Reflected class " + carClass.GetName())
                ' public fields
                Console.WriteLine("FIELDS:")
                Dim fields() As IReflectField = carClass.GetDeclaredFields()
                Dim field As IReflectField
                For Each field In fields
                    Console.WriteLine(field.GetName())
                Next

                ' constructors
                Console.WriteLine("CONSTRUCTORS:")
                Dim cons() As IReflectConstructor = carClass.GetDeclaredConstructors()
                Dim constructor As IReflectConstructor
                For Each constructor In cons
                    Console.WriteLine(constructor)
                Next

                ' public methods
                Console.WriteLine("METHODS:")
                Dim params As IReflectClass() = {}
                Dim method As IReflectMethod = carClass.GetMethod("ToString", params)
                Console.WriteLine("ToString method " + CType(method, Object).ToString())
            Finally
                db.Close()
            End Try
        End Sub
        ' end GetCarInfo

        Public Shared Sub GetReflectorInfo()

            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim ref As IReflector
                ref = db.Ext().Reflector()
                Console.WriteLine("Reflector in use: " + CType(ref, Object).ToString())
                Console.WriteLine("Reflector delegate" + CType(db.Ext().Reflector().GetDelegate(), Object).ToString())
                Dim knownClasses As IReflectClass()
                knownClasses = db.Ext().Reflector().KnownClasses()
                Dim count As Integer
                count = knownClasses.Length
                Console.WriteLine("Known classes: " + count.ToString())
                Dim i As Integer
                Dim knownClass As IReflectClass
                For Each knownClass In knownClasses
                    Console.WriteLine(knownClass.GetName())
                Next

            Finally
                db.Close()
            End Try
        End Sub
        ' end GetReflectorInfo

        Public Shared Sub TestReflector()
            Dim logger As LoggingReflector = New LoggingReflector()
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ReflectWith(logger)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim car As Car = New Car("BMW")
                Dim rc As IReflectClass
                rc = db.Ext().Reflector().ForObject(car)
                Console.WriteLine("Reflected class: " + rc.GetName())
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestReflector

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult

    End Class
End Namespace

