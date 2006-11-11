Imports System.Globalization

Namespace Db4objects.Db4odoc.Evaluations
    ''' <summary>
    ''' A CultureInfo aware list of objects.
    ''' CultureInfo objects hold a native pointer to 
    ''' a system structure.
    ''' </summary>
    Public Class LocalizedItemList
        Private _culture As CultureInfo

        Private _items As String()

        Public Sub New(ByVal culture As CultureInfo, ByVal items As String())
            _culture = culture
            _items = items
        End Sub

        Public Overloads Overrides Function ToString() As String
            Return String.Join(String.Concat(_culture.TextInfo.ListSeparator, " "), _items)
        End Function

    End Class
End Namespace
