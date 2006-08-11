Imports System.Globalization
Imports com.db4o
Imports com.db4o.config
Namespace com.db4odoc.f1.evaluations
    Public Class CultureInfoTranslator
        Implements ObjectConstructor
        Public Function OnStore(ByVal container As ObjectContainer, ByVal applicationObject As Object) As Object Implements ObjectConstructor.OnStore
            System.Console.WriteLine("onStore for {0}", applicationObject)
            Return (DirectCast(applicationObject, CultureInfo)).Name
        End Function

        Public Function OnInstantiate(ByVal container As ObjectContainer, ByVal storedObject As Object) As Object Implements ObjectConstructor.OnInstantiate
            System.Console.WriteLine("onInstantiate for {0}", storedObject)
            Dim name As String = DirectCast(storedObject, String)
            Return CultureInfo.CreateSpecificCulture(name)
        End Function

        Public Sub OnActivate(ByVal container As ObjectContainer, ByVal applicationObject As Object, ByVal storedObject As Object) Implements ObjectConstructor.OnActivate
            System.Console.WriteLine("onActivate for {0}/{1}", applicationObject, storedObject)
        End Sub

        Public Function StoredClass() As j4o.lang.Class Implements ObjectConstructor.StoredClass
            Return j4o.lang.[Class].GetClassForType(GetType(String))
        End Function

    End Class
End Namespace
