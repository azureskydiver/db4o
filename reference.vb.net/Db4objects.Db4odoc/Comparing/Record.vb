' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Namespace Db4objects.Db4odoc.Comparing

    Class Record
        Private _record As MyString

        Public Sub New(ByVal record As String)
            _record = New MyString(record)
        End Sub

        Public Overloads Overrides Function ToString() As String
            Return _record.ToString
        End Function
    End Class
End Namespace
