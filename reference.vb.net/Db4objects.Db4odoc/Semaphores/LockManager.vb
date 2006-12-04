' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext

Namespace com.db4odoc.f1.semaphores
    ' 	*
    ' 	 * This class demonstrates a very rudimentary implementation
    ' 	 * of virtual "locks" on objects with db4o. All code that is
    ' 	 * intended to obey these locks will have to call lock() and
    ' 	 * unlock().  
    ' 	 */
    Public Class LockManager

        Private ReadOnly SEMAPHORE_NAME As String = "locked: "
        Private ReadOnly WAIT_FOR_AVAILABILITY As Integer = 300  ' 300 milliseconds

        Private ReadOnly _objectContainer As IExtObjectContainer

        Public Sub New(ByVal objectContainer As IObjectContainer)
            _objectContainer = objectContainer.Ext()
        End Sub

        Public Function Lock(ByVal obj As Object) As Boolean
            Dim id As Long = _objectContainer.GetID(obj)
            Return _objectContainer.SetSemaphore(SEMAPHORE_NAME + id.ToString(), WAIT_FOR_AVAILABILITY)
        End Function

        Public Sub Unlock(ByVal obj As Object)
            Dim id As Long = _objectContainer.GetID(obj)
            _objectContainer.ReleaseSemaphore(SEMAPHORE_NAME + id.ToString())
        End Sub
    End Class

End Namespace
