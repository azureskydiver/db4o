' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Namespace Db4objects.Db4odoc.Autoinc

    Class TestObject
        Inherits CountedObject
        Private _name As String

        Public Sub New(ByVal name As String)
            _name = name
        End Sub

        Public Overloads Overrides Function ToString() As String
            Return _name + "/" + _id.ToString()
        End Function
    End Class
End Namespace