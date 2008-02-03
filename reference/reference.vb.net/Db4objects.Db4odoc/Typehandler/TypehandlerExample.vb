' Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com 

Imports System.Text
Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Defragment
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.Query
Imports Db4objects.Db4o.Reflect
Imports Db4objects.Db4o.Reflect.Net
Imports Db4objects.Db4o.Reflect.Generic
Imports Db4objects.Db4o.Typehandlers

Namespace Db4objects.Db4odoc.Typehandler

    Public Class TypehandlerExample

        Private Shared ReadOnly Db4oFileName As String = "reference.db4o"
        Private Shared _container As IObjectContainer = Nothing


        Public Shared Sub Main(ByVal args As String())
            TestReadWriteDelete()
            TestDefrag()
            TestCompare()
        End Sub
        ' end Main

        Private Class TypeHandlerPredicate
            Implements ITypeHandlerPredicate

            Public Function Match(ByVal classReflector As IReflectClass, ByVal version As Integer) As Boolean Implements ITypeHandlerPredicate.Match
                Dim reflector As IReflector = classReflector.Reflector()
                Dim claxx As IReflectClass = reflector.ForClass(GetType(StringBuilder))
                Dim res As Boolean = claxx Is classReflector
                Return res

            End Function

        End Class
        ' end TypeHandlerPredicate

        Private Shared Function Configure() As IConfiguration
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            ' add a custom typehandler support

            configuration.RegisterTypeHandler(New TypeHandlerPredicate(), New StringBuilderHandler())
            Return configuration
        End Function
        ' end Configure


        Private Shared Sub TestReadWriteDelete()
            StoreCar()
            ' Does it still work after close? 
            RetrieveCar()
            ' Does deletion work?
            DeleteCar()
            RetrieveCar()
        End Sub
        ' end TestReadWriteDelete

        Private Shared Sub RetrieveCar()
            Dim container As IObjectContainer = Database(Configure())
            If container IsNot Nothing Then
                Try
                    Dim result As IObjectSet = container.QueryByExample(New Car(Nothing))
                    Dim car As Car = Nothing
                    If result.HasNext() Then
                        car = DirectCast(result.[Next](), Car)
                    End If
                    If car Is Nothing Then
                        System.Console.WriteLine("Retrieved: Nothing")
                    Else
                        System.Console.WriteLine("Retrieved: " + car.ToString())
                    End If

                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end RetrieveCar

        Private Shared Sub DeleteCar()
            Dim container As IObjectContainer = Database(Configure())
            If container IsNot Nothing Then
                Try
                    Dim result As IObjectSet = container.QueryByExample(New Car(Nothing))
                    Dim car As Car = Nothing
                    If result.HasNext() Then
                        car = DirectCast(result.[Next](), Car)
                    End If
                    container.Delete(car)
                    System.Console.WriteLine("Deleted: " + car.ToString())
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end DeleteCar

        Private Shared Sub StoreCar()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Database(Configure())
            If container IsNot Nothing Then
                Try
                    Dim car As New Car("BMW")
                    container.Store(car)
                    Dim result As IObjectSet = container.QueryByExample(New Car(Nothing))
                    car = DirectCast(container.QueryByExample(New Car(Nothing)).[Next](), Car)

                    System.Console.WriteLine("Stored: " + car.ToString())
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end StoreCar

        Private Shared Sub TestCompare()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Database(Configure())
            If container IsNot Nothing Then
                Try
                    Dim car As New Car("BMW")
                    container.Store(car)
                    car = New Car("Ferrari")
                    container.Store(car)
                    car = New Car("Mercedes")
                    container.Store(car)
                    Dim query As IQuery = container.Query()
                    query.Constrain(GetType(Car))
                    query.Descend("model").OrderAscending()
                    Dim result As IObjectSet = query.Execute()

                    ListResult(result)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end TestCompare

        Private Shared Sub TestDefrag()
            File.Delete(Db4oFileName + ".backup")
            StoreCar()
            Defragment.Defrag(Db4oFileName)
            RetrieveCar()
        End Sub
        ' end TestDefrag

        Private Shared Function Database(ByVal configuration As IConfiguration) As IObjectContainer
            If _container Is Nothing Then
                Try
                    _container = Db4oFactory.OpenFile(configuration, Db4oFileName)
                Catch ex As DatabaseFileLockedException
                    System.Console.WriteLine(ex.Message)
                End Try
            End If
            Return _container
        End Function
        ' end Database

        Private Shared Sub CloseDatabase()
            If _container IsNot Nothing Then
                _container.Close()
                _container = Nothing
            End If
        End Sub
        ' end CloseDatabase


        Private Shared Sub ListResult(ByVal result As IObjectSet)
            System.Console.WriteLine(result.Size())
            While result.HasNext()
                System.Console.WriteLine(result.[Next]())
            End While
        End Sub
        ' end ListResult

    End Class
End Namespace
