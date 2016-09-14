package de.skipgraph.operations;

import de.skipgraph.Element;
import de.skipgraph.ElementTable;
import de.skipgraph.Node;

import java.math.BigDecimal;
import java.util.ConcurrentModificationException;
import java.util.List;

public class DeleteOperation extends ModifyElementsOperation {

	public DeleteOperation(Element element) {
		super(element);
	}

	@Override
	public List<Element> execute(Node node) {

		// using local variables for better readability
		BigDecimal value = this.getElement().getValue();
		String capacity = this.getElement().getCapacity();
		int contactIp = this.getElement().getContactIp();
		int contactPort = this.getElement().getContactPort();
		ElementTable elementTable = node.getElementTable();

		// if node is not responsible for value forward query to prev or next node on highest possible level
		if (elementTable.isBelowElementTablesMinimum(value)) {
			System.out.println("  ! value too small -> prev");
			node.getContactTable().getPrevNodeForValue(value).execute(this);
		}
		else if (elementTable.isAboveElementTablesMaximum(value)) {
			System.out.println("  ! value too big -> next");
			node.getContactTable().getNextNodeForValue(value).execute(this);
		}
		// node is responsible for value.
		else {
			try {
				boolean success = false;
				Element element;
				// check if element exists. also deletes doubles
				// TODO: avoid doubles on input
				for (int i = 0; i < elementTable.size(); i++) {
					element = elementTable.get(i);
					if (element.getContactIp() == contactIp &&
							element.getContactPort() == contactPort &&
							element.getCapacity().equals(capacity) &&
							element.getValue().equals(value)) {
						elementTable.remove(element);
						i--;
						success = true;
					}
				}
				if (success) {
					System.out.println("deleting: " + this.getElement());
					elementTable.checkMinTableSize(node);
				}
				else {
					System.out.println("not found: " + this.getElement());
				}
				//node.printElementTable();
			} catch (ConcurrentModificationException e) {
				System.out.println("ConcurrentModificationException");
				e.getCause();
			}
		}
		return null;
	}

}