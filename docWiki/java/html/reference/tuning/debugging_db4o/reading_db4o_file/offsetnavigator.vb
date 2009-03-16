Sub SearchOffset()
    Dim pos As Integer
    pos = Val(Selection.Text)
    If pos = 0 Then
        MsgBox ("The selection is not a number")
    Else
        ActiveDocument.Content.Characters(pos).Select
    End If
End Sub
