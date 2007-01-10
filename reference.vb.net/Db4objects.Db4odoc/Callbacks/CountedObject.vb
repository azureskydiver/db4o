' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
'This class is used to mark classes that need to get an autoincremented ID
Namespace Db4objects.Db4odoc.Callbacks

    MustInherit Class CountedObject
        Protected _id As Integer

        Public Property Id() As Integer
            Get
                Return _id
            End Get
            Set(ByVal value As Integer)
                _id = value
            End Set
        End Property
    End Class
End Namespace