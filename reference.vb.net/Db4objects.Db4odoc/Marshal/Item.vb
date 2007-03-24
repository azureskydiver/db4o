' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Imports System
Namespace Db4objects.Db4odoc.marshal

    Class Item
        Public _one As Integer
        Public _two As Long
        Public _three As Integer

        Public Sub New(ByVal one As Integer, ByVal two As Long, ByVal three As Integer)
            _one = one
            _two = two
            _three = three
        End Sub

        Public Sub New()
        End Sub

        Public Overloads Overrides Function ToString() As String
            Return String.Format("{0:X}, {1:X}, {2:N}", _one, _two, _three)
        End Function
    End Class
End Namespace