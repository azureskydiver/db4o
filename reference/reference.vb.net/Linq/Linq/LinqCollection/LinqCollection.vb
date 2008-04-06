' Copyright (C) 2007 db4objects Inc. http://www.db4o.com 

Imports System
Imports System.Text
'Imports System.Linq
'Imports System.Linq.Enumerable
Imports System.IO
Imports System.Collections.Generic
Imports System.Collections

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Linq
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.Query
Imports Db4objects.Db4o.Diagnostic
Imports Db4objects.Db4o.Config

Namespace Db4objects.Db4odoc.Linq
    Class LinqCollection

        Private Shared ReadOnly Db4oFileName As String = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "reference.db4o")


        Private Const ObjectCount As Integer = 20

        Private Shared _container As IObjectContainer = Nothing

        Public Shared Sub Main(ByVal args As String())
            StoreObjects()
            SelectJoin()
            SelectFromSelection()
            SelectEverythingByName()
            SelectClone()
            SelectPilotByNameAndPoints()
            SelectByNameAndPoints()
            SelectOrdered()
            StoreForSorting()
            SelectGroupByName()
            SelectComplexOrdered()
            StorePilots()
            SelectWithModifiedResult()
            SelectAggregate()
            SelectAverage()
            SelectAny()

        End Sub

        ' end Main

        Private Shared Function Database(ByVal config As IConfiguration) As IObjectContainer
            If _container Is Nothing Then
                Try
                    _container = Db4oFactory.OpenFile(config, Db4oFileName)
                Catch ex As DatabaseFileLockedException
                    System.Console.WriteLine(ex.Message)
                End Try
            End If
            Return _container
        End Function

        ' end Database

        Private Shared Function Database() As IObjectContainer
            If _container Is Nothing Then
                Try
                    _container = Db4oFactory.OpenFile(Db4oFileName)
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

        Private Shared Sub StoreObjects()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim pilot As Pilot
                    Dim car As Car
                    For i As Integer = 0 To ObjectCount - 1
                        pilot = New Pilot("Test Pilot #" + i.ToString(), i + 10)
                        car = New Car("Test model #" + i.ToString(), pilot)
                        container.Store(car)
                    Next
                    container.Commit()
                Catch ex As Db4oException
                    System.Console.WriteLine("Db4o Exception: " + ex.Message)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end StoreObjects

        Private Shared Sub StorePilots()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim p As Pilot
                    For i As Integer = 0 To ObjectCount - 1
                        p = New Pilot("Test Pilot #" + i.ToString(), i)
                        container.Store(p)
                    Next
                    For i As Integer = 0 To ObjectCount - 1
                        p = New Pilot("Professional Pilot #" + (i + 10).ToString(), i + 10)
                        container.Store(p)
                    Next
                    container.Commit()
                Catch ex As Db4oException
                    System.Console.WriteLine("Db4o Exception: " + ex.Message)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end StorePilots

        Private Shared Sub StoreForSorting()

            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim p As Pilot
                    For i As Integer = 0 To ObjectCount - 1
                        p = New Pilot("Test Pilot #" + i.ToString(), i)
                        container.Store(p)
                    Next
                    For i As Integer = 0 To ObjectCount - 1
                        p = New Pilot("Test Pilot #" + i.ToString(), (i + 10))
                        container.Store(p)
                    Next
                    container.Commit()
                Catch ex As Db4oException
                    System.Console.WriteLine("Db4o Exception: " + ex.Message)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end StoreForSorting


        Private Shared Sub SelectEverythingByName()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim result = From o As Object In container _
                                 Select res = o _
                                 Where res.ToString().StartsWith("Test")
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end SelectEverythingByName


        Private Shared Sub SelectOrdered()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim result = From p As Pilot In container _
                    Where (p.Points < 15) Order By p.Name Descending Select p
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end SelectOrdered


        Private Shared Sub SelectComplexOrdered()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim result = From p As Pilot In container _
                                 Order By p.Name Descending Order By p.Points Ascending _
                                 Select p
                    ListResult(result)
                catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end SelectComplexOrdered

        Private Shared Sub SelectGroupByName()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim result = From p As Pilot In container _
                                  Order By p.Points Descending _
                                  Select p Group By p.Points, p.Name Into Group Select Name, Points
                    For Each value In result
                        Console.WriteLine("  {0}", value)
                    Next
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end SelectGroupByName

        Private Shared Sub SelectPilotByNameAndPoints()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim result As IEnumerable(Of Pilot) = From p As Pilot In container _
                                                Where p.Name.StartsWith("Test") And p.Points > 12 _
                                                Select p

                    ListResult(result)
                catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end SelectPilotByNameAndPoints

        Private Shared Sub SelectByNameAndPoints()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim result = From p As Pilot In container _
                                 Where p.Name.StartsWith("Test") And p.Points > 12 _
                                 Select p
                    ListResult(result)
                catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectByNameAndPoints

        Private Shared Sub SelectClone()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim result = From p As Pilot In container _
                                 Where p.Name.StartsWith("Test") And p.Points > 12 _
                                 Select New Pilot(p.Name, p.Points)
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end SelectClone

        Private Shared Sub SelectFromSelection()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim allObjects = From o As Object In container Select o
                    Dim listOfObjects = allObjects.ToList()
                    Dim pilots = From p As Object In allObjects _
                    Where (p.GetType().FullName.Equals("Linq.Db4objects.Db4odoc.Linq.Pilot")) _
                    Select pilot1 = CType(p, Pilot) Where pilot1.Points > 25
                    ListResult(pilots)
                    Dim cars = From car As Object In allObjects _
                               Where car.GetType().FullName.Equals("Linq.Db4objects.Db4odoc.Linq.Car") _
                               Select car1 = CType(car, Car) _
                               Where pilots.Contains(CType(car1, Car).Pilot)
                    ListResult(cars)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end SelectFromSelection

        Private Shared Sub SelectWithModifiedResult()
            Dim container As IObjectContainer = Database()
            Dim maxPoints As Integer = 100
            If container IsNot Nothing Then
                Try
                    ' Select percentage
                    Dim result = From p As Pilot In container _
                    Where (p.Name.StartsWith("Test")) _
                                 Select String.Format("{0}: {1}%", p.Name, (p.Points * 100 / maxPoints))
                    ListResult(result)
                catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectWithModifiedResult

        Private Shared Sub SelectAggregate()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    ' Select pilot names separated by semicolon
                    Dim result = From p As Pilot In container _
                    Where p.Name.StartsWith("Test") Select p.Name
                    Dim sumString As String = result.Aggregate("", Function(acc, value) acc + ";" + value)

                    System.Console.WriteLine(sumString)

                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectAggregate

        Private Shared Sub SelectAverage()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    'Find the average of pilot points
                    Dim result = Aggregate p As Pilot In container _
                    Where p.Name.StartsWith("Test") Into Average(p.Points)
                    System.Console.WriteLine(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end SelectAverage

        Private Shared Sub SelectAny()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    'Checks if query returns any results
                    Dim result = Aggregate p As Pilot In container _
                    Where p.Name.EndsWith("Test") Into Any()
                    System.Console.WriteLine("The query returns any results: " + result.ToString())
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end SelectAny

        Private Shared Sub SelectJoin()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim result = From p As Pilot In container _
                                  From c As Car In container _
                    Where (p.Points > 25) _
                                  And c.Pilot.Equals(p) _
                                  Select c
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectJoin

        Private Shared Sub ListResult(Of T)(ByVal result As IEnumerable(Of T))
            System.Console.WriteLine(result.Count())
            For Each obj As Object In result
                If obj.GetType() Is GetType(T) Then
                    System.Console.WriteLine(obj)
                End If
            Next
        End Sub

        ' end ListResult


    End Class
End Namespace
