package de.skipgraph;

import de.skipgraph.operations.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SkipGraphNode {


	private int minTableSize;
	private int maxTableSize;
	// TODO: list as set or sorted list (or sorted set?)
	private List<SkipGraphElement> elementTable;
	private BigDecimal tableRangeStart;
	private BigDecimal tableRangeEnd;
	private SkipGraphContacts contactsTable;

	public SkipGraphNode(int minTableSize, int maxTableSize) {
		this.minTableSize = minTableSize;
		this.maxTableSize = maxTableSize;
		this.elementTable = new ArrayList<>();
		this.tableRangeStart = BigDecimal.ZERO;
		this.tableRangeEnd = null; // initial null-value means: no upper limit (= infinity)
		this.contactsTable = new SkipGraphContacts(this, this);
	}

	public List<SkipGraphElement> getElementTable() {
		return elementTable;
	}

	public void setElementTable(List<SkipGraphElement> elementTable) {
		this.elementTable = elementTable;
	}

	public BigDecimal getTableRangeEnd() {
		return tableRangeEnd;
	}

	public SkipGraphContacts getContactsTable() {
		return contactsTable;
	}

	public void setContactsTable(SkipGraphContacts contactsTable) {
		this.contactsTable = contactsTable;
	}

	public List<SkipGraphElement> execute(QueryOperation queryOperation) {
		return queryOperation.execute(this);
	}

	// TODO: intervall nach unten offen
	/**
	 * checks if a given value is below the minimum value of the element table
	 * @param value
	 * @return
	 */
	public boolean isBelowElementTablesMinimum(BigDecimal value) {
		//
		if (value == null) {
			return !(tableRangeStart == null);
		}
		else {
			return !(tableRangeStart != null && tableRangeStart.compareTo(value) <= 0);
		}
	}

	/**
	 * checks if a given value is above the maxmimum value of the element table
	 * @param value
	 * @return
	 */
	public boolean isAboveElementTablesMaximum(BigDecimal value) {
		if (value == null) {
			return !(tableRangeEnd == null);
		}
		else {
			return (tableRangeEnd != null && tableRangeEnd.compareTo(value) >= 0);
		}
	}

	public void checkTableSize() {
		if (elementTable.size() < minTableSize) {
			System.out.println("  ! table too small -> leave?");
		} else if (elementTable.size() > maxTableSize) {
			System.out.println("  ! table too big -> split");
			//split();
		}
	}

	public int getNumberOfFreeSlots() {
		return maxTableSize-elementTable.size();
	}

	private void leave() {

	}

	private void split() {
		Collections.sort(elementTable);
		printTable();

		// split element table
		List<SkipGraphElement> tmp=elementTable.subList(elementTable.size()/2, elementTable.size());
		ArrayList newList=new ArrayList<>(tmp);
		tmp.clear();
		printTable();

		// create new node
		SkipGraphNode newNode = new SkipGraphNode(minTableSize, maxTableSize);
		newNode.setElementTable(newList);
		newNode.printTable();

		// update contacts
		newNode.getContactsTable().setPrev(this);
		newNode.getContactsTable().setNext(contactsTable.getNext());
		contactsTable.getNext().getContactsTable().setPrev(newNode);
		contactsTable.setNext(newNode);
	}

	public void printTable() {
		System.out.println("### printing table (size " + elementTable.size() + ") ###");
		Collections.sort(elementTable);
		for (int i=0; i < elementTable.size(); i++) {
			String index = String.format("%03d ", i);
			System.out.println(index + elementTable.get(i));
		}
	}

}
