' Copyright (C) 2007  db4objects Inc.  http://www.db4o.com 
'Imports Db4objects.Db4odoc.Refactoring.Initial

Namespace Db4objects.Db4odoc.Refactoring.Refactored
    Class D
        Inherits Initial.B

        Public storedDate As System.DateTime

        Public Overloads Overrides Function ToString() As String
            Return name + "/" + number.ToString() + ": " + storedDate.ToString()
        End Function

    End Class
End Namespace