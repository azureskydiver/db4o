' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Foundation
Namespace Db4objects.Db4odoc.marshal

    Class ItemMarshaller
        Implements IObjectMarshaller

        ' Write field values to a byte array
        ' No reflection is used
        Public Sub WriteFields(ByVal obj As Object, ByVal slot As Byte(), ByVal offset As Integer) Implements IObjectMarshaller.WriteFields
            Dim item As Item = CType(obj, Item)
            PrimitiveCodec.WriteInt(slot, offset, item._one)
            offset += PrimitiveCodec.INT_LENGTH
            PrimitiveCodec.WriteLong(slot, offset, item._two)
            offset += PrimitiveCodec.LONG_LENGTH
            PrimitiveCodec.WriteInt(slot, offset, item._three)
        End Sub
        ' end WriteFields

        ' Restore field values from the byte array
        ' No reflection is used
        Public Sub ReadFields(ByVal obj As Object, ByVal slot As Byte(), ByVal offset As Integer) Implements IObjectMarshaller.ReadFields
            Dim item As Item = CType(obj, Item)
            item._one = PrimitiveCodec.ReadInt(slot, offset)
            offset += PrimitiveCodec.INT_LENGTH
            item._two = PrimitiveCodec.ReadLong(slot, offset)
            offset += PrimitiveCodec.LONG_LENGTH
            item._three = PrimitiveCodec.ReadInt(slot, offset)
        End Sub
        ' end ReadFields

        Public Function MarshalledFieldLength() As Integer Implements IObjectMarshaller.MarshalledFieldLength
            Return PrimitiveCodec.INT_LENGTH * 2 + PrimitiveCodec.LONG_LENGTH
        End Function
        ' end MarshalledFieldLength

    End Class
End Namespace