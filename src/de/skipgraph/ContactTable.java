package de.skipgraph;

import de.skipgraph.operations.JoinLevelOperation;
import de.skipgraph.operations.ModifyContactsOperation;
import de.skipgraph.operations.SetContactOperation;

import java.math.BigDecimal;
import java.util.*;

import static de.skipgraph.operations.ModifyContactsOperation.ContactType.*;

public class ContactTable {

	private final Node node;

	public ContactTable(Node node) {
		this.node = node;
	}

	private LinkedList<ContactLevel> contactTable = new LinkedList<>();

	public int size() {
		return contactTable.size();
	}

	public boolean addLevel(ContactLevel level) {
		return contactTable.add(level);
	}

	public ContactLevel getLevel(int i) {
		return contactTable.get(i);
	}

	public Node getPrevNode() {
		return getPrevNodeOnLevel(0);
	}

	public Node getNextNode() {
		return getNextNodeOnLevel(0);
	}

	public Node getNextNodeOnLevel(int i) {
		return getLevel(i).getNextContact().getNode();
	}

	public Node getPrevNodeOnLevel(int i) {
		return getLevel(i).getPrevContact().getNode();
	}

	/**
	 * this method finds the NextNode on the highest possible level which range start is below or equal the given value
	 * @param value	the value a query starts with
	 * @return			returns the highest NextNode on the highest possible level that doen't exceed the value
	 */
	public Node getNextNodeForValue(BigDecimal value) {
		// TODO: durch Iterator ersetzen
		for (int i=size()-1; i>-1; i--) {
			// check whether nextNode's rangeStart is actually higher (avoid feedback loop)
			if (node.getElementTable().getRangeStart().compareTo(getLevel(i).getNextContact().getRangeStart()) >= 0) {
				continue;
			}
			// check whether nextNode's rangeStart exceeds value
			else if (getLevel(i).getNextContact().getRangeStart().compareTo(value) > 0) {
				continue;
			}
			return getNextNodeOnLevel(i);
		}
		// fallback to level 0
		return getNextNodeOnLevel(0);
	}

	/**
	 * this method finds the PrevNode on the highest possible level which range start is below or equal the given value
	 * @param value	the value a query starts with
	 * @return			returns the highest PrevNode on the highest possible level that doen't exceed the value
	 */
	public Node getPrevNodeForValue(BigDecimal value) {
		// TODO: durch Iterator ersetzen
		for (int i=size()-1; i>-1; i--) {
			if (getLevel(i).getPrevContact().getRangeStart().compareTo(value) > 0) continue;
			return getPrevNodeOnLevel(i);
		}
		return getPrevNodeOnLevel(0);
	}

	public ContactLevel getHighestLevel() {
		return contactTable.getLast();
	}

	public void updatingContactsOnLeave() {
		for (short i=0; i<size(); i++) {
			// using local variables for better readability
			Contact currentPrev = getLevel(i).getPrevContact();
			Contact currentNext = getLevel(i).getNextContact();
			byte prefix = getLevel(i).getPrefix();

			// updating the prevNode of nextNode
			SetContactOperation setPrevOnNext = new SetContactOperation(i, prefix, PREV, currentPrev);
			currentNext.getNode().execute(setPrevOnNext);
			// updating the nextNode of prevNode
			SetContactOperation setNextOnPrev = new SetContactOperation(i, prefix, NEXT, currentNext);
			currentPrev.getNode().execute(setNextOnPrev);
		}
		System.out.println("  ! bye bye");
	}

	/* // replaced by queryOperations
	void updatingContactsOnLeave() {
		for (int i=0; i<size(); i++) {
			// using local variables for better readability
			Node currentPrev = getPrefix(i).getPrevContact().getNode();
			Node currentNext = getPrefix(i).getNextContact().getNode();

			// updating the prevNode of nextNode
			currentNext.getContactTable().getPrefix(i).getPrevContact().setNode(currentPrev);
			// updating the nextNode of prevNode
			currentPrev.getContactTable().getPrefix(i).getNextContact().setNode(currentNext);
		}
		System.out.println("  ! bye bye");
	}
	*/

	/**
	 * given that a node has prev and next contacts on level 0 it builds up prev and next contacts on higher levels
	 * until it gets to a level where it is its own contact
	 */
	public void joinLevels() {
		while (node.getContactTable().getLevel(size()-1).getNextContact().getNode() != node) {
			byte prefix = (byte)(Math.random() + 0.5);
			Contact selfContact = node.thisContact();
			ContactLevel temporarySelfContactLevel = new ContactLevel(selfContact, selfContact, prefix);
			node.getContactTable().addLevel(temporarySelfContactLevel);
			ModifyContactsOperation joinLevel = new JoinLevelOperation(size()-1, prefix, node);
			node.getContactTable().getLevel(size()-2).getNextContact().getNode().execute(joinLevel);
			System.out.println("Size: "+size());
		}
	}

}
