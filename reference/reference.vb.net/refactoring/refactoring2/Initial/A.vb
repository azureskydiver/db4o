' Copyright (C) 2007  db4objects Inc.  http://www.db4o.com 

Namespace Db4objects.Db4odoc.Refactoring.Initial
    Class A
        Public name As String

        Public Overloads Overrides Function ToString() As String
            Return name
        End Function
    End Class
End Namespace