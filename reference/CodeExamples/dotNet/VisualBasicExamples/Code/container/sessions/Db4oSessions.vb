Imports Db4objects.Db4o
Imports Db4objects.Db4o.CS

Namespace Db4oDoc.Code.Container.Sessions
    Public Class Db4oSessions
        Private Const DatabaseFileName As String = "database.db4o"


        Public Sub Sessions()
            ' #example: Session object container
            Dim rootContainer As IObjectContainer = Db4oEmbedded.OpenFile(DatabaseFileName)

            ' open the db4o-session. For example at the beginning for a web-request
            Using session As IObjectContainer = rootContainer.Ext().OpenSession()
                ' do the operations on the session-container
                session.Store(New Person("Joe"))
            End Using
            ' #end example

            rootContainer.Dispose()
        End Sub

        Public Sub EmbeddedClient()
            ' #example: Embedded client
            Dim server As IObjectServer = Db4oClientServer.OpenServer(DatabaseFileName, 0)

            ' open the db4o-embedded client. For example at the beginning for a web-request
            Using container As IObjectContainer = server.OpenClient()
                ' do the operations on the session-container
                container.Store(New Person("Joe"))
            End Using
            ' #end example

            server.Dispose()
        End Sub


        Private Class Person
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
    End Class
End Namespace
