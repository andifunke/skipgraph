package de.skipgraph;

import de.skipgraph.operations.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SkipGraphNode {

	private int minTableSize;
	private int maxTableSize;
	private List<SkipGraphElement> elementTable;
	private BigDecimal tableRangeStart;
	private BigDecimal tableRangeEnd;
	private List<SkipGraphContacts> contactsTable;

	public SkipGraphNode(int minTableSize, int maxTableSize) {
		this.minTableSize = minTableSize;
		this.maxTableSize = maxTableSize;
		this.elementTable = new ArrayList<>();
		this.tableRangeStart = BigDecimal.ZERO;
		this.tableRangeEnd = null;
		this.contactsTable = new LinkedList<>();
	}



	public List<SkipGraphElement> execute(QueryOperation queryOperation) {
		String queryType = queryOperation.getClass().getSimpleName();
		switch (queryType) {
			case "DeleteOperation":
				remove(queryOperation);
				return null;
			case "InputOperation":
				add(queryOperation);
				return null;
			case "SearchOperation":
				return collect(queryOperation);
			case "GetOperation":
				return get(queryOperation);
		}
		return null;
	}

	private void add(QueryOperation queryOperation) {
		InputOperation inputOperation = (InputOperation)queryOperation;
		SkipGraphElement element = inputOperation.getElement();
		BigDecimal value = element.getValue();
		if (tableRangeStart.compareTo(value) <= 0 && tableRangeEnd.compareTo(value) >= 0) {
			elementTable.add(element);
			checkTableSize();
		}
	}

	private void remove(QueryOperation queryOperation) {
		DeleteOperation deleteOperation = (DeleteOperation)queryOperation;
		SkipGraphElement element = deleteOperation.getElement();
		BigDecimal value = element.getValue();
		String capacity = element.getCapacity();
		int contactIp = element.getContactIp();
		int contactPort = element.getContactPort();
		if (tableRangeStart.compareTo(value) <= 0 && tableRangeEnd.compareTo(value) >= 0) {
			for (SkipGraphElement tableElement :
					elementTable) {
				if (tableElement.getContactIp() == contactIp &&
						tableElement.getContactPort() == contactPort &&
						tableElement.getCapacity().equals(capacity) &&
						tableElement.getValue().equals(value)) {
					elementTable.remove(tableElement);
				}
			}
			checkTableSize();
		}
	}

	private void findElement(SkipGraphElement element) {

	}

	private List<SkipGraphElement> collect(QueryOperation queryOperation) {
		SearchOperation searchOperation = (SearchOperation)queryOperation;
		return new ArrayList<>();
	}

	private List<SkipGraphElement> get(QueryOperation queryOperation) {
		return new ArrayList<>();
	}

	private void checkTableSize() {

	}

	private void leave() {

	}

	private void split() {

	}




}
