' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Imports System.Collections.Generic
Namespace Db4objects.Db4odoc.ListOperations

    Class ListObject
        Private _name As String
        Private _data As List(Of DataObject)

        Public Sub New()
            _data = New List(Of DataObject)
        End Sub

        Public Property Name() As String
            Get
                Return _name
            End Get
            Set(ByVal value As String)
                _name = value
            End Set
        End Property

        Public Property Data() As List(Of DataObject)
            Get
                Return _data
            End Get
            Set(ByVal value As List(Of DataObject))
                _data = value
            End Set
        End Property
    End Class
End Namespace