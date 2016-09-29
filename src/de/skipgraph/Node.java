package de.skipgraph;

import de.skipgraph.operations.*;

import java.math.BigDecimal;
import java.util.List;

import static de.skipgraph.operations.ModifyContactsOperation.ContactType.PREV;

public class Node {

	private ElementTable elementTable;
	private ContactTable contactTable;
	private final int id;

	/**
	 * if a Node is constructed without a basic contactTable given it is assumed that this is
	 * the first node of the SkipGraph.
	 * @param elementTableMinSize	specify the minimum size of the ElementTable
	 * @param elementTableMaxSize specify the maximum size of the ElementTable
	 */
	public Node(int elementTableMinSize, int elementTableMaxSize) {
		this.elementTable = new ElementTable(elementTableMinSize, elementTableMaxSize);
		this.contactTable = new ContactTable(this);
		Contact selfContact = thisContact();
		this.contactTable.addLevel(new ContactLevel(selfContact, selfContact, 1));
		id = createID();
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
		Contact selfContact = thisContact();
		this.contactTable.addLevel(new ContactLevel(selfContact, selfContact, 1));
		id = createID();
	}

	public Node(ElementTable elementTable, ContactTable contactTable) {
		this.elementTable = elementTable;
		this.contactTable = contactTable;
		contactTable.joinLevels();
		id = createID();
	}

	private int createID() {
		return Main.skipGraph.getNodeCounter();
	}

	public int getId() {
		return id;
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
		int prevFree = prevNode.getElementTable().getNumberOfFreeSlots();
		int nextFree = nextNode.getElementTable().getNumberOfFreeSlots();

		if (nextNode == this || prevNode == this || (prevFree+nextFree) <= elementTable.size() ) {
			System.out.println("  ! hold your ground");
			return;
		}

		Main.skipGraph.buildDotFile(DotFileBuilder.getFileCounter()+"_ID"+this+"_beforeLeaving.dot", true);

		// special case for first node in skipgraph - TODO: replace 0 with null
		if (elementTable.getRangeStart().equals(BigDecimal.ZERO) && (nextFree > elementTable.size())) {
			nextNode.getElementTable().extendElementTableAtStart(elementTable, nextNode);
			contactTable.updatingContactsOnLeave();
		}
		// special case for last node in skipgraph
		else if (elementTable.getRangeEnd() == null && (prevFree > elementTable.size())) {
				prevNode.getElementTable().extendElementTableAtEnd(elementTable, prevNode);
				contactTable.updatingContactsOnLeave();
		}
		// distribute the elements equally on predecessor and successor
		else {
			double ratio = (double) prevFree / (prevFree + nextFree);
			ElementTable nextElementTable = elementTable.split(ratio);
			prevNode.getElementTable().extendElementTableAtEnd(elementTable, prevNode);
			nextNode.getElementTable().extendElementTableAtStart(nextElementTable, nextNode);
			contactTable.updatingContactsOnLeave();
		}
	}

	public void leave_OldBehaviour() {
		Node prevNode = contactTable.getPrevNode();
		Node nextNode = contactTable.getNextNode();
		// check if successor has enough free space in table
		if (nextNode != this &&
				nextNode.getElementTable().getNumberOfFreeSlots() > elementTable.size()) {
			Main.skipGraph.buildDotFile(DotFileBuilder.getFileCounter()+"_ID"+this+"_beforeLeaving.dot", true);
			nextNode.getElementTable().extendElementTableAtStart(elementTable, nextNode);
			contactTable.updatingContactsOnLeave();
		}
		// else check if predecessor has enough free space in table
		else if (prevNode != this &&
				prevNode.getElementTable().getNumberOfFreeSlots() > elementTable.size()) {
			Main.skipGraph.buildDotFile(DotFileBuilder.getFileCounter()+"_ID"+this+"_beforeLeaving.dot", true);
			prevNode.getElementTable().extendElementTableAtEnd(elementTable, prevNode);
			contactTable.updatingContactsOnLeave();
		}
		else {
			System.out.println("  ! hold your ground");
		}
	}

	public void split() {
		Node nextNode = contactTable.getNextNode();

		// TODO: split table so that predecessor and succor get equal amount based on their number of free slots
		// split element table
		ElementTable newElementTable = elementTable.split();
		// update all contacts
		contactTable.updateAllContacts();

		// check if successor != this && can handle separated elements
		if (nextNode != this &&
				newElementTable.size() < nextNode.getElementTable().getNumberOfFreeSlots()) {
			nextNode.elementTable.extendElementTableAtStart(newElementTable, nextNode);
		}
		// otherwise create a new Node and make it join the SkipGraph as new successor on level 0
		else {
			// local variable
			Contact nextContact = getContactTable().getLevel(0).getNextContact();

			// create new node
			Node newNode = new Node(newElementTable);
			// build new basic contactTable for new node
			ContactTable newContactTable = new ContactTable(newNode);
			newContactTable.addLevel(new ContactLevel(thisContact(), nextContact, 1));
			newNode.setContactTable(newContactTable);

			// inform NextNode of new PrevNode
			ModifyContactsOperation setPrevOnNext = new SetContactOperation(0, 1, PREV, newNode);
			nextContact.getNode().execute(setPrevOnNext);

			// update own nextContact
			getContactTable().getLevel(0).setNextContact(newNode);

			// make newNode join the graph on all levels
			newNode.getContactTable().joinLevels();
		}

	}

	public String toString() {
		return String.format("%d", id);
	}

	public void printElementTable() {
		System.out.print(elementTable);
	}

	public void printContactTable() {
		System.out.print(contactTable);
	}

	public Contact thisContact() {
		return new Contact(this, this.getElementTable().getRangeStart(), this.getElementTable().getRangeEnd());
	}

}
