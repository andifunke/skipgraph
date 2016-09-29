package de.skipgraph;

import de.skipgraph.operations.JoinLevelOperation;
import de.skipgraph.operations.ModifyContactsOperation;
import de.skipgraph.operations.SetContactOnLeaveOperation;
import de.skipgraph.operations.SetContactOperation;

import java.math.BigDecimal;
import java.util.*;

import static de.skipgraph.operations.ModifyContactsOperation.ContactType.*;

public class ContactTable {

	private final Node node;
	private LinkedList<ContactLevel> contactTable = new LinkedList<>();
	private Node lastJoiningNode;


	public ContactTable(Node node) {
		this.node = node;
	}

	public int size() {
		return contactTable.size();
	}

	public ContactLevel get(int i) {
		return contactTable.get(i);
	}

	public Node getLastJoiningNode() {
		return lastJoiningNode;
	}

	public void setLastJoiningNode(Node lastJoiningNode) {
		this.lastJoiningNode = lastJoiningNode;
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
			int prefix = getLevel(i).getPrefix();

			// updating the prevNode of successor
			SetContactOperation setPrevOnNext = new SetContactOnLeaveOperation(i, prefix, PREV, currentPrev);
			currentNext.getNode().execute(setPrevOnNext);
			// updating the nextNode of predecessor
			SetContactOperation setNextOnPrev = new SetContactOnLeaveOperation(i, prefix, NEXT, currentNext);
			currentPrev.getNode().execute(setNextOnPrev);
		}
		Main.skipGraph.buildDotFile(DotFileBuilder.getFileCounter()+"_ID"+node+"_afterLeaving.dot", true);
		System.out.println("   ID:" + node + " has left");
	}

	/**
	 * given that a node has prev and next contacts on level 0 it builds up prev and next contacts on higher levels
	 * until it gets to a level where it is its own contact
	 */
	public void joinLevels() {
		Main.skipGraph.buildDotFile(DotFileBuilder.getFileCounter()+"_ID"+node+"_beforeJoining.dot", true);

		while (node.getContactTable().getLevel(size()-1).getNextContact().getNode() != node) {
			int prefix = Main.skipGraph.generatePrefix();
			Contact selfContact = node.thisContact();
			ContactLevel temporarySelfContactLevel = new ContactLevel(selfContact, selfContact, prefix);
			node.getContactTable().addLevel(temporarySelfContactLevel);
			ModifyContactsOperation joinLevel = new JoinLevelOperation(size()-1, prefix, node);
			node.getContactTable().getLevel(size()-2).getNextContact().getNode().execute(joinLevel);
		}

		Main.skipGraph.buildDotFile(DotFileBuilder.getFileCounter()+"_ID"+node+"_afterJoining.dot", true);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("--- contact table <size:%d>\n", size()));
		for (int i=0; i<size(); i++) {
			String index = String.format("level %02d, prefix %d, ", i, get(i).getPrefix());
			sb.append(index + get(i) + "\n");
		}
		return sb.toString();
	}

	/**
	 * this method checks if there is more than one level which is only self referencing
	 */
	public void deleteRedundantLevels() {
		int max = contactTable.size()-1;
		int counter = 0;
		while (get(max-counter) != null &&
				get(max-counter).getPrevContact().getNode() == node &&
				get(max-counter).getNextContact().getNode() == node) {
			counter++;
			System.out.println("number of selfcontact levels: " + counter);
		}
		for ( ; counter>1; counter--) {
			contactTable.removeLast();
			System.out.println("deleting redundant level");
		}
	}

	/**
	 * this method updates all self contacts, in case the element's table range has changed
	 */
	public void updateSelfContacts() {
		for (ContactLevel level : contactTable) {
			if (level.getPrevContact().getNode() == node) {
				level.getPrevContact().setRangeStart(node.getElementTable().getRangeStart());
				level.getPrevContact().setRangeEnd(node.getElementTable().getRangeEnd());
			}
			if (level.getNextContact().getNode() == node) {
				level.getNextContact().setRangeStart(node.getElementTable().getRangeStart());
				level.getNextContact().setRangeEnd(node.getElementTable().getRangeEnd());
			}
		}
	}

	/**
	 * updates all contacts, in case the element's table range has changed
	 */
	public void updateAllContacts() {
		for (short i=0; i<size(); i++) {
			// using local variables for better readability
			Contact selfContact = node.thisContact();
			int prefix = getLevel(i).getPrefix();

			// updating predecessor
			SetContactOperation setNextContactOnPredecessor = new SetContactOperation(i, prefix, NEXT, selfContact);
			getLevel(i).getPrevContact().getNode().execute(setNextContactOnPredecessor);

			// updating successor
			SetContactOperation setPrevContactOnSuccessor = new SetContactOperation(i, prefix, PREV, selfContact);
			getLevel(i).getNextContact().getNode().execute(setPrevContactOnSuccessor);
		}
	}

}
