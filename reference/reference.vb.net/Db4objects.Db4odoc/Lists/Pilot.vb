' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 

Namespace Db4objects.Db4odoc.Lists
    Public Class Pilot
        Private _name As String
        Private _points As Integer

        Public Sub New(ByVal name As String, ByVal points As Integer)
            _name = name
            _points = points
        End Sub

        Public ReadOnly Property Name() As String
            Get
                Return _name
            End Get
        End Property

        Public Overloads Overrides Function ToString() As String
            Return String.Format("{0}:{1}", _name, _points)
        End Function

    End Class
End Namespace

