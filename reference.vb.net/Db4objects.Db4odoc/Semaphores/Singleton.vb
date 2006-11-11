' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query

Namespace com.db4odoc.f1.semaphores
    ' This class	demonstrates the use	of a	semaphore to ensure that only
    ' one instance of a	certain	class is stored to an IObjectContainer.
    ' 
    ' Caution	!!! The getSingleton method contains a commit()	call.	

    Public Class Singleton

        ' returns	a singleton object of one	class for an	IObjectContainer.
        ' Caution !!!	This	method contains a commit() call.

        Public Shared Function GetSingleton(ByVal objectContainer As IObjectContainer, ByVal clazz As System.Type) As Object
            Dim obj As Object = queryForSingletonClass(objectContainer, clazz)
            If Not obj Is Nothing Then
                Return obj
            End If

            Dim semaphore As String = "Singleton#getSingleton_" + clazz.FullName

            If Not objectContainer.Ext().SetSemaphore(semaphore, 10000) Then
                Throw New Exception("Blocked semaphore " + semaphore)
            End If

            obj = queryForSingletonClass(objectContainer, clazz)

            If obj Is Nothing Then

                Try
                    obj = System.Activator.CreateInstance(clazz)
                Catch e As Exception
                    System.Console.WriteLine(e.Message)
                End Try

                objectContainer.Set(obj)
                ' 				 Not Not Not  CAUTION Not Not Not 
                ' 				 * There is a	commit	call here.
                ' 				 * 
                ' 				 * The commit call	is	necessary, so	other transactions
                ' 				 * can see the New inserted Object.
                ' 				 */
                objectContainer.Commit()

            End If

            objectContainer.Ext().ReleaseSemaphore(semaphore)

            Return obj
        End Function

        Private Shared Function queryForSingletonClass(ByVal objectContainer As IObjectContainer, ByVal clazz As System.Type) As Object
            Dim q As IQuery = objectContainer.Query()
            q.Constrain(clazz)
            Dim objectSet As IObjectSet = q.Execute()
            If objectSet.Size() = 1 Then
                Return objectSet.Next()
            End If
            If objectSet.Size() > 1 Then
                Throw New Exception("Singleton problem. Multiple	instances of: " + clazz.FullName)
            End If
            Return Nothing
        End Function

    End Class

End Namespace
