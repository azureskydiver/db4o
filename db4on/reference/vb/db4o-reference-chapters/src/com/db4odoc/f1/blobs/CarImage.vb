' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports j4o.io
Imports j4o.lang
Imports com.db4o.ext
Imports com.db4o.types


Namespace com.db4odoc.f1.blobs

    Public Class CarImage
        Dim _blob As Blob
        Private _file As String = Nothing
        Private inFolder As String = "blobs\in\"
        Private outFolder As String = "blobs\out\"

        Public Sub New()

        End Sub

        Public Property FileName() As String
            Get
                Return _file
            End Get
            Set(ByVal Value As String)
                _file = Value
            End Set
        End Property

        Public Function ReadFile() As Boolean
            _blob.ReadFrom(New File(inFolder + _file))
            Dim s As Double = _blob.GetStatus()
            While s > Status.COMPLETED
                Thread.sleep(50)
                s = _blob.GetStatus()
            End While
            Return (s = Status.COMPLETED)
        End Function

        Public Function WriteFile() As Boolean
            _blob.writeTo(New File(outFolder + _file))
            Dim s As Double = _blob.GetStatus()
            While s > Status.COMPLETED
                Thread.sleep(50)
                s = _blob.GetStatus()
            End While
            Return (s = Status.COMPLETED)
        End Function
    End Class
End Namespace
