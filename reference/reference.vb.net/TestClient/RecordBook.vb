' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Namespace Db4objects.Db4odoc.NoClasses.Client

    Public Class RecordBook
        Private _notes As String(,)
        Private _recordCounter As Integer

        Public Sub New()
            _notes = New String(20, 3) {}
            _recordCounter = 0
        End Sub

        Public Sub AddRecord(ByVal period As String, ByVal pilotName As String, ByVal note As String)
            _notes(_recordCounter, 0) = period
            _notes(_recordCounter, 1) = pilotName
            _notes(_recordCounter, 2) = note
            System.Math.Min(System.Threading.Interlocked.Increment(_recordCounter), _recordCounter - 1)
        End Sub

        Public Overloads Overrides Function ToString() As String
            Dim temp As String
            temp = "Record book: " & Microsoft.VisualBasic.Chr(10) & ""
            Dim i As Integer = 0
            While i < _recordCounter
                temp = temp + _notes(i, 0) + "/" + _notes(i, 1) + "/" + _notes(i, 2) + "" & Microsoft.VisualBasic.Chr(10) & ""
                System.Math.Min(System.Threading.Interlocked.Increment(i), i - 1)
            End While
            Return temp
        End Function
    End Class
End Namespace