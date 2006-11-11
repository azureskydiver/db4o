' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Blobs

    Public Class BlobExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args() As String)
            StoreCars()
            RetrieveCars()
        End Sub
        ' end Main

        Public Shared Sub StoreCars()
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4o.OpenFile(YapFileName)
            Try
                Dim car1 As Car = New Car("Ferrari")
                db.Set(car1)
                StoreImage(car1)
                Dim car2 As Car = New Car("BMW")
                db.Set(car2)
                StoreImage(car2)
            Finally
                db.Close()
            End Try
        End Sub
        ' end StoreCars

        Public Shared Sub StoreImage(ByVal car As Car)
            Dim img As CarImage = car.CarImage
            Try
                img.ReadFile()
            Catch ex As Exception
                Console.WriteLine(ex.Message)
            End Try
        End Sub
        ' end StoreImage

        Public Shared Sub RetrieveCars()
            Dim db As IObjectContainer = Db4o.OpenFile(YapFileName)
            Try
                Dim query As IQuery = db.Query()
                query.Constrain(GetType(Car))
                Dim result As IObjectSet = query.Execute()
                GetImages(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end RetrieveCars

        Private Shared Sub GetImages(ByVal result As IObjectSet)
            While result.HasNext()
                Dim car As Car = CType((result.Next()), Car)
                Console.WriteLine(car)
                Dim img As CarImage = car.CarImage
                Try
                    img.WriteFile()
                Catch ex As Exception
                    Console.WriteLine(ex.Message)
                End Try
            End While
        End Sub
        ' end GetImages
    End Class
End Namespace

