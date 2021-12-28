package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import structures.Arc;
import structures.Graph;
import structures.PartialTree;

//C:\Users\james_4\eclipse-workspace\MST\graph1.txt

public class LittleSearchEngine {

	static String address = "";

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Welcome to MSTDriver!");
		System.out.println("Note: you will have to change the \"address\" field to match the address in your computer of your graphs!");
		String input = "";

		Graph graph = null;
		

		graph = addGraph(graph, input, sc);
		

		graphMenu(graph, input, sc);
		


		sc.close();
	}
	

	public static Graph addGraph(Graph graph, String input, Scanner sc) {
		System.out.print("Please enter the name of the file: ");
		

		boolean success = false;
		

		while (!success) {
			input = sc.next();
			System.out.println();
			try {
				graph = new Graph(address + input);
				success = true;
			} catch (Exception e) {
				System.out.print("An error occurred, please input a valid file name: ");
			}
		}
		return graph;
	}
	

	public static void graphMenu(Graph graph, String input, Scanner sc) {
		while (!input.equals("q")) {
			System.out.println("--------------------------------");
			System.out.println("Graph Menu:");
			System.out.println("p   -   print graph");
			System.out.println("n   -   new graph");
			System.out.println("ptl -   creates a ptl");
			System.out.println("q   -   quit");
			System.out.print("What would you like to do? ");
			input = sc.next();
			System.out.println();
			

			switch(input) {
			case "p": 
				if (graph == null) System.out.println("null");
				else graph.print();
				break;
			case "n":
				graph = addGraph(graph, input, sc);
				break;
			case "ptl":
				ptlMenu(graph, input, sc);
				break;
			}
		}
	}
	

	public static void ptlMenu(Graph graph, String input, Scanner sc) {
		

		PartialTreeList ptl = PartialTreeList.initialize(graph);
		

		while (!input.equals("b")) {
			System.out.println("--------------------------------");
			System.out.println("PTL Menu:");
			System.out.println("p   -   print ptl");
			System.out.println("r   -   removeTreeContainingVertex");
			System.out.println("mst -   prints out an mst");
			System.out.println("b   -   return to graph menu");
			System.out.print("What would you like to do? ");
			input = sc.next();
			System.out.println();
			

			switch(input) {
			case "p": 
				print(ptl);
				break;
			case "mst":
				ArrayList<Arc> MST = PartialTreeList.execute(ptl);
				while (!MST.isEmpty()) {
					System.out.println(MST.remove(0));
				}
				break;
			case "r":
				System.out.println("Note: This is just to test RemoveTreeContainingVertex.");
				System.out.println("This is done by having you identify the tree you would like removed");
				System.out.println("and then taking that tree's root to remove it");
				print(ptl);
				System.out.print("Select the index of the tree you would like removed: ");
				input = sc.next();
				int index = Integer.parseInt(input);
				Iterator<PartialTree> itrtr = ptl.iterator();
				PartialTree tree = null;
				while (itrtr.hasNext() && index >= 0) {
					tree = itrtr.next();
					index--;
				}
				System.out.println("removing: " + tree);
				if (tree != null) ptl.removeTreeContaining(tree.getRoot());
				else System.out.println("Tree not found?");
				print(ptl);
				break;
			}
		}
	}
	

	public static void print(PartialTreeList ptl) {
		if (ptl == null) System.out.println("This ptl is empty. YEET!");
		else {
			Iterator<PartialTree> itrtr = ptl.iterator();
			int count = 0;
			while (itrtr.hasNext()) {
				PartialTree tree = itrtr.next();
				System.out.print("Tree " + count + ": ");
				System.out.println(tree);
				count++;
			}
		}
	}
	

}