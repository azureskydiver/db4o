Imports System
Imports System.Globalization
Imports com.db4o

Namespace com.db4odoc.f1.evaluations
    Public Class TranslatorExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args As String())
            TryStoreWithCallConstructors()
            TryStoreWithoutCallConstructors()
            StoreWithTranslator()
        End Sub
        ' end Main

        Public Shared Sub TryStoreWithCallConstructors()
            Db4oFactory.Configure().ExceptionsOnNotStorable(True)
            Db4oFactory.Configure().ObjectClass(GetType(CultureInfo)).CallConstructor(True)
            TryStoreAndRetrieve()
        End Sub
        ' end TryStoreWithCallConstructors

        Public Shared Sub TryStoreWithoutCallConstructors()
            Db4oFactory.Configure().ObjectClass(GetType(CultureInfo)).CallConstructor(False)
            ' trying to store objects that hold onto
            ' system resources can be pretty nasty
            ' uncomment the following line to see
            ' how nasty it can be
            'TryStoreAndRetrieve();
        End Sub
        ' end TryStoreWithoutCallConstructors

        Public Shared Sub StoreWithTranslator()
            Db4oFactory.Configure().ObjectClass(GetType(CultureInfo)).Translate(New CultureInfoTranslator())
            TryStoreAndRetrieve()
            Db4oFactory.Configure().ObjectClass(GetType(CultureInfo)).Translate(Nothing)
        End Sub
        ' end StoreWithTranslator

        Public Shared Sub TryStoreAndRetrieve()
            Dim db As ObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim champs As String() = New String() {"Ayrton Senna", "Nelson Piquet"}
                Dim LocalizedItemList As LocalizedItemList = New LocalizedItemList(CultureInfo.CreateSpecificCulture("pt-BR"), champs)
                System.Console.WriteLine("ORIGINAL: {0}", LocalizedItemList)
                db.[Set](LocalizedItemList)
            Catch x As Exception
                System.Console.WriteLine(x)
                Return
            Finally
                db.Close()
            End Try
            db = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim result As ObjectSet = db.[Get](GetType(LocalizedItemList))
                While result.HasNext()
                    Dim LocalizedItemList As LocalizedItemList = DirectCast(result.[Next](), LocalizedItemList)
                    System.Console.WriteLine("RETRIEVED: {0}", LocalizedItemList)
                    db.Delete(LocalizedItemList)
                End While
            Finally
                db.Close()
            End Try
        End Sub
        ' end TryStoreAndRetrieve
    End Class
End Namespace
