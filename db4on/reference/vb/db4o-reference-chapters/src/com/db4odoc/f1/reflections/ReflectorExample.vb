' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System
Imports com.db4o.reflect
Imports com.db4o.reflect.net
Imports com.db4o.reflect.generic
Imports com.db4o.query
Imports com.db4o
Imports System.IO


Namespace com.db4odoc.f1.reflections
    Public Class ReflectorExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"
        
        Public Sub New()

        End Sub

        Public Sub Main()
            setCars()
            getReflectorInfo()
            getCars()
            getCarInfo()
        End Sub
        ' end Main

        Public Sub SetCars()
            File.Delete(YapFileName)
            Dim db As ObjectContainer = Db4o.OpenFile(YapFileName)
            Try
                Dim car1 As Car = New Car("BMW")
                db.Set(car1)
                Dim car2 As Car = New Car("Ferrari")
                db.Set(car2)

                Console.WriteLine("Saved:")
                Dim query As Query = db.Query()
                query.Constrain(GetType(Car))
                Dim results As ObjectSet = query.Execute()
                ListResult(results)
            Finally
                db.Close()
            End Try
        End Sub
        ' end SetCars

        Public Sub GetCars()
            Dim db As ObjectContainer = Db4o.OpenFile(YapFileName)
            Try
                Dim query As Query = db.Query()
                query.Constrain(GetType(Car))
                Dim result As ObjectSet = query.Execute()
                ListResult(result)
                Dim car As Car = CType(result(0), Car)
                Dim reflector As GenericReflector = New GenericReflector(Nothing, db.Ext().Reflector())
                Dim carClass As ReflectClass = reflector.ForObject(car)
                Console.WriteLine("Reflected class " + carClass.GetName())
            Finally
                db.Close()
            End Try
        End Sub
        ' end GetCars

        Public Sub GetCarInfo()
            Dim db As ObjectContainer = Db4o.OpenFile(YapFileName)
            Try
                Dim result As ObjectSet = db.Get(New Car("BMW"))
                If result.Size() < 1 Then
                    Return
                End If
                Dim car As Car = CType(result(0), Car)
                Dim reflector As GenericReflector = New GenericReflector(Nothing, db.Ext().Reflector())
                Dim carClass As ReflectClass = reflector.ForObject(car)
                Console.WriteLine("Reflected class " + carClass.GetName())
                ' public fields
                Console.WriteLine("FIELDS:")
                Dim fields() As ReflectField = carClass.GetDeclaredFields()
                Dim i As Integer
                For i = 0 To fields.Length - 1 Step i + 1
                    Console.WriteLine(fields(i).GetName())
                Next

                ' constructors
                Console.WriteLine("CONSTRUCTORS:")
                Dim cons() As ReflectConstructor = carClass.GetDeclaredConstructors()
                For i = 0 To cons.Length - 1 Step i + 1
                    Console.WriteLine(cons(i))
                Next

                ' public methods
                Console.WriteLine("METHODS:")
                Dim method As ReflectMethod = carClass.GetMethod("ToString", Nothing)
                Console.WriteLine("ToString method " + CType(method, Object).ToString())
            Finally
                db.Close()
            End Try
        End Sub
        ' end GetCarInfo

        Public Sub GetReflectorInfo()

            Dim db As ObjectContainer = Db4o.OpenFile(YapFileName)
            Try
                Dim ref As Reflector
                ref = db.Ext().Reflector()
                Console.WriteLine("Reflector in use: " + CType(ref, Object).ToString())
                Console.WriteLine("Reflector delegate" + CType(db.Ext().Reflector().GetDelegate(), Object).ToString())
                Dim knownClasses As ReflectClass()
                knownClasses = db.Ext().Reflector().KnownClasses()
                Dim count As Integer
                count = knownClasses.Length
                Console.WriteLine("Known classes: " + count.ToString())
                Dim i As Integer
                For i = 0 To knownClasses.Length - 1 Step i + 1
                    Console.WriteLine(knownClasses(i).GetName())
                Next

            Finally
                db.Close()
            End Try
        End Sub
        ' end GetReflectorInfo

        Public Sub TestReflector()
            Dim logger As LoggingReflector = New LoggingReflector()
            Db4o.Configure().ReflectWith(logger)
            Dim db As ObjectContainer = Db4o.OpenFile(YapFileName)
            Try
                Dim car As Car = New Car("BMW")
                Dim rc As ReflectClass
                rc = db.Ext().Reflector().ForObject(car)
                Console.WriteLine("Reflected class: " + rc.GetName())
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestReflector

        Public Shared Sub ListResult(ByVal result As ObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult

    End Class
End Namespace

