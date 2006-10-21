' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports com.db4o.config.attributes

Namespace com.db4odoc.f1.attributes
    Public Class Car
        <Indexed()> _
        Private _model As String
        Private _year As Integer
    End Class
End Namespace
