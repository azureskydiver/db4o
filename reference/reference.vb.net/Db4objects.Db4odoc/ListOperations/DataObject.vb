' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Namespace Db4objects.Db4odoc.ListOperations

    Class DataObject
        Private _name As String
        Private _data As String

        Public Sub New()
        End Sub

        Public Property Name() As String
            Get
                Return _name
            End Get
            Set(ByVal value As String)
                _name = value
            End Set
        End Property

        Public Property Data() As String
            Get
                Return _data
            End Get
            Set(ByVal value As String)
                _data = value
            End Set
        End Property

        Public Overloads Overrides Function ToString() As String
            Return String.Format("{0}/{1}", _name, _data)
        End Function
    End Class
End Namespace