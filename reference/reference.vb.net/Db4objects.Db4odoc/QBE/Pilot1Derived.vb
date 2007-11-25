' Copyright (C) 2007 db4objects Inc. http://www.db4o.com

Namespace Db4objects.Db4odoc.QBE
    Class Pilot1Derived
        Inherits Pilot1
        Public Sub New(ByVal name As String, ByVal points As Integer)

            MyBase.New(name, points)
        End Sub
    End Class
End Namespace