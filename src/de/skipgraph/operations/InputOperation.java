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
		System.out.println("trying to add " + this.getElement().toString());
		BigDecimal value = this.getElement().getValue();
		if (node.isBelowElementTablesMinimum(value)) {
			System.out.println("value too small");
		} else if (node.isAboveElementTablesMaximum(value)) {
			System.out.println("value too big");
		} else {
			node.getElementTable().add(this.getElement());
			System.out.println("element added");
			node.printTable();
			node.checkTableSize();
		}
		return null;
	}

}
