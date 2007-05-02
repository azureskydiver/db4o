/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System.Globalization;

namespace Db4objects.Db4odoc.Translators
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