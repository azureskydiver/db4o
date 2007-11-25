' Copyright (C) 2007  db4objects Inc.  http://www.db4o.com 

Namespace Db4objects.Db4odoc.Refactoring.Initial
    Class B
        Inherits A
        Public number As Integer

        Public Overloads Overrides Function ToString() As String
            Return name + "/" + number.ToString()
        End Function
    End Class
End Namespace