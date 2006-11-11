' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System.IO
Imports Db4objects.Db4o.IO

Namespace Db4objects.Db4odoc.IOs
    Public Class LoggingAdapter
        Inherits IoAdapter
        Private _delegate As Sharpen.IO.RandomAccessFile

        Public Sub New()
        End Sub

        Protected Friend Sub New(ByVal path As String, ByVal lockFile As Boolean, ByVal initialLength As Long)
            _delegate = New Sharpen.IO.RandomAccessFile(path, "rw")
            If initialLength > 0 Then
                _delegate.Seek(initialLength - 1)
                Dim b As Byte() = New Byte() {0}
                _delegate.Write(b)
            End If
        End Sub

        Public Sub SetOut(ByVal outs As TextWriter)
            System.Console.SetOut(outs)
        End Sub

        Public Overrides Sub Close()
            System.Console.WriteLine("Closing file")
            _delegate.Close()
        End Sub

        Public Overrides Sub Delete(ByVal path As String)
            System.Console.WriteLine("Deleting file " + path)
            File.Delete(path)
        End Sub

        Public Overrides Function Exists(ByVal path As String) As Boolean
            Dim existingFile As Sharpen.IO.File = New Sharpen.IO.File(path)
            Return existingFile.Exists() And existingFile.Length() > 0
        End Function

        Public Overrides Function GetLength() As Long
            System.Console.WriteLine("File length:" + _delegate.Length().ToString())
            Return _delegate.Length()
        End Function

        Public Overrides Function Open(ByVal path As String, ByVal lockFile As Boolean, ByVal initialLength As Long) As IoAdapter
            System.Console.WriteLine("Opening file " + path)
            Return New LoggingAdapter(path, lockFile, initialLength)
        End Function

        Public Overrides Function Read(ByVal bytes() As Byte, ByVal length As Integer) As Integer
            System.Console.WriteLine("Reading " + length.ToString() + " bytes")
            Return _delegate.Read(bytes, 0, length)
        End Function

        Public Overrides Sub Seek(ByVal pos As Long)
            System.Console.WriteLine("Setting pointer position to  " + pos.ToString())
            _delegate.Seek(pos)
        End Sub

        Public Overrides Sub Sync()
            System.Console.WriteLine("Synchronizing")
            _delegate.GetFD().Sync()
        End Sub

        Public Overrides Sub Write(ByVal buffer() As Byte, ByVal length As Integer)
            System.Console.WriteLine("Writing " + length.ToString() + " bytes")
            _delegate.Write(buffer, 0, length)
        End Sub
    End Class
End Namespace
