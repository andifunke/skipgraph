package de.skipgraph.operations;

import de.skipgraph.Contact;
import de.skipgraph.Element;
import de.skipgraph.Node;

import java.util.List;

public class SetContactOperation extends ModifyContactsOperation {

	private ContactType contactType;
	private final Contact contact;
	private int level;
	private byte prefix;

	public SetContactOperation(int level, byte prefix, ContactType contactType, Contact contact) {
		this.level = level;
		this.prefix = prefix;
		this.contactType = contactType;
		this.contact = contact;
	}

	public SetContactOperation(int level, byte prefix, ContactType contactType, Node node) {
		this.level = level;
		this.prefix = prefix;
		this.contactType = contactType;
		this.contact = new Contact(node, node.getElementTable().getRangeStart(), node.getElementTable().getRangeEnd());
	}

	public Contact getContact() {
		return contact;
	}

	@Override
	public List<Element> execute(Node thisNode) {
		System.out.println(thisNode);
		// in a perfect world this case should never be true
		if (thisNode.getContactTable().getLevel(level).getPrefix() != prefix) {
			return null;
		}
		if (thisNode.getContactTable().size() > level) {
			switch (contactType) {
				case PREV:
					thisNode.getContactTable().getLevel(level).setPrevContact(contact);
					break;
				case NEXT:
					thisNode.getContactTable().getLevel(level).setNextContact(contact);
					break;
			}
		}
		return null;
	}
}
