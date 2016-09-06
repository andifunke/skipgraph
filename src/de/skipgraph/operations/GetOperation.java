package de.skipgraph.operations;

import de.skipgraph.SkipGraphElement;
import de.skipgraph.SkipGraphNode;

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
	public List<SkipGraphElement> execute(SkipGraphNode node) {
		System.out.println("trying to get element #" + index);

		if (index < node.getElementTable().size()) {
			List<SkipGraphElement> returnList = new ArrayList<>();
			returnList.add(node.getElementTable().get(index));
			return returnList;
		} else if (node.getTableRangeEnd() != null) {
			index -= node.getElementTable().size();
			return node.getContacts().getNext().execute(this);
		} else {
			System.out.println("  ! index out of bounds");
			return null;
		}

	}
}
