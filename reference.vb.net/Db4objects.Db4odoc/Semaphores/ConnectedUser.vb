' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.Collections
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.Query


Namespace com.db4odoc.f1.semaphores
    ' 	
    ' 	  This class demonstrates how semaphores can be used 
    ' 	  to rule  race conditions when providing exact and
    ' 	  up-to-date information about all connected clients 
    ' 	  on a server. The class also can be used to make sure
    ' 	  that only one login is possible with a give user name
    ' 	  and ipAddress combination.
    ' 	 
    Public Class ConnectedUser

        Public Shared ReadOnly SEMAPHORE_CONNECTED As String = "ConnectedUser_"
        Public Shared ReadOnly SEMAPHORE_LOCK_ACCESS As String = "ConnectedUser_Lock_"

        Public Shared ReadOnly TIMEOUT As Integer = 10000  ' concurrent access timeout 10 seconds

        Dim userName As String
        Dim ipAddress As String

        Public Sub New(ByVal userName As String, ByVal ipAddress As String)
            Me.userName = userName
            Me.ipAddress = ipAddress
        End Sub

        ' make sure to call this on the server before opening the database
        ' to improve querying speed 
        Public Shared Sub Configure()
            Dim objectClass As IObjectClass = Db4o.Configure().ObjectClass(GetType(ConnectedUser))
            objectClass.ObjectField("userName").Indexed(True)
            objectClass.ObjectField("ipAddress").Indexed(True)
        End Sub

        ' call this on the client to ensure to have a ConnectedUser record 
        ' in the database file and the semaphore set
        Public Shared Sub Login(ByVal client As IObjectContainer, ByVal userName As String, ByVal ipAddress As String)
            If Not client.Ext().SetSemaphore(SEMAPHORE_LOCK_ACCESS, TIMEOUT) Then
                Throw New Exception("Timeout Trying to get access to ConnectedUser lock")
            End If
            Dim q As IQuery = client.Query()
            q.Constrain(GetType(ConnectedUser))
            q.Descend("userName").Constrain(userName)
            q.Descend("ipAddress").Constrain(ipAddress)
            If q.Execute().Size() = 0 Then
                client.Set(New ConnectedUser(userName, ipAddress))
                client.Commit()
            End If
            Dim connectedSemaphoreName As String = SEMAPHORE_CONNECTED + userName + ipAddress
            Dim unique As Boolean = client.Ext().SetSemaphore(connectedSemaphoreName, 0)
            client.Ext().ReleaseSemaphore(SEMAPHORE_LOCK_ACCESS)
            If Not unique Then
                Throw New Exception("Two clients with same userName and ipAddress")
            End If
        End Sub

        ' here is your list of all connected users, callable on the server
        Public Shared Function ConnectedUsers(ByVal server As IObjectServer) As IList
            Dim serverObjectContainer As IExtObjectContainer = server.Ext().ObjectContainer().Ext()
            If serverObjectContainer.SetSemaphore(SEMAPHORE_LOCK_ACCESS, TIMEOUT) Then
                Throw New Exception("Timeout Trying to get access to ConnectedUser lock")
            End If
            Dim list As IList = New ArrayList()
            Dim q As IQuery = serverObjectContainer.Query()
            q.Constrain(GetType(ConnectedUser))
            Dim objectSet As IObjectSet = q.Execute()
            While objectSet.HasNext()
                Dim connectedUser As ConnectedUser = CType(objectSet.Next(), ConnectedUser)
                Dim connectedSemaphoreName As String = SEMAPHORE_CONNECTED + connectedUser.userName + connectedUser.ipAddress
                If serverObjectContainer.SetSemaphore(connectedSemaphoreName, TIMEOUT) Then
                    serverObjectContainer.Delete(connectedUser)
                Else
                    list.Add(connectedUser)
                End If
            End While
            serverObjectContainer.Commit()
            serverObjectContainer.ReleaseSemaphore(SEMAPHORE_LOCK_ACCESS)
            Return list
        End Function
    End Class
End Namespace
