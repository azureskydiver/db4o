' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Namespace Db4objects.Db4odoc.CommitCallbacks

    Public Class Item
        Private _number As Integer
        Private _word As String

        Public Sub New(ByVal number As Integer, ByVal word As String)
            _number = number
            _word = word
        End Sub

        Public ReadOnly Property Word() As String
            Get
                Return _word
            End Get
        End Property

        Public ReadOnly Property Number() As Integer
            Get
                Return _number
            End Get
        End Property

        Public Overloads Overrides Function ToString() As String
            Return _number.ToString() + "/" + _word
        End Function
    End Class
End Namespace