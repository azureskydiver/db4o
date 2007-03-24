Imports Db4objects.Db4o.Types
Namespace Db4objects.Db4odoc.SelectivePersistence

    Class NotStorable
        Implements ITransientClass

        Public Overloads Overrides Function ToString() As String
            Return "NotStorable class"
        End Function
    End Class
End Namespace