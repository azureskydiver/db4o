' Copyright (C) 2004   db4objects Inc.   http://www.db4o.com 

Imports System.Text

Imports Db4objects.Db4o.Foundation
Imports Db4objects.Db4o.Internal
Imports Db4objects.Db4o.Marshall

Namespace Db4objects.Db4odoc.Typehandler

    Public Class StringBuilderHandler
        Implements ITypeHandler4


        Public Sub New()
        End Sub


        Public Sub Delete(ByVal context As IDeleteContext) Implements ITypeHandler4.Delete
            context.ReadSlot()
        End Sub
        ' end Delete


        Private Shared Function Compare(ByVal a_compare As StringBuilder, ByVal a_with As StringBuilder) As Integer
            If a_compare Is Nothing Then
                If a_with Is Nothing Then
                    Return 0
                End If
                Return -1
            End If
            If a_with Is Nothing Then
                Return 1
            End If
            Dim c_compare As Char() = New Char(a_compare.Length - 1) {}
            a_compare.CopyTo(0, c_compare, 0, a_compare.Length)
            Dim c_with As Char() = New Char(a_with.Length - 1) {}
            a_with.CopyTo(0, c_with, 0, a_with.Length)

            Return CompareChars(c_compare, c_with)
        End Function
        ' end Compare

        Private Shared Function CompareChars(ByVal compare As Char(), ByVal [with] As Char()) As Integer
            Dim min As Integer = IIf(compare.Length < [with].Length, compare.Length, [with].Length)
            For i As Integer = 0 To min - 1
                If compare(i) <> [with](i) Then
                    Return compare(i).CompareTo([with](i))
                End If
            Next
            Return compare.Length - [with].Length
        End Function
        ' end CompareChars


        Public Sub Write(ByVal context As IWriteContext, ByVal obj As Object) Implements ITypeHandler4.Write
            Dim str As String = DirectCast(obj, StringBuilder).ToString()
            Dim buffer As IWriteBuffer = context
            buffer.WriteInt(str.Length)
            WriteToBuffer(buffer, str)
        End Sub
        ' end Write

        Private Shared Sub WriteToBuffer(ByVal buffer As IWriteBuffer, ByVal str As String)
            Dim length As Integer = str.Length
            Dim chars As Char() = New Char(length - 1) {}
            str.CopyTo(0, chars, 0, length)
            For i As Integer = 0 To length - 1
                buffer.WriteByte(CByte(Val(chars(i)) And 255))
                buffer.WriteByte(CByte(Val(chars(i)) >> 8))
            Next
        End Sub
        ' end WriteToBuffer


        Private Shared Function ReadBuffer(ByVal buffer As IReadBuffer, ByVal length As Integer) As String
            Dim chars As Char() = New Char(length - 1) {}
            For ii As Integer = 0 To length - 1
                chars(ii) = ChrW(((buffer.ReadByte() And 255) Or ((buffer.ReadByte() And 255) << 8)))
            Next
            Return New String(chars, 0, length)
        End Function
        ' end ReadBuffer

        Public Function Read(ByVal context As IReadContext) As Object Implements ITypeHandler4.Read
            Dim buffer As IReadBuffer = context
            Dim str As String = ""
            buffer.ReadInt()
            buffer.ReadInt()
            Dim length As Integer = buffer.ReadInt()
            If length > 0 Then
                str = ReadBuffer(buffer, length)
            End If
            Return New StringBuilder(str)
        End Function
        ' end Read

        Public Sub Defragment(ByVal context As IDefragmentContext) Implements ITypeHandler4.Defragment
            ' To stay compatible with the old marshaller family
            ' In the marshaller family 0 number 4 represented
            ' length reqiored to store ID and object length information
            context.IncrementOffset(4)
        End Sub
        ' end Defragment

        Public Function PrepareComparison(ByVal obj As Object) As IPreparedComparison Implements ITypeHandler4.PrepareComparison
            Return New PreparedComparison(obj)
        End Function
        ' end PrepareComparison

        Private Class PreparedComparison
            Implements IPreparedComparison
            Private _source As Object = Nothing

            Public Sub New(ByVal source As Object)
                _source = source
            End Sub

            Public Function CompareTo(ByVal target As Object) As Integer Implements IPreparedComparison.CompareTo
                Return Compare(DirectCast(_source, StringBuilder), DirectCast(target, StringBuilder))
            End Function
        End Class
        ' end PreparedComparison
    End Class

End Namespace
