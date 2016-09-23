package de.skipgraph.operations;

import de.skipgraph.*;

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

	public String toString() {
		return String.format("JoinLevelOperation. level %d. prefix %d. joiningNode %s", level, prefix, joiningNode);
	}

	@Override
	public List<Element> execute(Node thisNode) {

		//System.out.println("node executing the joinLevelOperation: "+thisNode);
		//System.out.println(this);
		//System.out.println("#aaa#aaa#"+thisNode.getContactTable().size());

		if (thisNode.getContactTable().size() <= level) {
			// TODO: reactivate next line
			//thisNode.getContactTable().joinLevels();
			// TODO: deactivcate next 2 lines
			//System.out.println("#as#dasd#"+thisNode.getContactTable().size());
			byte newPrefix = SkipGraph.generatePrefix();
			Contact selfContact = thisNode.thisContact();
			ContactLevel temporarySelfContactLevel = new ContactLevel(selfContact, selfContact, newPrefix);
			thisNode.getContactTable().addLevel(temporarySelfContactLevel);
			return null;
		}

		// local variables
		Node nextNode = thisNode.getContactTable().getLevel(level-1).getNextContact().getNode();

		// node has the same prefix on this level as joining node
		if (thisNode.getContactTable().getLevel(level).getPrefix() == prefix) {

			Node prevNode = thisNode.getContactTable().getLevel(level).getPrevContact().getNode();
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
