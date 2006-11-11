Imports System.Globalization
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config

Namespace Db4objects.Db4odoc.Evaluations
    Public Class CultureInfoTranslator
        Implements IObjectConstructor
        Public Function OnStore(ByVal container As IObjectContainer, ByVal applicationObject As Object) As Object Implements IObjectConstructor.OnStore
            System.Console.WriteLine("onStore for {0}", applicationObject)
            Return (DirectCast(applicationObject, CultureInfo)).Name
        End Function

        Public Function OnInstantiate(ByVal container As IObjectContainer, ByVal storedObject As Object) As Object Implements IObjectConstructor.OnInstantiate
            System.Console.WriteLine("onInstantiate for {0}", storedObject)
            Dim name As String = DirectCast(storedObject, String)
            Return CultureInfo.CreateSpecificCulture(name)
        End Function

        Public Sub OnActivate(ByVal container As IObjectContainer, ByVal applicationObject As Object, ByVal storedObject As Object) Implements IObjectConstructor.OnActivate
            System.Console.WriteLine("onActivate for {0}/{1}", applicationObject, storedObject)
        End Sub

        Public Function StoredClass() As System.Type Implements IObjectConstructor.StoredClass
            Return GetType(String)
        End Function

    End Class
End Namespace
