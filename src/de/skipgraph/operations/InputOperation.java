package de.skipgraph.operations;

import de.skipgraph.Element;
import de.skipgraph.Node;

import java.math.BigDecimal;
import java.util.List;

public class InputOperation extends ModifyElementsOperation {

	public InputOperation(Element element) {
		super(element);
	}

	@Override
	public List<Element> execute(Node node) {

		//System.out.println("trying to add: " + this.getElement());

		// using local variables for better readability
		BigDecimal value = this.getElement().getValue();

		// if node is not responsible for value forward query to prev or next node on highest possible level
		if (node.getElementTable().isBelowElementTablesMinimum(value)) {
			System.out.println("  ! value too small -> prev");
			node.getContactTable().getPrevNodeForValue(value).execute(this);
		}
		else if (node.getElementTable().isAboveElementTablesMaximum(value)) {
			System.out.println("  ! value too big -> next");
			node.getContactTable().getNextNodeForValue(value).execute(this);
		}
		// node is responsible for value.
		else {
			node.getElementTable().add(this.getElement());
			System.out.println("adding: " + this.getElement());
			// TODO: sortieren?
			//node.printElementTable();
			node.getElementTable().checkMaxTableSize(node);
		}
		return null;
	}

}
