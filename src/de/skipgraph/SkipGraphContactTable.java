package de.skipgraph;

import java.math.BigDecimal;
import java.util.*;

public class SkipGraphContactTable {

	private LinkedList<SkipGraphContactLevel> contactTable = new LinkedList<>();

	public int size() {
		return contactTable.size();
	}

	public void addLevel(SkipGraphContactLevel level) {
		contactTable.add(level);
	}

	public SkipGraphContactLevel getLevel(int i) {
		return contactTable.get(i);
	}

	public SkipGraphNode getPrevNode() {
		return getPrevNodeOnLevel(0);
	}

	public SkipGraphNode getNextNode() {
		return getNextNodeOnLevel(0);
	}

	public SkipGraphNode getNextNodeOnLevel(int i) {
		return getLevel(i).getNext().getNode();
	}

	public SkipGraphNode getPrevNodeOnLevel(int i) {
		return getLevel(i).getPrev().getNode();
	}

	/**
	 * this method finds the NextNode on the highest possible level which range start is below or equal the given value
	 * @param value	the value a query starts with
	 * @return			returns the highest NextNode on the highest possible level that doen't exceed the value
	 */
	public SkipGraphNode getNextNodeForValue(BigDecimal value) {
		// TODO: durch Iterator ersetzen
		for (int i=size()-1; i>-1; i--) {
			if (getLevel(i).getNext().getRangeStart().compareTo(value) > 0) continue;
			return getNextNodeOnLevel(i);
		}
		return getNextNodeOnLevel(0);
	}

	/**
	 * this method finds the PrevNode on the highest possible level which range start is below or equal the given value
	 * @param value	the value a query starts with
	 * @return			returns the highest PrevNode on the highest possible level that doen't exceed the value
	 */
	public SkipGraphNode getPrevNodeForValue(BigDecimal value) {
		// TODO: durch Iterator ersetzen
		for (int i=size()-1; i>-1; i--) {
			if (getLevel(i).getPrev().getRangeStart().compareTo(value) > 0) continue;
			return getPrevNodeOnLevel(i);
		}
		return getPrevNodeOnLevel(0);
	}

	public SkipGraphContactLevel getHighestLevel() {
		return contactTable.getLast();
	}

}
