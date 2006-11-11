' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports Db4objects.Db4o

Namespace Db4objects.Db4odoc.Enums
    Class EnumExample
        Public Shared Function FindOpenDoors(ByVal container As IObjectContainer) As IObjectSet
            Dim theDoor As Door = New Door(DoorState.Open)
            Return container.Get(theDoor)
        End Function
    End Class
End Namespace
