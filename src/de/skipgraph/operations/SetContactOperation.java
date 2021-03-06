package de.skipgraph.operations;

import de.skipgraph.Contact;
import de.skipgraph.Element;
import de.skipgraph.Node;

import java.util.List;

public class SetContactOperation extends ModifyContactsOperation {

	private ContactType contactType;
	private final Contact contact;
	private int level;
	private int prefix;

	public SetContactOperation(int level, int prefix, ContactType contactType, Contact contact) {
		this.level = level;
		this.prefix = prefix;
		this.contactType = contactType;
		this.contact = contact;
	}

	/**
	 *
	 * @param level        number of level
	 * @param prefix       prefix for this level (usually 0 or 1)
	 * @param contactType  PREV to update prevContact or NEXT to update nextContact
	 * @param node         node you want to set the contact to
	 */
	public SetContactOperation(int level, int prefix, ContactType contactType, Node node) {
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
		updateContactsOn(thisNode);
		return null;
	}

	void updateContactsOn(Node thisNode) {
		// in a perfect world this case should never be true
		if (thisNode.getContactTable().getLevel(level).getPrefix() != prefix) {
			System.out.println("**********************************but it is");
			return;
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
	}

}
