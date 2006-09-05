' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Namespace com.db4odoc.f1.debugging
    Public Class Car
        Private _model As String

      
        Public Sub New(ByVal model As String)
            _model = model
        End Sub

        Public Overloads Overrides Function ToString() As String
            Return _model
        End Function

    End Class
End Namespace
