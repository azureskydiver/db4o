Imports System
Imports System.Collections

Namespace Db4objects.Db4odoc.Evaluations
    Public Class Car
        Private _model As String

        Private _pilot As Pilot

        Private _history As IList

        Public Sub New(ByVal model As String)
            Me.New(model, New ArrayList())
        End Sub

        Public Sub New(ByVal model As String, ByVal history As IList)
            _model = model
            _pilot = Nothing
            _history = history
        End Sub

        Public Property Pilot() As Pilot
            Get
                Return _pilot
            End Get
            Set(ByVal value As Pilot)
                _pilot = value
            End Set
        End Property

        Public ReadOnly Property Model() As String
            Get
                Return _model
            End Get
        End Property

        Public ReadOnly Property History() As IList
            Get
                Return _history
            End Get
        End Property

        Public Sub Snapshot()
            _history.Add(New SensorReadout(Poll(), DateTime.Now, Me))
        End Sub

        Protected Function Poll() As Double()
            Dim factor As Integer = _history.Count + 1
            Return New Double() {0.1 * factor, 0.2 * factor, 0.3 * factor}
        End Function

        Public Overloads Overrides Function ToString() As String
            Return String.Format("{0}[{1}]/{2}", _model, _pilot, _history.Count)
        End Function

    End Class
End Namespace
