package de.skipgraph;

import de.skipgraph.operations.*;

import java.util.List;

import static de.skipgraph.operations.ModifyContactsOperation.ContactType.PREV;

public class Node {

	private ElementTable elementTable;
	private ContactTable contactTable;

	/**
	 * if a Node is constructed without a basic contactTable given it is assumed that this is
	 * the first node of the SkipGraph.
	 * @param elementTableMinSize	specify the minimum size of the ElementTable
	 * @param elementTableMaxSize specify the maximum size of the ElementTable
	 */
	public Node(int elementTableMinSize, int elementTableMaxSize) {
		this.elementTable = new ElementTable(elementTableMinSize, elementTableMaxSize);
		this.contactTable = new ContactTable(this);
		this.contactTable.addLevel(new ContactLevel(thisContact(), thisContact(), (byte)0));
	}

	/**
	 * use this constructor if you want a new node to join an existing SkipGraph
	 * @param elementTableMinSize specify the minimum size of the ElementTable
	 * @param elementTableMaxSize specify the maximum size of the ElementTable
	 * @param contactTable			construct Node with an existing contactTable (at least level 0),
	 *                            so it can join the SkipGraph easily
	 */
	public Node(int elementTableMinSize, int elementTableMaxSize, ContactTable contactTable) {
		this(new ElementTable(elementTableMinSize, elementTableMaxSize), contactTable);
	}

	public Node(ElementTable elementTable) {
		this.elementTable = elementTable;
		this.contactTable = new ContactTable(this);
		this.contactTable.addLevel(new ContactLevel(thisContact(), thisContact(), (byte)0));
	}

	public Node(ElementTable elementTable, ContactTable contactTable) {
		this.elementTable = elementTable;
		this.contactTable = contactTable;
		contactTable.joinLevels();
	}

	public ElementTable getElementTable() {
		return elementTable;
	}

	public void setElementTable(ElementTable elementTable) {
		this.elementTable = elementTable;
	}

	public ContactTable getContactTable() {
		return contactTable;
	}

	public void setContactTable(ContactTable contactTable) {
		this.contactTable = contactTable;
	}

	public List<Element> execute(QueryOperation queryOperation) {
		return queryOperation.execute(this);
	}


	public void leave() {
		Node prevNode = contactTable.getPrevNode();
		Node nextNode = contactTable.getNextNode();

		// check if successor has enough free space in table
		if (nextNode != this &&
				nextNode.getElementTable().getNumberOfFreeSlots() > elementTable.size()) {
			nextNode.getElementTable().extendElementTableAtStart(elementTable, nextNode);
			contactTable.updatingContactsOnLeave();
		}
		// else check if predecessor has enough free space in table
		else if (prevNode != this &&
				prevNode.getElementTable().getNumberOfFreeSlots() > elementTable.size()) {
			prevNode.getElementTable().extendElementTableAtEnd(elementTable, prevNode);
			contactTable.updatingContactsOnLeave();
		}
		// TODO: split table and distribute to prev and next
		else {
			System.out.println("  ! hold your ground");
		}
	}

	public void split() {
		Node nextNode = contactTable.getNextNode();

		// split element table
		ElementTable newElementTable = elementTable.split();

		// check if successor != this && can handle separated elements
		if (nextNode != this &&
				newElementTable.size() < nextNode.getElementTable().getNumberOfFreeSlots()) {
			nextNode.elementTable.extendElementTableAtStart(newElementTable, nextNode);
		}
		// otherwise create a new Node and make it join the SkipGraph as new successor on level 0
		else {
			Contact nextContact = getContactTable().getLevel(0).getNextContact();

			// create new node
			Node newNode = new Node(newElementTable);
			// build new basic contactTable for new node
			ContactTable newContactTable = new ContactTable(newNode);
			newContactTable.addLevel(new ContactLevel(thisContact(), nextContact, (byte)0));
			newNode.setContactTable(newContactTable);

			// inform NextNode of new PrevNode
			ModifyContactsOperation setPrevOnNext = new SetContactOperation((short)0, (byte)0, PREV, newNode);
			nextContact.getNode().execute(setPrevOnNext);

			// update own nextContact
			getContactTable().getLevel(0).setNextContact(newNode);

			// make newNode join the graph on all levels
			newNode.getContactTable().joinLevels();
		}

	}

	public void printElementTable() {
		System.out.println(String.format("### printing table (start:%s, end:%s, size:%d, min-size:%d, max-size:%d) ###",
				elementTable.getRangeStart(), elementTable.getRangeEnd() == null ? "inf" :
						elementTable.getRangeEnd(), elementTable.size(), elementTable.getMinSize(), elementTable.getMaxSize()));
		elementTable.sort();
		for (int i = 0; i < elementTable.size(); i++) {
			String index = String.format("%03d ", i);
			System.out.println(index + elementTable.get(i));
		}
	}

	public Contact thisContact() {
		return new Contact(this, this.getElementTable().getRangeStart(), this.getElementTable().getRangeEnd());
	}

}
