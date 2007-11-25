' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Namespace Db4objects.Db4odoc.Comparing

    Class MyString
        Private _string As String

        Public Sub New(ByVal str As String)
            _string = str
        End Sub

        Public Overloads Overrides Function ToString() As String
            Return _string
        End Function
    End Class
End Namespace