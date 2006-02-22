Imports System
Imports System.Globalization
Imports com.db4o

Namespace com.db4o.f1.chapter6
	Public Class TranslatorExample
	Inherits Util
		Public Shared Sub Main(ByVal args As String())
			TryStoreWithCallConstructors()
			TryStoreWithoutCallConstructors()
			StoreWithTranslator()
		End Sub

		Public Shared Sub TryStoreWithCallConstructors()
            Global.com.db4o.Db4o.Configure().ExceptionsOnNotStorable(True)
            Global.com.db4o.Db4o.Configure().ObjectClass(GetType(CultureInfo)).CallConstructor(True)
			TryStoreAndRetrieve()
		End Sub

		Public Shared Sub TryStoreWithoutCallConstructors()
            Global.com.db4o.Db4o.Configure().ObjectClass(GetType(CultureInfo)).CallConstructor(False)
			' trying to store objects that hold onto
			' system resources can be pretty nasty
			' uncomment the following line to see
			' how nasty it can be
			'tryStoreAndRetrieve();
		End Sub

		Public Shared Sub StoreWithTranslator()
            Global.com.db4o.Db4o.Configure().ObjectClass(GetType(CultureInfo)).Translate(New CultureInfoTranslator())
			TryStoreAndRetrieve()
            Global.com.db4o.Db4o.Configure().ObjectClass(GetType(CultureInfo)).Translate(Nothing)
		End Sub

		Public Shared Sub TryStoreAndRetrieve()
            Dim db As ObjectContainer = Global.com.db4o.Db4o.OpenFile(Util.YapFileName)
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
            db = Global.com.db4o.Db4o.OpenFile(Util.YapFileName)
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

	End Class
End Namespace
