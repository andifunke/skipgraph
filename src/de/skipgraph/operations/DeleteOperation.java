package de.skipgraph.operations;

import de.skipgraph.SkipGraphElement;
import de.skipgraph.SkipGraphNode;

import java.math.BigDecimal;
import java.util.ConcurrentModificationException;
import java.util.List;

public class DeleteOperation extends ModifyContentOperation {

	public DeleteOperation(SkipGraphElement element) {
		super(element);
	}

	@Override
	public List<SkipGraphElement> execute(SkipGraphNode node) {

		// using local variables for better readability
		BigDecimal value = this.getElement().getValue();
		String capacity = this.getElement().getCapacity();
		int contactIp = this.getElement().getContactIp();
		int contactPort = this.getElement().getContactPort();

		// if node is not responsible for value forward query to prev or next node on highest possible level
		if (node.isBelowElementTablesMinimum(value)) {
			System.out.println("  ! value too small -> prev");
			node.getContactTable().getPrevNodeForValue(value).execute(this);
		}
		else if (node.isAboveElementTablesMaximum(value)) {
			System.out.println("  ! value too big -> next");
			node.getContactTable().getNextNodeForValue(value).execute(this);
		}
		// node is responsible for value.
		else {
			try {
				boolean success = false;
				// check if element exists. also deletes doubles
				// TODO: avoid doubles on input
				for (int i = 0; i < node.getElementTable().size(); i++) {
					if (node.getElementTable().get(i).getContactIp() == contactIp &&
							node.getElementTable().get(i).getContactPort() == contactPort &&
							node.getElementTable().get(i).getCapacity().equals(capacity) &&
							node.getElementTable().get(i).getValue().equals(value)) {
						node.getElementTable().remove(node.getElementTable().get(i));
						i--;
						success = true;
					}
				}
				if (success) {
					System.out.println("deleting: " + this.getElement());
					node.checkMinTableSize();
				}
				else {
					System.out.println("not found: " + this.getElement());
				}
				//node.printTable();
			} catch (ConcurrentModificationException e) {
				System.out.println("ConcurrentModificationException");
				e.getCause();
			}
		}
		return null;
	}

}