' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com
Imports Db4objects.Db4o.Types
Namespace Db4ojects.Db4odoc.TAExamples

    Public Class Image
        Private _blob As IBlob = Nothing
        Private _fileName As String = Nothing

        Public Sub New(ByVal fileName As String)
            _fileName = fileName
        End Sub

        ' Image recording and reading functionality to be implemented ...
    End Class
End Namespace