using System;
using System.Drawing;
using System.Windows.Forms;

namespace Db4oTestRunner
{
	class RichTextBoxLogger : MarshalByRefObject, ILogger
	{
		public static ILogger For(RichTextBox target)
		{
			return new RichTextBoxLogger(target);
		}

		public void LogMessage(string message, params object[] args)
		{
			AppendText(message, args);
		}

		public void LogMessageFormated(Color foreground, Font font, string message, params object[] args)
		{
			AppendSelected(
					text =>
						{
							text.SelectionColor = foreground;
							text.SelectionFont = font;
						},
					message,
					args);
		}

		public void LogError(string message, params object[] args)
		{
			AppendSelected(
					text => text.SelectionColor = Color.Red,
					message,
					args);
		}

		public void LogException(Exception ex)
		{
			AppendSelected(
				text =>
					{
						text.SelectionColor = Color.Red;
						text.SelectionFont = new Font("Tahoma", 9, FontStyle.Bold);
					}, 
				ex.ToString());
		}

		private RichTextBoxLogger(RichTextBox target)
		{
			_target = target;
		}

		private void AppendSelected(Action<RichTextBox> action, string message, params object[] args)
		{
			try
			{
				int startIndex = _target.Text.Length;
				AppendText(message, args);

				_target.SelectionStart = startIndex;
				_target.SelectionLength = _target.Text.Length - startIndex;

				action(_target);
			}
			finally
			{
				_target.SelectionStart = _target.Text.Length-1;
				_target.SelectionLength = 1;
			}
		}

		private void AppendText(string message, object[] args)
		{
			_target.AppendText(string.Format(message, args) + "\r\n");
		}

		private readonly RichTextBox _target;
	}
}
