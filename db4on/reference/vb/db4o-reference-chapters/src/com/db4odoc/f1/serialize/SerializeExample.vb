' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System
Imports System.IO
Imports System.Xml.Serialization
Imports com.db4o


Namespace com.db4odoc.f1.serialize
    Public Class SerializeExample
        Inherits Util
        Public Shared ReadOnly XmlFileName As String = "formula1.xml"


        Public Shared Sub main(ByVal args() As String)
            SetObjects()
            ExportToXml()
            ImportFromXml()
        End Sub

        Public Shared Sub SetObjects()
            File.Delete(Util.YapFileName)
            Dim db As ObjectContainer = Db4o.OpenFile(Util.YapFileName)
            Try
                Dim car As Car = New Car("BMW", New Pilot("Rubens Barrichello"))
                db.Set(car)
                car = New Car("Ferrari", New Pilot("Michael Schumacher"))
                db.Set(car)
            Finally
                db.Close()
            End Try
        End Sub

        Public Shared Sub ExportToXml()
            Dim carSerializer As XmlSerializer = New XmlSerializer(GetType(Car()))
            Dim xmlWriter As StreamWriter = New StreamWriter(XmlFileName)
            Dim db As ObjectContainer = Db4o.OpenFile(Util.YapFileName)
            Try
                Dim result As ObjectSet = db.Get(GetType(Car))
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

        Public Shared Sub ImportFromXml()
            File.Delete(Util.YapFileName)
            Dim carSerializer As XmlSerializer = New XmlSerializer(GetType(Car()))
            Dim xmlFileStream As FileStream = New FileStream(XmlFileName, FileMode.Open)
            Dim cars() As Car = CType(carSerializer.Deserialize(xmlFileStream), Car())
            Dim db As ObjectContainer
            Dim i As Integer
            For i = 0 To cars.Length - 1 Step i + 1
                db = Db4o.OpenFile(Util.YapFileName)
                Try
                    Dim car As Car = CType(cars(i), Car)
                    db.Set(car)
                Finally
                    db.Close()
                End Try
            Next
            db = Db4o.OpenFile(Util.YapFileName)
            Try
                Dim result As ObjectSet = db.Get(GetType(Pilot))
                ListResult(result)
                result = db.Get(GetType(Car))
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub

    End Class
End Namespace

