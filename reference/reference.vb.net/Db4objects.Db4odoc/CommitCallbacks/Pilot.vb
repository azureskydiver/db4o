' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Namespace Db4objects.Db4odoc.CommitCallbacks

    Class Pilot
        Private _name As String

        Public ReadOnly Property Name() As String
            Get
                Return _name
            End Get
        End Property

        Public Sub New(ByVal name As String)
            _name = name
        End Sub
    End Class
End Namespace