package de.skipgraph;

import de.skipgraph.operations.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SkipGraphNode {


	private int minTableSize;
	private int maxTableSize;
	// TODO: list as set or sorted list (or sorted set?)
	private List<SkipGraphElement> elementTable;
	private BigDecimal tableRangeStart;
	private BigDecimal tableRangeEnd;
	private SkipGraphContacts contacts;

	public SkipGraphNode(int minTableSize, int maxTableSize) {
		this.minTableSize = minTableSize;
		this.maxTableSize = maxTableSize;
		this.elementTable = new ArrayList<>();
		this.tableRangeStart = BigDecimal.ZERO;
		this.tableRangeEnd = null; // initial null-value means: no upper limit (= infinity)
		this.contacts = new SkipGraphContacts(this, this);
	}

	public List<SkipGraphElement> getElementTable() {
		return elementTable;
	}

	public void setElementTable(List<SkipGraphElement> elementTable) {
		this.elementTable = elementTable;
	}

	public void setElementTable(List<SkipGraphElement> elementTable, BigDecimal start, BigDecimal end) {
		this.elementTable = elementTable;
		this.tableRangeStart = start;
		this.tableRangeEnd = end;
	}

	public void extendElementTableAtStart(List<SkipGraphElement> newList, BigDecimal start) {
		tableRangeStart = start;
		elementTable.addAll(0, newList);
		checkTableSize();
	}

	public void extendElementTableAtEnd(List<SkipGraphElement> newList, BigDecimal end) {
		tableRangeEnd = end;
		elementTable.addAll(newList);
		checkTableSize();
	}

	public BigDecimal getTableRangeEnd() {
		return tableRangeEnd;
	}

	public SkipGraphContacts getContacts() {
		return contacts;
	}

	public void setContacts(SkipGraphContacts contacts) {
		this.contacts = contacts;
	}

	public List<SkipGraphElement> execute(QueryOperation queryOperation) {
		return queryOperation.execute(this);
	}

	// TODO: replace with compareTo ?
	/**
	 * checks if a given value is below or equal to the minimum value of the element table
	 * @param value
	 * @return
	 */
	public boolean isBelowElementTablesMinimum(BigDecimal value) {
		//System.out.println(String.format("%s (value) <= %s (start) ?", value, tableRangeStart));
		boolean ret;
		if (value == null) {
			ret = !(tableRangeStart == null);
		}
		else {
			ret = !(tableRangeStart != null && tableRangeStart.compareTo(value) <= 0);
		}
		//System.out.println(ret);
		return ret;
	}

	/**
	 * checks if a given value is above the maxmimum value of the element table
	 * @param value
	 * @return
	 */
	public boolean isAboveElementTablesMaximum(BigDecimal value) {
		//System.out.println(String.format("%s (value) > %s (end) ?", value, tableRangeEnd));
		boolean ret;
		if (value == null) {
			ret = !(tableRangeEnd == null);
		}
		else {
			ret = tableRangeEnd != null && tableRangeEnd.compareTo(value) < 0;
		}
		//System.out.println(ret);
		return ret;
	}

	public void checkTableSize() {
		if (elementTable.size() < minTableSize) {
			System.out.println("  ! table too small -> leave?");
			leave();
		} else if (elementTable.size() > maxTableSize) {
			System.out.println("  ! table too big -> split");
			split();
		}
	}

	public int getNumberOfFreeSlots() {
		return maxTableSize-elementTable.size();
	}

	private void leave() {

		// check if successor has enough free space in table
		if (contacts.getNext() != this && contacts.getNext().getNumberOfFreeSlots() > elementTable.size()) {
			contacts.getNext().extendElementTableAtStart(elementTable, tableRangeStart);
			updatingContactsOnLeave();
		}
		// else check if predecessor has enough free space in table
		else if (contacts.getPrev() != this && contacts.getPrev().getNumberOfFreeSlots() > elementTable.size()) {
			contacts.getNext().extendElementTableAtEnd(elementTable, tableRangeEnd);
			updatingContactsOnLeave();
		}
		else {
			System.out.println("  ! hold your ground");
		}
	}

	private void updatingContactsOnLeave() {
		contacts.getNext().getContacts().setPrev(contacts.getPrev());
		contacts.getPrev().getContacts().setNext(contacts.getNext());
		System.out.println("  ! bye bye");
	}

	private void split() {
		Collections.sort(elementTable);
		//printTable();

		// split element table
		int roundup = 0;
		if (elementTable.size() % 2 > 0) roundup = 1;
		List<SkipGraphElement> tmp = elementTable.subList(elementTable.size()/2+roundup, elementTable.size());
		ArrayList<SkipGraphElement> newTable = new ArrayList<>(tmp);
		tmp.clear();
		BigDecimal newStart = elementTable.get(elementTable.size()-1).getValue();
		BigDecimal newEnd = tableRangeEnd;
		tableRangeEnd = newStart;
		//printTable();

		// check if successor can handle split elements
		if (newTable.size() < contacts.getNext().getNumberOfFreeSlots()) {
			contacts.getNext().extendElementTableAtStart(newTable, newStart);
			//contacts.getNext().printTable();
		}
		else {
			// create new node
			SkipGraphNode newNode = new SkipGraphNode(minTableSize, maxTableSize);
			newNode.setContacts(new SkipGraphContacts(this, contacts.getNext()));
			newNode.setElementTable(newTable, newStart, newEnd);
			//newNode.printTable();

			// update prev contact of next node
			contacts.getNext().getContacts().setPrev(newNode);
			// update next contact of this node
			contacts.setNext(newNode);
		}

	}

	public void printTable() {
		System.out.println(String.format("### printing table (start:%s, end:%s, size:%d, min-size:%d, max-size:%d) ###",
				tableRangeStart, tableRangeEnd==null?"inf":tableRangeEnd, elementTable.size(), minTableSize, maxTableSize));
		Collections.sort(elementTable);
		for (int i=0; i < elementTable.size(); i++) {
			String index = String.format("%03d ", i);
			System.out.println(index + elementTable.get(i));
		}
	}

}
