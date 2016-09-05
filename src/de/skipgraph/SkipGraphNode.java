package de.skipgraph;

import de.skipgraph.operations.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

public class SkipGraphNode {

	// TODO: move methods to operation classes

	private int minTableSize;
	private int maxTableSize;
	// TODO: list as set or sorted list (or sorted set?)
	private List<SkipGraphElement> elementTable;
	private BigDecimal tableRangeStart;
	private BigDecimal tableRangeEnd;
	private List<SkipGraphContacts> contactsTable;

	public SkipGraphNode(int minTableSize, int maxTableSize) {
		this.minTableSize = minTableSize;
		this.maxTableSize = maxTableSize;
		this.elementTable = new ArrayList<>();
		this.tableRangeStart = BigDecimal.ZERO;
		this.tableRangeEnd = null; // initial null-value means: no upper limit (= infinity)
		this.contactsTable = new LinkedList<>();
	}

	public List<SkipGraphElement> getElementTable() {
		return elementTable;
	}

	public List<SkipGraphElement> execute(QueryOperation queryOperation) {
		String queryType = queryOperation.getClass().getSimpleName();
		switch (queryType) {
			case "DeleteOperation":
				remove(queryOperation);
				return null;
			case "InputOperation":
				queryOperation.execute(this);
				return null;
			case "SearchOperation":
				return collect(queryOperation);
			case "GetOperation":
				return get(queryOperation);
		}
		return null;
	}

	/*
	private void add(QueryOperation queryOperation) {
		InputOperation inputOperation = (InputOperation)queryOperation;
		SkipGraphElement element = inputOperation.getElement();
		System.out.println("trying to add " + element.toString());
		BigDecimal value = element.getValue();
		int rc = rangeCheck(value);
		if (rc == 0) {
			elementTable.add(element);
			System.out.println("element added");
			printTable();
			checkTableSize();
		} else if (rc < 0) {
			System.out.println("value too small");
		} else {
			System.out.println("value too big");
		}
	}
	*/

	private void remove(QueryOperation queryOperation) {
		DeleteOperation deleteOperation = (DeleteOperation)queryOperation;
		SkipGraphElement element = deleteOperation.getElement();
		System.out.println("trying to delete " + element.toString());
		BigDecimal value = element.getValue();
		String capacity = element.getCapacity();
		int contactIp = element.getContactIp();
		int contactPort = element.getContactPort();
		int rc = rangeCheck(value);
		if (rc == 0) {
			try {
				boolean success = false;
				for (int i=0; i < elementTable.size(); i++) {
					if (elementTable.get(i).getContactIp() == contactIp &&
							elementTable.get(i).getContactPort() == contactPort &&
							elementTable.get(i).getCapacity().equals(capacity) &&
							elementTable.get(i).getValue().equals(value)) {
						elementTable.remove(elementTable.get(i));
						i--;
						success = true;
					}
				}
				if (success) {
					System.out.println("element deleted");
					checkTableSize();
				} else {
					System.out.println("element not found");
				}
				printTable();
			} catch (ConcurrentModificationException e) {
				System.out.println("ConcurrentModificationException");
				e.getCause();
			}
		} else if (rc < 0) {
			System.out.println("value too small");
		} else {
			System.out.println("value too big");
		}
	}

	/**
	 * checks if a given value is within the range of the element table
	 * @param value
	 * @return   return -1 if the value is below the table range, 0 if inside range, 1 if above range
	 */
	public int rangeCheck(BigDecimal value) {
		//
		int returnValue = -1;
		if (tableRangeStart.compareTo(value) <= 0) {
			returnValue++;
			if (tableRangeEnd != null && tableRangeEnd.compareTo(value) >= 0) {
				returnValue++;
			}
		}
		return returnValue;
	}

	private List<SkipGraphElement> get(QueryOperation queryOperation) {

		return new ArrayList<>();
	}

	private void findElement(SkipGraphElement element) {

	}

	private List<SkipGraphElement> collect(QueryOperation queryOperation) {
		SearchOperation searchOperation = (SearchOperation)queryOperation;
		return new ArrayList<>();
	}

	public void checkTableSize() {
		if (elementTable.size() < minTableSize) {
			System.out.println("table too small -- leave");
		} else if (elementTable.size() > maxTableSize) {
			System.out.println("table too big -- split");
		}
	}

	private void leave() {

	}

	private void split() {

	}

	public void printTable() {
		for (int i=0; i < elementTable.size(); i++) {
			String index = String.format("%03d -- ", i+1);
			System.out.println(index + elementTable.get(i).toString());
		}
	}

}
