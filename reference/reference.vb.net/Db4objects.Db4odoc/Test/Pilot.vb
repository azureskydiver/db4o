' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 

Namespace Db4objects.Db4odoc.Test
    Public Class Pilot
        Public _name As String
        Private _points As Integer

        Public Sub New()
            _name = ""
        End Sub

        Public Sub New(ByVal name As String, ByVal points As Integer)
            _name = name
            _points = points
        End Sub

        Public Property Name() As String
            Get
                Return _name
            End Get
            Set(ByVal value As String)
                _name = value
            End Set
        End Property


        Public Overloads Overrides Function ToString() As String
            Return String.Format("{0}", _name)
        End Function

    End Class

End Namespace
