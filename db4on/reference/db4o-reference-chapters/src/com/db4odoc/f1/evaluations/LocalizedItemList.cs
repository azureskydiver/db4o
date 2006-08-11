using System.Globalization;

namespace com.db4odoc.f1.evaluations
{
    public class LocalizedItemList
    {
        CultureInfo _culture;
        string[] _items;

        public LocalizedItemList(CultureInfo culture, string[] items)
        {
            _culture = culture;
            _items = items;
        }

        override public string ToString()
        {
            return string.Join(string.Concat(_culture.TextInfo.ListSeparator,  " "), _items);
        }
    }
}