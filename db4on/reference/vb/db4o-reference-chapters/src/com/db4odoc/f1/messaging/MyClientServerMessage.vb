' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System

Namespace com.db4odoc.f1.messaging
    Class MyClientServerMessage
        Private _info As String

        Public Sub New(ByVal info As String)
            Me._info = info
        End Sub

        Public Overrides Function ToString() As String
            Return "MyClientServerMessage: " + _info
        End Function
    End Class
End Namespace

