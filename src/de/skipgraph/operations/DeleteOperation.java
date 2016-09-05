package de.skipgraph.operations;

import de.skipgraph.SkipGraphElement;
import de.skipgraph.SkipGraphNode;

import java.math.BigDecimal;
import java.util.ConcurrentModificationException;
import java.util.List;

public class DeleteOperation extends UpdateOperation {

	public DeleteOperation(SkipGraphElement element) {
		super(element);
	}

	@Override
	public List<SkipGraphElement> execute(SkipGraphNode node) {
		System.out.println("trying to delete " + this.getElement().toString());
		BigDecimal value = this.getElement().getValue();
		String capacity = this.getElement().getCapacity();
		int contactIp = this.getElement().getContactIp();
		int contactPort = this.getElement().getContactPort();
		if (node.isBelowElementTablesMinimum(value)) {
			System.out.println("value too small");
		} else if (node.isAboveElementTablesMaximum(value)) {
			System.out.println("value too big");
		} else {
			try {
				boolean success = false;
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
					System.out.println("element deleted");
					node.checkTableSize();
				}
				else {
					System.out.println("element not found");
				}
				node.printTable();
			} catch (ConcurrentModificationException e) {
				System.out.println("ConcurrentModificationException");
				e.getCause();
			}
		}
		return null;
	}
}
