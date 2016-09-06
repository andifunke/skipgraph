package de.skipgraph.operations;

import de.skipgraph.SkipGraphElement;
import de.skipgraph.SkipGraphNode;

import java.math.BigDecimal;
import java.util.List;

public class InputOperation extends UpdateOperation {

	public InputOperation(SkipGraphElement element) {
		super(element);
	}

	@Override
	public List<SkipGraphElement> execute(SkipGraphNode node) {
		BigDecimal value = this.getElement().getValue();
		if (node.isBelowElementTablesMinimum(value)) {
			System.out.println("  ! value too small");
		} else if (node.isAboveElementTablesMaximum(value)) {
			System.out.println("  ! value too big");
		} else {
			node.getElementTable().add(this.getElement());
			System.out.println("adding: " + this.getElement());
			// TODO: sortieren?
			//node.printTable();
			node.checkTableSize();
		}
		return null;
	}

}
