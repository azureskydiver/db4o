' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System

Namespace Db4objects.Db4odoc.Blobs
    Public Class Car
        Dim _model As String
        Dim _img As CarImage

        Public Sub New(ByVal model As String)
            _model = model
            _img = New CarImage()
            _img.FileName = _model + ".jpg"
        End Sub

        Public ReadOnly Property CarImage() As CarImage
            Get
                Return _img
            End Get
        End Property

        Public Overrides Function ToString() As String
            Return String.Format("{0}({1})", _model, _img.FileName)
        End Function
    End Class
End Namespace


