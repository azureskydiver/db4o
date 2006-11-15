' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System
Imports System.IO
Imports System.Xml.Serialization
Imports Db4objects.Db4o


Namespace Db4objects.Db4odoc.Serializing
    Public Class SerializeExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"
        Public Shared ReadOnly XmlFileName As String = "formula1.xml"


        Public Shared Sub Main(ByVal args() As String)
            SetObjects()
            ExportToXml()
            ImportFromXml()
        End Sub
        ' end Main

        Public Shared Sub SetObjects()
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim car As Car = New Car("BMW", New Pilot("Rubens Barrichello"))
                db.Set(car)
                car = New Car("Ferrari", New Pilot("Michael Schumacher"))
                db.Set(car)
            Finally
                db.Close()
            End Try
        End Sub
        ' end SetObjects

        Public Shared Sub ExportToXml()
            Dim carSerializer As XmlSerializer = New XmlSerializer(GetType(Car()))
            Dim xmlWriter As StreamWriter = New StreamWriter(XmlFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim result As IObjectSet = db.Get(GetType(Car))
                Dim cars() As Car = New Car(result.Size()) {}
                Dim i As Integer
                For i = 0 To result.Size() - 1 Step i + 1
                    Dim car As Car = CType(result(i), Car)
                    cars.SetValue(car, i)
                Next
                carSerializer.Serialize(xmlWriter, cars)
                xmlWriter.Close()
            Finally
                db.Close()
            End Try
        End Sub
        ' end ExportToXml

        Public Shared Sub ImportFromXml()
            File.Delete(YapFileName)
            Dim carSerializer As XmlSerializer = New XmlSerializer(GetType(Car()))
            Dim xmlFileStream As FileStream = New FileStream(XmlFileName, FileMode.Open)
            Dim cars() As Car = CType(carSerializer.Deserialize(xmlFileStream), Car())
            Dim db As IObjectContainer
            Dim i As Integer
            For i = 0 To cars.Length - 1 Step i + 1
                db = Db4oFactory.OpenFile(YapFileName)
                Try
                    Dim car As Car = CType(cars(i), Car)
                    db.Set(car)
                Finally
                    db.Close()
                End Try
            Next
            db = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim result As IObjectSet = db.Get(GetType(Pilot))
                ListResult(result)
                result = db.Get(GetType(Car))
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end ImportFromXml

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace

