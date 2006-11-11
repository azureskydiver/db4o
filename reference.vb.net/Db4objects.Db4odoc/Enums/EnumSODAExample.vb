' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Enums
    Public Class EnumSODAExample
        Public Shared Function FindOpenDoors(ByVal container As IObjectContainer) As IObjectSet
            Dim query As IQuery = container.Query()
            query.Constrain(GetType(Door))
            query.Descend("_state").Constrain(DoorState.Open)
            Return query.Execute()
        End Function
    End Class
End Namespace
	