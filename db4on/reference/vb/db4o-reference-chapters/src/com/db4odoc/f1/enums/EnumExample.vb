' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports com.db4o

Namespace com.db4odoc.f1.Enums
    Class EnumExample
        Public Shared Function FindOpenDoors(ByVal container As ObjectContainer) As ObjectSet
            Dim theDoor As Door = New Door(DoorState.Open)
            Return container.Get(theDoor)
        End Function
    End Class
End Namespace
