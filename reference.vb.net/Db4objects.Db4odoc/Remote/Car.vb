' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Namespace Db4objects.Db4odoc.Remote
    Public Class Car
        Dim _model As String

        Public Sub New(ByVal model As String)
            _model = model
        End Sub

        Public Property Model() As String
            Get
                Return _model
            End Get
            Set(ByVal value As String)
                _model = value
            End Set
        End Property

        Public Overrides Function ToString() As String
            Return _model
        End Function
    End Class
End Namespace


