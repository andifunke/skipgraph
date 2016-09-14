package de.skipgraph.operations;

import de.skipgraph.Element;
import de.skipgraph.Node;

import java.util.ArrayList;
import java.util.List;

public class GetOperation extends QueryOperation {

	private int index;

	/**
	 * constructor
	 *
	 * @param index
	 */
	public GetOperation(int index) {
		this.index = index;
	}

	@Override
	public List<Element> execute(Node node) {
		System.out.println("trying to get element #" + index);

		// check if node is responsible for index
		// if so return element for index (element is wrapped in a list of size==1)
		if (index < node.getElementTable().size()) {
			List<Element> returnList = new ArrayList<>();
			returnList.add(node.getElementTable().get(index));
			return returnList;
		}
		// if node not responsible for end of table: forward to next node
		else if (node.getElementTable().getRangeEnd() != null) {
			index -= node.getElementTable().size();
			System.out.print("-> next: ");
			return node.getContactTable().getNextNode().execute(this);
		}
		// if end of table: index exceeds SkipGraph
		else {
			System.out.println("  ! index out of bounds");
			return null;
		}

	}
}
