' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 

Namespace Db4objects.Db4odoc.MetaInfo
    Public Class Pilot
        Private _name As String

        Public Sub New(ByVal name As String)
            _name = name
        End Sub

        Public ReadOnly Property Name() As String
            Get
                Return _name
            End Get
        End Property

        Public Overloads Overrides Function ToString() As String
            Return String.Format("{0}", _name)
        End Function

    End Class
End Namespace

