Imports System.Collections.Generic
Imports System.IO
Imports System.Linq
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Linq

Namespace Db4oDoc.Code.Pitfalls.UpdateDepth
    Public Class UpdateDepthPitfall
        Public Const DatabaseFile As [String] = "database.db4o"

        Public Shared Sub Main(ByVal args As String())
            CleanUp()
            PrepareDeepObjGraph()


            ToLowUpdateDeph()
            UpdateDepth()
        End Sub

        Private Shared Sub ToLowUpdateDeph()
            ' #example: Update doesn't work
            Using container As IObjectContainer = Db4oEmbedded.OpenFile(DatabaseFile)
                Dim jodie As Person = QueryForJodie(container)
                jodie.Add(New Person("Jamie"))
                ' Remember that a collection is also a regular object
                ' so with the default-update depth of one, only the changes
                ' on the person-object are stored, but not the changes on
                ' the friend-list.
                container.Store(jodie)
            End Using
            Using container As IObjectContainer = Db4oEmbedded.OpenFile(DatabaseFile)
                Dim jodie As Person = QueryForJodie(container)
                For Each person As Person In jodie.Friends
                    ' the added friend is gone, because the update-depth is to low
                    Console.WriteLine("Friend=" & person.Name)
                Next
            End Using
            ' #end example
        End Sub

        Private Shared Sub UpdateDepth()
            ' #example: A higher update depth fixes the issue
            Dim config As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            config.Common.UpdateDepth = 2
            Using container As IObjectContainer = Db4oEmbedded.OpenFile(config, DatabaseFile)
                Dim jodie As Person = QueryForJodie(container)
                jodie.Add(New Person("Jamie"))
                ' Remember that a collection is also a regular object
                ' so with the default-update depth of one, only the changes
                ' on the person-object are stored, but not the changes on
                ' the friend-list.
                container.Store(jodie)
            End Using
            config = Db4oEmbedded.NewConfiguration()
            config.Common.UpdateDepth = 2
            Using container As IObjectContainer = Db4oEmbedded.OpenFile(config, DatabaseFile)
                Dim jodie As Person = QueryForJodie(container)
                For Each person As Person In jodie.Friends
                    ' the added friend is gone, because the update-depth is to low
                    Console.WriteLine("Friend=" & person.Name)
                Next
            End Using
        End Sub

        Private Shared Sub CleanUp()
            File.Delete(DatabaseFile)
        End Sub

        Private Shared Function QueryForJodie(ByVal container As IObjectContainer) As Person
            Return (From p As Person In container Where p.Name = "Jodie").First()
        End Function

        Private Shared Sub PrepareDeepObjGraph()
            Using container As IObjectContainer = Db4oEmbedded.OpenFile(DatabaseFile)
                Dim jodie As New Person("Jodie")

                jodie.Add(New Person("Joanna"))
                jodie.Add(New Person("Julia"))
                container.Store(jodie)
            End Using
        End Sub
    End Class

    Friend Class Person
        Private m_friends As IList(Of Person) = New List(Of Person)()

        Private m_name As String

        Friend Sub New(ByVal name As String)
            Me.m_name = name
        End Sub


        Public ReadOnly Property Friends() As IList(Of Person)
            Get
                Return m_friends
            End Get
        End Property

        Public ReadOnly Property Name() As String
            Get
                Return m_name
            End Get
        End Property

        Public Sub Add(ByVal item As Person)
            m_friends.Add(item)
        End Sub
    End Class
End Namespace