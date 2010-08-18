Imports Db4objects.Db4o

Namespace Db4oDoc.Code.Basics
    Public Class Db4oBasics
        Public Shared Sub Main(ByVal args As String())
            OpenAndCloseTheContainer()

            Using container As IObjectContainer = Db4oEmbedded.OpenFile("databaseFile.db4o")
                StoreObject(container)
                DeleteObject(container)
            End Using
        End Sub

        Private Shared Sub StoreObject(ByVal container As IObjectContainer)
            ' #example: Store a object
            Dim pilot As New Pilot("Joe")
            container.Store(pilot)
            ' #end example
        End Sub

        Private Shared Sub DeleteObject(ByVal container As IObjectContainer)
            Dim pilot As Pilot = container.Query(Of Pilot)()(0)
            ' #example: Delete a object
            container.Delete(pilot)
            ' #end example
        End Sub

        Private Shared Sub OpenAndCloseTheContainer()
            ' #example: Open the object container to use the database
            Using container As IObjectContainer = Db4oEmbedded.OpenFile("databaseFile.db4o")               
                ' use the object container
            End Using
            ' #end example
        End Sub
    End Class

    Friend Class Pilot
        Private m_name As String

        Public Sub New(ByVal name As String)
            Me.m_name = name
        End Sub

        Public Property Name() As String
            Get
                Return m_name
            End Get
            Set(ByVal value As String)
                m_name = value
            End Set
        End Property
    End Class
End Namespace