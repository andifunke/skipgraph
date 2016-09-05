package de.skipgraph.operations;

import de.skipgraph.SkipGraphElement;
import de.skipgraph.SkipGraphNode;

import java.math.BigDecimal;

public class InputOperation extends UpdateOperation {

	public InputOperation(SkipGraphElement element) {
		super(element);
	}

	public void execute(SkipGraphNode node) {
		System.out.println("trying to add " + this.getElement().toString());
		BigDecimal value = this.getElement().getValue();
		int rc = node.rangeCheck(value);
		if (rc == 0) {
			node.getElementTable().add(this.getElement());
			System.out.println("element added");
			node.printTable();
			node.checkTableSize();
		} else if (rc < 0) {
			System.out.println("value too small");
		} else {
			System.out.println("value too big");
		}
	}

}
