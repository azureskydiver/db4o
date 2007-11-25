' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.Globalization
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config

Namespace Db4objects.Db4odoc.Translators
    Public Class TranslatorExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args As String())
            TryStoreWithCallConstructors()
            TryStoreWithoutCallConstructors()
            StoreWithTranslator()
        End Sub
        ' end Main

        Private Shared Sub TryStoreWithCallConstructors()
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ExceptionsOnNotStorable(True)
            configuration.ObjectClass(GetType(CultureInfo)).CallConstructor(True)
            TryStoreAndRetrieve(configuration)
        End Sub
        ' end TryStoreWithCallConstructors

        Private Shared Sub TryStoreWithoutCallConstructors()
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ObjectClass(GetType(CultureInfo)).CallConstructor(False)
            ' trying to store objects that hold onto
            ' system resources can be pretty nasty
            ' uncomment the following line to see
            ' how nasty it can be
            'TryStoreAndRetrieve(configuration);
        End Sub
        ' end TryStoreWithoutCallConstructors

        Private Shared Sub StoreWithTranslator()
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ObjectClass(GetType(CultureInfo)).Translate(New CultureInfoTranslator())
            TryStoreAndRetrieve(configuration)
        End Sub
        ' end StoreWithTranslator

        Private Shared Sub TryStoreAndRetrieve(ByVal configuration As IConfiguration)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim champs As String() = New String() {"Ayrton Senna", "Nelson Piquet"}
                Dim LocalizedItemList As LocalizedItemList = New LocalizedItemList(CultureInfo.CreateSpecificCulture("pt-BR"), champs)
                System.Console.WriteLine("ORIGINAL: {0}", LocalizedItemList)
                db.Set(LocalizedItemList)
            Catch x As Exception
                System.Console.WriteLine(x)
                Return
            Finally
                db.Close()
            End Try
            db = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim result As IObjectSet = db.Get(GetType(LocalizedItemList))
                While result.HasNext()
                    Dim LocalizedItemList As LocalizedItemList = DirectCast(result.Next(), LocalizedItemList)
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
