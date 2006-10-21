' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports com.db4o
Imports com.db4o.query

Namespace com.db4odoc.f1.Enums
    Public Class EnumSODAExample
        Public Shared Function FindOpenDoors(ByVal container As ObjectContainer) As ObjectSet
            Dim query As Query = container.Query()
            query.Constrain(GetType(Door))
            query.Descend("_state").Constrain(DoorState.Open)
            Return query.Execute()
        End Function
    End Class
End Namespace
	