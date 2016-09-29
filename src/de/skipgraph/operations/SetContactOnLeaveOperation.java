package de.skipgraph.operations;

import de.skipgraph.Contact;
import de.skipgraph.Element;
import de.skipgraph.Node;

import java.util.List;

public class SetContactOnLeaveOperation extends SetContactOperation {

	public SetContactOnLeaveOperation(int level, int prefix, ContactType contactType, Contact contact) {
		super(level, prefix, contactType, contact);
	}

	/**
	 *
	 * @param level        number of level
	 * @param prefix       prefix for this level (usually 0 or 1)
	 * @param contactType  PREV to update prevContact or NEXT to update nextContact
	 * @param node         node you want to set the contact to
	 */
	public SetContactOnLeaveOperation(int level, int prefix, ContactType contactType, Node node) {
		super(level, prefix, contactType, node);
	}

	@Override
	public List<Element> execute(Node thisNode) {
		updateContactsOn(thisNode);
		thisNode.getContactTable().deleteRedundantLevels();
		return null;
	}
}
