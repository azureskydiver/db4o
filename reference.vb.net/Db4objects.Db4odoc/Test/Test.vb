Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config

Namespace Db4objects.Db4odoc.Test
    Public Class Test

        Public Shared Sub Main(ByVal args As String())
            Dim result As IObjectSet
            Dim db As IObjectContainer

            '       File.Delete("C:\car.yap")
            Dim conf As IConfiguration = Db4oFactory.NewConfiguration()

            'conf.Encrypt(True)
            'conf.Password("test")

            db = Db4oFactory.OpenFile(conf, "C:\car.yap")

            Dim pilot1 As New Pilot("pilotA", 100)
            Dim pilot2 As New Pilot("pilotB", 500)
            Dim pilot3 As New Pilot("pilotC", 1000)
            Dim car1 As New Car("BMW")
            Dim car2 As New Car("Benz")

            'db.Set(pilot1)
            'db.Set(pilot2)
            'db.Set(pilot3)
            'db.Set(car1)
            'db.Set(car2)

            'car1.Pilot = pilot1
            'db.Set(car1)

            'car2.Pilot = pilot2
            'db.Set(car2)


            result = db.Get(New Pilot(Nothing, 0))
            ListResult(result)
        End Sub

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult

    End Class
End Namespace