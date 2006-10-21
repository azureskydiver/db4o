' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports com.db4o
Imports com.db4o.query

Namespace com.db4odoc.f1.semaphores
    ' This class	demonstrates the use	of a	semaphore to ensure that only
    ' one instance of a	certain	class is stored to an ObjectContainer.
    ' 
    ' Caution	!!! The getSingleton method contains a commit()	call.	

    Public Class Singleton

        ' returns	a singleton object of one	class for an	ObjectContainer.
        ' Caution !!!	This	method contains a commit() call.

        Public Shared Function GetSingleton(ByVal objectContainer As ObjectContainer, ByVal clazz As j4o.lang.Class) As Object
            Dim obj As Object = queryForSingletonClass(objectContainer, clazz)
            If Not obj Is Nothing Then
                Return obj
            End If

            Dim semaphore As String = "Singleton#getSingleton_" + clazz.GetName()

            If Not objectContainer.Ext().SetSemaphore(semaphore, 10000) Then
                Throw New Exception("Blocked semaphore " + semaphore)
            End If

            obj = queryForSingletonClass(objectContainer, clazz)

            If obj Is Nothing Then

                Try
                    obj = clazz.NewInstance()
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

        Private Shared Function queryForSingletonClass(ByVal objectContainer As ObjectContainer, ByVal clazz As j4o.lang.Class) As Object
            Dim q As Query = objectContainer.Query()
            q.Constrain(clazz)
            Dim objectSet As ObjectSet = q.Execute()
            If objectSet.Size() = 1 Then
                Return objectSet.Next()
            End If
            If objectSet.Size() > 1 Then
                Throw New Exception("Singleton problem. Multiple	instances of: " + clazz.GetName())
            End If
            Return Nothing
        End Function

    End Class

End Namespace
