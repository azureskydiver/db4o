Imports System.Collections
Imports System.Collections.Generic
Imports System.IO
Imports Db4objects.Db4o

Namespace Db4oDoc.Code.Query.NativeQueries
    Public Class NativeQueriesSorting
        Private Const DatabaseFile As String = "database.db4o"

        Public Shared Sub Main(ByVal args As String())
            CleanUp()
            Using container As IObjectContainer = Db4oEmbedded.OpenFile(DatabaseFile)
                StoreData(container)

                NativeQuerySorting(container)
            End Using
        End Sub


        Private Shared Sub NativeQuerySorting(ByVal container As IObjectContainer)
            ' #example: Native query with sorting
            Dim pilots As IList(Of Pilot) = container.Query( _
                    Function(p As Pilot) p.Age > 18, _
                    Function(p1 As Pilot, p2 As Pilot) p1.Name.CompareTo(p2.Name))
            ' #end example

            ListResult(pilots)
        End Sub

        Private Shared Sub CleanUp()
            File.Delete(DatabaseFile)
        End Sub


        Private Shared Sub ListResult(ByVal result As IEnumerable)
            For Each obj As Object In result
                Console.WriteLine(obj)
            Next
        End Sub

        Private Shared Sub StoreData(ByVal container As IObjectContainer)
            Dim john As New Pilot("John", 42)
            Dim joanna As New Pilot("Joanna", 45)
            Dim jenny As New Pilot("Jenny", 21)
            Dim rick As New Pilot("Rick", 33)

            container.Store(New Car(john, "Ferrari"))
            container.Store(New Car(joanna, "Mercedes"))
            container.Store(New Car(jenny, "Volvo"))
            container.Store(New Car(rick, "Fiat"))
        End Sub
    End Class

    Friend Class Pilot
        Private m_name As String
        Private m_age As Integer

        Public Sub New(ByVal name As String, ByVal age As Integer)
            Me.m_name = name
            Me.m_age = age
        End Sub

        Public Property Name() As String
            Get
                Return m_name
            End Get
            Set(ByVal value As String)
                m_name = value
            End Set
        End Property

        Public Property Age() As Integer
            Get
                Return m_age
            End Get
            Set(ByVal value As Integer)
                m_age = value
            End Set
        End Property

        Public Overrides Function ToString() As String
            Return String.Format("Name: {0}, Age: {1}", m_name, m_age)
        End Function
    End Class

    Friend Class Car
        Private m_pilot As Pilot
        Private m_name As String


        Public Sub New(ByVal pilot As Pilot, ByVal name As String)
            Me.m_pilot = pilot
            Me.m_name = name
        End Sub

        Public Property Pilot() As Pilot
            Get
                Return m_pilot
            End Get
            Set(ByVal value As Pilot)
                m_pilot = value
            End Set
        End Property

        Public Property Name() As String
            Get
                Return m_name
            End Get
            Set(ByVal value As String)
                m_name = value
            End Set
        End Property

        Public Overrides Function ToString() As String
            Return String.Format("Pilot: {0}, Name: {1}", m_pilot, m_name)
        End Function
    End Class
End Namespace