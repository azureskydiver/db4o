' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System
Imports System.Collections

Namespace Db4objects.Db4odoc.Lists
    Public Class CollectionFactory
        Public Shared Function NewList() As IList
            Return New VerboseList()
        End Function
    End Class
End Namespace

