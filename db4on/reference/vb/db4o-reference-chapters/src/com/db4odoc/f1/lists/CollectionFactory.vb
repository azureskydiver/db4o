' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System
Imports System.Collections

Namespace com.db4odoc.f1.lists
    Public Class CollectionFactory
        Public Shared Function NewList() As IList
            Return New VerboseList()
        End Function
    End Class
End Namespace

