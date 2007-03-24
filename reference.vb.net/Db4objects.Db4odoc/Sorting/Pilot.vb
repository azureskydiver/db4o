' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 

Namespace Db4objects.Db4odoc.Sorting
    Class Pilot
        Private _name As String
        Private _points As Integer

        Public Sub New(ByVal name As String)
            Me._name = name
        End Sub

        Public Sub New(ByVal name As String, ByVal points As Integer)
            Me._name = name
            Me._points = points
        End Sub

        Public ReadOnly Property Name() As String
            Get
                Return _name
            End Get
        End Property

        Public ReadOnly Property Points() As Integer
            Get
                Return _points
            End Get
        End Property

        Public Overloads Overrides Function ToString() As String
            If _points = 0 Then
                Return _name
            Else
                Return String.Format("{0}/{1}", _name, _points)
            End If
        End Function
    End Class
End Namespace