import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {
	
	public static final int NO_COLOR = -1;
	public static Random random = new Random();

	public static int getHighestDegreeVertex(int[] degreeArray) {
		int highestDegVertexIndex = 0;

		for (int i = 0; i < degreeArray.length; i++) {
			if (degreeArray[i] > degreeArray[highestDegVertexIndex])
				highestDegVertexIndex = i;
		}

		return highestDegVertexIndex;
	}

	public static int[] calculateVerticesDegrees(int[][] adjacency) {
		int[] verticesDegrees = new int[adjacency.length];

		for (int y = 0; y < adjacency.length; y++) {
			verticesDegrees[y] = adjacency[y].length;
		}

		return verticesDegrees;
	}

	public static ArrayList<Integer> getIndexesWithMaxValue(int[] arr) {
		int maxValue = -10000;
		ArrayList<Integer> bestIndexes = new ArrayList<Integer>();
		for (int h = 0; h < arr.length; h++) {
			if (arr[h] > maxValue) {
				bestIndexes = new ArrayList<Integer>();
				bestIndexes.add(h);
				maxValue = arr[h];
			} else if (arr[h] == maxValue) {
				bestIndexes.add(h);
			}
		}
		return bestIndexes;

	}

	public static int selectColor(int index, ArrayList<Set<Integer>> colorsUsedArround) {

		Set<Integer> colorsAr = colorsUsedArround.get(index);
		for (int h = 0; true; h++) {
			if (!colorsAr.contains(h)) {
				return h;
			}
		}
	}

	public static void addColor(int index, int color, int[] coloring, ArrayList<Set<Integer>> colorsUsedArround,
			int[][] adjacency) {
		assert coloring[index] == NO_COLOR;
		coloring[index] = color;
		for (int y = 0; y < adjacency[index].length; y++) {
			colorsUsedArround.get(adjacency[index][y]).add(color);
		}

	}

	public static int selectNextvertice(ArrayList<Set<Integer>> colorsUsedArround, int[][] adjacency, int[] degrees,
			int[] coloring) {
		int[] saturationDegrees = new int[adjacency.length];
		for (int h = 0; h < adjacency.length; h++) {
			saturationDegrees[h] = colorsUsedArround.get(h).size();
			if (coloring[h] != NO_COLOR) {
				saturationDegrees[h] = -1; // deja colorié, pour eviter de re-selectionner
			}
		}

		ArrayList<Integer> bestIndices = getIndexesWithMaxValue(saturationDegrees);

		int[] _degrees = new int[bestIndices.size()];
		for (int g = 0; g < bestIndices.size(); g++) {
			_degrees[g] = degrees[bestIndices.get(g)];
		}
		ArrayList<Integer> bestIndices2 = getIndexesWithMaxValue(_degrees);

		return bestIndices.get(bestIndices2.get(random.nextInt(bestIndices2.size())));
	}

	public static int[] dsatur(int[][] adjacency, int nNodes) {

		int[] degrees = calculateVerticesDegrees(adjacency);

		ArrayList<Set<Integer>> colorsUsedArround = new ArrayList<Set<Integer>>(nNodes);
		int[] coloring = new int[nNodes];

		for (int i = 0; i < nNodes; i++) {
			coloring[i] = NO_COLOR;
			// notColored.add(i);
			colorsUsedArround.add(i, new HashSet<Integer>());

		}

		int highestDegreeVertex = getHighestDegreeVertex(degrees);
		addColor(highestDegreeVertex, 0, coloring, colorsUsedArround, adjacency);

		for (int g = 0; g < nNodes; g++) {
			
			int vertice = selectNextvertice(colorsUsedArround, adjacency, degrees, coloring);

			int color = selectColor(vertice, colorsUsedArround);

			addColor(vertice, color, coloring, colorsUsedArround, adjacency);
		}
		return coloring;
	}

	static class Point {
		int x;
		int y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public boolean equals(Point point) {
			return point.x == this.x && point.y == this.y;
		}

	};

	static boolean doIntersect(Point p1, Point q1, Point p2, Point q2) {
		if (p1.equals(p2) || p1.equals(q2) || q1.equals(p2) || q1.equals(q2)) {
			return false;
		}
		return Line2D.linesIntersect(p1.x, p1.y, q1.x, q1.y, p2.x, p2.y, q2.x, q2.y);
	}

	static class Graph {
		Point[] nodes;
		Integer[][] edges; // [[index_from1, index_to1], [index_from2, index_to2] ...]

		public Graph(Point[] nodes, Integer[][] edges) {
			this.nodes = nodes;
			this.edges = edges;
		}
	}

	public static int[][] getAdjacency(Graph graph) {

		int[] adjacencySizes = new int[graph.edges.length];
		
		int count = 0;

		for (int edge1Index = 0; edge1Index < graph.edges.length; edge1Index++) {
			for (int edge2Index = edge1Index + 1; edge2Index < graph.edges.length; edge2Index++) {
				Point p1 = graph.nodes[graph.edges[edge1Index][0]];
				Point q1 = graph.nodes[graph.edges[edge1Index][1]];
				Point p2 = graph.nodes[graph.edges[edge2Index][0]];
				Point q2 = graph.nodes[graph.edges[edge2Index][1]];

				if (doIntersect(p1, q1, p2, q2)) {
					adjacencySizes[edge1Index]++;
					adjacencySizes[edge2Index]++;
					count ++;

				}
			}
		}

		int[][] adjacency = new int[graph.edges.length][];
		for (int y = 0; y < adjacency.length; y++) {
			adjacency[y] = new int[adjacencySizes[y]];
		}
		int[] currentIndexes = new int[adjacency.length];

		for (int edge1Index = 0; edge1Index < graph.edges.length; edge1Index++) {
			if (true) {
				//System.out.println(edge1Index / (double) graph.edges.length);
			}
			for (int edge2Index = edge1Index + 1; edge2Index < graph.edges.length; edge2Index++) {
				Point p1 = graph.nodes[graph.edges[edge1Index][0]];
				Point q1 = graph.nodes[graph.edges[edge1Index][1]];
				Point p2 = graph.nodes[graph.edges[edge2Index][0]];
				Point q2 = graph.nodes[graph.edges[edge2Index][1]];

				if (doIntersect(p1, q1, p2, q2)) {
					adjacency[edge1Index][currentIndexes[edge1Index]] = edge2Index;
					adjacency[edge2Index][currentIndexes[edge2Index]] = edge1Index;

					currentIndexes[edge1Index]++;
					currentIndexes[edge2Index]++;

				}
			}
		}
		return adjacency;

	}

	public static void run(String instancePath, String solutionPath) {
		
		JSONParser parser = new JSONParser();
		Reader reader = null;
		
		try {
			reader = new FileReader(instancePath);
		} catch (Exception e) {

		}

		JSONObject jsonObject = null;
		
		try {
			jsonObject = (JSONObject) parser.parse(reader);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String type = (String) jsonObject.get("type");
		String id = (String) jsonObject.get("id");

		JSONArray x_list = (JSONArray) jsonObject.get("x");
		JSONArray y_list = (JSONArray) jsonObject.get("y");
		JSONArray edge_i_list = (JSONArray) jsonObject.get("edge_i");
		JSONArray edge_j_list = (JSONArray) jsonObject.get("edge_j");

		int nEdges = edge_i_list.size();
		int nNodes = x_list.size();

		Point[] nodes = new Point[nNodes];
		for (int u = 0; u < nNodes; u++) {
			nodes[u] = new Point((int) ((Long) x_list.get(u)).intValue(), (int) ((Long) y_list.get(u)).intValue());
		}

		Integer[][] edges = new Integer[nEdges][];
		for (int u = 0; u < nEdges; u++) {
			edges[u] = new Integer[2];
			edges[u][0] = (Integer) ((Long) edge_i_list.get(u)).intValue();
			edges[u][1] = (Integer) ((Long) edge_j_list.get(u)).intValue();
		}

		Graph graph = new Graph(nodes, edges);
		System.out.println("Computing adjacency...");
		int[][] adjacency = getAdjacency(graph);
		System.out.println("Computing dsatur...");
		int[] coloring = dsatur(adjacency, graph.edges.length);

		int nColors = 0;
		for (int y = 0; y < coloring.length; y++) {
			if (coloring[y] > nColors) {
				nColors = coloring[y];
			}
		}
		nColors++;

		JSONObject obj = new JSONObject();
		Long nColorsLong = (long) nColors;
		obj.put("type", "Solution_CGSHOP2022");
		obj.put("instance", id);
		obj.put("num_colors", nColorsLong);

		JSONArray coloringJsonList = new JSONArray();
		for (int h = 0; h < coloring.length; h++) {
			coloringJsonList.add(coloring[h]);
		}

		obj.put("colors", coloringJsonList);

		try (FileWriter file = new FileWriter(solutionPath)) {
			file.write(obj.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		
		String workingDirectory = System.getProperty("user.dir");
		String instancesDirectory = workingDirectory + "/instances";
		String solutionsDirectory = workingDirectory + "/solutions/";

		File dir = new File(instancesDirectory);
		System.out.println(dir.getName());
		File[] directoryListing = dir.listFiles();
		for (File file : directoryListing) {
			
			String fileName = file.getName();
			System.out.println("file: " + fileName);
			if(fileName.charAt(0)=='.') continue; //eviter les fichiers système
			String instanceAbsolutePath = file.getAbsolutePath();
			String solutionAbsolutePath = solutionsDirectory + fileName;
			
			run(instanceAbsolutePath, solutionAbsolutePath);
		}
		System.out.println("Over");
	}

}

