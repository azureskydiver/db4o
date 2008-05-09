package contacts;

import java.util.*;

public class ContactList {
	
	private List<Contact> _entries = new ArrayList<Contact>();

	public void add(Contact contact) {
		_entries.add(contact);
	}
	
	/**
	 * @sharpen.property
	 */
	public Iterable<Contact> entries() {
		return _entries;
	}

}
