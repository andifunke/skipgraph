package de.skipgraph.operations;

import de.skipgraph.ContactLevel;
import de.skipgraph.Element;
import de.skipgraph.Node;

import java.util.List;

import static de.skipgraph.operations.ModifyContactsOperation.ContactType.NEXT;
import static de.skipgraph.operations.ModifyContactsOperation.ContactType.PREV;

public class JoinLevelOperation extends ModifyContactsOperation {

	private int level;
	private byte prefix;
	private Node joiningNode;

	public JoinLevelOperation(int level, byte prefix, Node joiningNode) {
		this.level = level;
		this.prefix = prefix;
		this.joiningNode = joiningNode;
	}

	@Override
	public List<Element> execute(Node thisNode) {

		//System.out.println("#aaa#aaa#"+thisNode.getContactTable().size());

		if (thisNode.getContactTable().size() <= level) {
			// TODO: reactivate next line
			//thisNode.getContactTable().joinLevels();
			// TODO: deactivcate next 2 lines
			//System.out.println("#as#dasd#"+thisNode.getContactTable().size());
			byte newPrefix = (byte)(Math.random() + 0.5);
			ContactLevel temporarySelfContactLevel = new ContactLevel(thisNode.thisContact(), thisNode.thisContact(), newPrefix);
			thisNode.getContactTable().addLevel(temporarySelfContactLevel);
		}

		// local variables
		Node prevNode = thisNode.getContactTable().getLevel(level).getPrevContact().getNode();
		Node nextNode = thisNode.getContactTable().getLevel(level).getNextContact().getNode();

		// node has the same prefix on this level as joining node
		if (thisNode.getContactTable().getLevel(level).getPrefix() == prefix) {

			// updates nextNode on previous node
			ModifyContactsOperation setNextOnPrev = new SetContactOperation(level, prefix, NEXT, joiningNode);
			prevNode.execute(setNextOnPrev);
			// updates prevNode on joining node
			ModifyContactsOperation setPrevOnJoining = new SetContactOperation(level, prefix, PREV, prevNode);
			joiningNode.execute(setPrevOnJoining);
			// updates nextNode on joining node
			ModifyContactsOperation setNextOnJoining = new SetContactOperation(level, prefix, NEXT, thisNode);
			joiningNode.execute(setNextOnJoining);
			// updates prevNode on this node
			thisNode.getContactTable().getLevel(level).setPrevContact(joiningNode);
		}
		// node has a different prefix on this level and forwards request to the next node on this level
		// (making sure nextNode != joiningNode)
		else if (nextNode != thisNode && nextNode != joiningNode) {
			nextNode.execute(this);
		}
		return null;
	}
}
