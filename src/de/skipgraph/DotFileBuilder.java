package de.skipgraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DotFileBuilder {

	private boolean plotAutomatically = false;
	private boolean cluster = false;
	private boolean strict = true;
	private boolean concentrate = true;
	private final Node head;
	private String code = null;
	private SortedMap<String, LinkedList<ContactTupel>> graphPerLevelAndPrefixMap = new TreeMap<>();
	private static File dir;
	private static int fileCounter = 1;

	public DotFileBuilder(Node head) {
		this.head = head;
	}

	public DotFileBuilder(Node head, Boolean plotAutomatically, Boolean cluster) {
		this.head = head;
		this.plotAutomatically = plotAutomatically;
		this.cluster = cluster;
	}

	public static int getFileCounter() {
		return fileCounter++;
	}

	public void build() {

		// format outer frame
		StringBuilder sbMain = new StringBuilder();
		sbMain.append(String.format("%sstrict\n digraph SkipGraph {\n\n",
				strict ? "" : "#"));
		sbMain.append("\tranksep=1.0\n" +
				"\tconcentrate=" + concentrate + "\n\n");

		// format vertical subgraphs with "pseudo"-edges
		int clusterInt = cluster ? 1 : 50;
		StringBuilder sbVertical = new StringBuilder();
		sbVertical.append("\t# vertical\n" +
				"\tedge [dir=none style=dashed, weight=" + clusterInt + "]\n");

		// cluster for element table content
		StringBuilder sbContent = new StringBuilder();
		sbContent.append("\n\t# horizontal\n" +
				"\tedge [dir=forward, style=solid, weight=1]\n" +
				"\tsubgraph cluster_content {\n" +
				"\t\t#rank=same\n");

		// format horizontal subgraphs with "real"-edges
		StringBuilder sbHorizontal = new StringBuilder();

		Node next = head;
		do {
			sbVertical.append(formatVerticalForNode(next));
			sbContent.append("\t\t\"" + next + "\" [shape=box, label=\n");
			sbContent.append("\t\t\t<\n");
			sbContent.append(formatElementTableForNode(next));
			sbContent.append(formatContactTableForNode(next));
			sbContent.append("\t\t\t>]\n");
			addToLevelMap(next);
			next = next.getContactTable().getNextNode();
			if (next == null) {
				System.out.println("link to next node missing - aborting...");
				break;
			}
		} while (next != head);

		sbHorizontal.append(generateHorizontalSubgraphsFromLevelMap());
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
		sb.append("\t\t\t\t<B>ID:</B> " + node + "<BR ALIGN=\"LEFT\"/>\n");
		sb.append(String.format("\t\t\t\tresponsible for <B>[%s, %s)</B><BR ALIGN=\"LEFT\"/>\n",
					elementTable.getRangeStart(), elementTable.getRangeEnd() == null ? "inf" : elementTable.getRangeEnd()));
		elementTable.sort();
		for (int i = 0; i < elementTable.size(); i++) {
			sb.append(String.format("\t\t\t\t%03d %s<BR ALIGN=\"LEFT\"/>\n", i, elementTable.get(i)));
		}
		sb.append(String.format("\t\t\t<B>size:</B>%d, <B>min-size:</B>%d, <B>max-size:</B>%d<BR ALIGN=\"LEFT\"/>\n",
				elementTable.size(), elementTable.getMinSize(), elementTable.getMaxSize()));

		return sb.toString();
	}


	private String formatContactTableForNode(Node node) {
		ContactTable contactTable = node.getContactTable();
		StringBuilder sb = new StringBuilder();
		sb.append((String.format("\t\t\t\t<B>contacts:</B> (<B>size:</B>%d)<BR ALIGN=\"LEFT\"/>\n", contactTable.size())));
		for (int i = 0; i < contactTable.size(); i++) {
			sb.append(String.format("\t\t\t\t%d: %s<BR ALIGN=\"LEFT\"/>\n", i, contactTable.get(i)));
		}
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

	private StringBuilder generateHorizontalSubgraphsFromLevelMap() {
		if (graphPerLevelAndPrefixMap.isEmpty()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		String clusterStr = cluster ? "cluster_" : "";

		for (Map.Entry<String, LinkedList<ContactTupel>> entry : graphPerLevelAndPrefixMap.entrySet()) {
			sb.append(String.format("\tedge [color=%s]\n", colorByInt(keyToInt(entry.getKey()))));
			sb.append(String.format("\tsubgraph %s%s {\n", clusterStr, entry.getKey()));
			sb.append("\t\trank=same\n");
			sb.append(String.format("\t\tlabel=\"Level %d (%s)\"\n", entry.getValue().getFirst().getIndex(),
					entry.getKey()));
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


	private int keyToInt(String key) {
		int keyInt = 0;
		for (int i=0; i<key.length(); i++) {
			keyInt = keyInt << 1;
			switch (key.charAt(i)) {
				case '0':
					break;
				case '1':
					keyInt++;
					break;
				default:
					return -1;
			}
		}
		return keyInt;
	}


	private String colorByInt(int i) {
		switch (i % 23) {
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
			case 8:
				return "aquamarine";
			case 9:
				return "darkslategray";
			case 10:
				return "brown";
			case 11:
				return "burlywood";
			case 12:
				return "cadetblue";
			case 13:
				return "chartreuse";
			case 14:
				return "chocolate";
			case 15:
				return "cornflowerblue";
			case 16:
				return "cornsilk4";
			case 17:
				return "crimson";
			case 18:
				return "darkgoldenrod";
			case 19:
				return "darkolivegreen";
			case 20:
				return "deeppink";
			case 21:
				return "indigo";
			case 22:
				return "navy";
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



	public void writeFile(String filename) {
		try {

			if (dir == null) {
				makeDir();
			}
			File file = new File(dir, filename);

			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(this.toString());
			bw.close();
			if (plotAutomatically) {
				plot(file.getAbsolutePath());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void makeDir() {
		Date date = new Date( );
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd_hh-mm-ss-SSS");
		String formatedDate = simpleDateFormat.format(date);
		dir = new File("graph/"+formatedDate+"/");

		if (!dir.exists()) {
			dir.mkdir();
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
