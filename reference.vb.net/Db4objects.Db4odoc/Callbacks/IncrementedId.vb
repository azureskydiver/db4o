' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

' Singleton class used to keep auotincrement information 
' and give the next available ID on request

Imports System
Imports Db4objects.Db4o
Namespace Db4objects.Db4odoc.Callbacks

    Class IncrementedId
        Private _no As Integer
        Private Shared _ref As IncrementedId

        Private Sub New()
            _no = 0
        End Sub
        ' end New

        Public Function GetNextID(ByVal db As IObjectContainer) As Integer
            System.Math.Min(System.Threading.Interlocked.Increment(_no), _no - 1)
            db.Set(Me)
            Return _no
        End Function
        ' end GetNextID

        Public Shared Function GetIdObject(ByVal db As IObjectContainer) As IncrementedId
            ' if _ref is not assigned yet:
            If _ref Is Nothing Then
                ' check if there is a stored instance from the previous 
                ' session in the database
                Dim os As IObjectSet = db.Get(GetType(IncrementedId))
                If os.Size > 0 Then
                    _ref = CType(os.Next, IncrementedId)
                End If
            End If
            If _ref Is Nothing Then
                ' create new instance and store it
                Console.WriteLine("Id object is created")
                _ref = New IncrementedId
                db.Set(_ref)
            End If
            Return _ref
        End Function
        ' end GetIdObject

    End Class
End Namespace