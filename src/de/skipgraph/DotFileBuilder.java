package de.skipgraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DotFileBuilder {

	private final Node head;
	private String code = null;
	private SortedMap<String, LinkedList<ContactTupel>> graphPerLevelAndPrefixMap = new TreeMap<>();

	public DotFileBuilder(Node head) {
		this.head = head;
	}


	public void build() {

		// format outer frame
		StringBuilder sbMain = new StringBuilder();
		sbMain.append("strict\n" +
				"digraph SkipGraph {\n\n");

		// format vertical subgraphs with "pseudo"-edges
		StringBuilder sbVertical = new StringBuilder();
		sbVertical.append("\t# vertical\n" +
				"\tedge [dir=none style=dashed]\n");

		// cluster for element table content
		StringBuilder sbContent = new StringBuilder();
		sbContent.append("\n\t# horizontal\n" +
				"\tedge [dir=forward, style=solid]\n" +
				"\tsubgraph cluster_content {\n" +
				"\t\trank = same\n");

		// format horizontal subgraphs with "real"-edges
		StringBuilder sbHorizontal = new StringBuilder();

		Node next = head;
		do {
			sbVertical.append(formatVerticalForNode(next));
			sbContent.append(formatElementTableForNode(next));
			addToLevelMap(next);
			next = next.getContactTable().getNextNode();
			if (next == null) {
				System.out.println("link to next node missing - aborting...");
				break;
			}
		} while (next != head);

		sbHorizontal.append(generateHorizontalSubgraphsFromLevelMap(false));
		sbContent.append("\t}\n\n");
		sbMain.append(sbVertical).append(sbContent).append(sbHorizontal);
		sbMain.append("\n}\n");
		this.code = sbMain.toString();
	}


	private String formatVerticalForNode(Node node) {
		StringBuilder sb = new StringBuilder();
		sb.append("\tsubgraph {\n");
		sb.append(String.format("\t\t\"%s\"", node));
		for (int i=0; i<node.getContactTable().size(); i++) {
			sb.append(String.format(" -> \"%s.%d\"", node, i));
		}
		sb.append("\n\t\t}\n");

		return sb.toString();
	}


	private String formatElementTableForNode(Node node) {
		ElementTable elementTable = node.getElementTable();
		StringBuilder sb = new StringBuilder();
		sb.append("\t\t\"" + node + "\" [shape=box, label=\n");
		sb.append("\t\t\t<\n");
		sb.append("\t\t\t\t<B>ID:</B> " + node + "<BR ALIGN=\"LEFT\"/>\n");
		sb.append(String.format("\t\t\t\tresponsible for <B>[%s, %s)</B><BR ALIGN=\"LEFT\"/>\n",
					elementTable.getRangeStart(), elementTable.getRangeEnd() == null ? "inf" : elementTable.getRangeEnd()));
		elementTable.sort();
		for (int i = 0; i < elementTable.size(); i++) {
			sb.append(String.format("\t\t\t\t%03d %s<BR ALIGN=\"LEFT\"/>\n", i, elementTable.get(i)));
		}
		sb.append(String.format("\t\t\t<B>size:</B>%d, <B>min-size:</B>%d, <B>max-size:</B>%d<BR ALIGN=\"LEFT\"/>",
				elementTable.size(), elementTable.getMinSize(), elementTable.getMaxSize()));
		sb.append("\t\t\t>]\n");

		return sb.toString();
	}


	private void addToLevelMap(Node next) {
		ContactTable contactTable = next.getContactTable();
		String key = "";
		for (int i=0; i<contactTable.size(); i++) {
			key += Integer.toString(contactTable.get(i).getPrefix());
			if (graphPerLevelAndPrefixMap.containsKey(key)) {
				graphPerLevelAndPrefixMap.get(key).add(new ContactTupel(next.toString(), i, contactTable.get(i)));
			}
			else {
				LinkedList<ContactTupel> levelList = new LinkedList<>();
				levelList.add(new ContactTupel(next.toString(), i, contactTable.get(i)));
				graphPerLevelAndPrefixMap.put(key, levelList);
			}
		}
	}

	private StringBuilder generateHorizontalSubgraphsFromLevelMap(boolean cluster) {
		if (graphPerLevelAndPrefixMap.isEmpty()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		String clusterStr = cluster ? "cluster_" : "";
		int i = 0;

		for (Map.Entry<String, LinkedList<ContactTupel>> entry : graphPerLevelAndPrefixMap.entrySet()) {
			sb.append(String.format("\tedge [color=%s]\n", color(i++)));
			sb.append(String.format("\tsubgraph %s%s {\n", clusterStr, entry.getKey()));
			sb.append("\t\trank = same\n");
			sb.append(String.format("\t\tlabel = \"Level %d%s\"\n", entry.getValue().getFirst().getIndex(),
					entry.getKey().length() > 1 ? " ("+entry.getKey().substring(1)+")" : ""));
			for (ContactTupel contactTupel : entry.getValue()) {
				sb.append("\t\tsubgraph {\n");
				sb.append(String.format("\t\t\t\"%s.%d\" -> \"%s.%d\"\n",
						contactTupel.getId(), contactTupel.getIndex(),
						contactTupel.getLevel().getNextContact().getNode(), contactTupel.getIndex()));
				sb.append(String.format("\t\t\t\"%s.%d\" -> \"%s.%d\"\n",
						contactTupel.getId(), contactTupel.getIndex(),
						contactTupel.getLevel().getPrevContact().getNode(), contactTupel.getIndex()));
				sb.append("\t\t}\n");
			}
			sb.append("\t}\n\n");
		}

		return sb;
	}


	private String color(int i) {
		switch (i % 8) {
			case 0:
				return "blue";
			case 1:
				return "red";
			case 2:
				return "green";
			case 3:
				return "orange";
			case 4:
				return "cyan";
			case 5:
				return "violet";
			case 6:
				return "grey";
			case 7:
				return "yellow";
		}
		return "black";
	}


	public String toString() {
		if (code == null) {
			build();
		}
		return code;
	}


	public void print() {
		System.out.println(this.toString());
	}


	public void write(String filename) {
		try {

			File file = new File("graph/"+filename);

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(this.toString());
			bw.close();
			plot(file.getAbsolutePath());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void plot(String filename) {
		String outfile = filename.substring(0, filename.lastIndexOf(".")) + ".png";
		try {
			Runtime r = Runtime.getRuntime();
			r.exec(String.format("dot -Tpng %s -o %s", filename, outfile));
			//r.exec("xdg-open " + outfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
