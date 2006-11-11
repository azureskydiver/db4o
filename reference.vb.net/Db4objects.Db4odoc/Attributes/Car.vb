' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports Db4objects.Db4o

Namespace Db4objects.Db4odoc.Attributes
    Public Class Car
        <Config.Attributes.Indexed()> Private _model As String
        Private _year As Integer
    End Class
End Namespace
