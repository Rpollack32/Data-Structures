package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {

	/**
	 * Root node
	 */
	TagNode root=null;

	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;

	/**
	 * Initializes this tree object with scanner for input HTML file
	 *
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}

	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object.
	 *
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build(){
		/** COMPLETE THIS METHOD **/
		Stack<TagNode> tags = new Stack<TagNode>();
		sc.nextLine();
		root = new TagNode("html", null, null);
		tags.push(root); 

		while(sc.hasNextLine()){
			String string = sc.nextLine();
			Boolean isitATag = false;

			if(string.charAt(0) == '<'){ 
				if(string.charAt(1) == '/'){
					tags.pop();
					continue;
				}else{ 
					string = string.replace("<", "");
					string = string.replace(">", "");
					isitATag = true;
				}
			}

			TagNode temp = new TagNode(string, null, null);

			if(tags.peek().firstChild == null){
				tags.peek().firstChild = temp;
			}else{
				TagNode pointer = tags.peek().firstChild;
				while(pointer.sibling != null){
					pointer = pointer.sibling;
				}
				pointer.sibling = temp;
			}
			
			if(isitATag){
				tags.push(temp);
			}
		}

	}

	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 *
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag){
		/** COMPLETE THIS METHOD **/
		if((oldTag.equalsIgnoreCase("b") && newTag.equalsIgnoreCase("em")) || (oldTag.equalsIgnoreCase("ol") && newTag.equalsIgnoreCase("ul"))
			|| (oldTag.equalsIgnoreCase("em") && newTag.equalsIgnoreCase("b")) || (oldTag.equalsIgnoreCase("ul") && newTag.equalsIgnoreCase("ol"))){
			replaceRecursion(root, oldTag, newTag);
		}
	}

	private void replaceRecursion(TagNode root, String oldTag, String newTag){
		for(TagNode pointer = root; pointer != null; pointer = pointer.sibling){

			if (pointer.tag.equalsIgnoreCase(oldTag)  && pointer.firstChild != null){
				pointer.tag = newTag;
			}

			if(pointer.firstChild != null){
				replaceRecursion(pointer.firstChild, oldTag, newTag);
			}
		}
	}

	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 *
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		if(row <= 0){ 
			return;
		}else{
			boldRowRecursion(root, row);
		}
	}

	private TagNode boldRowRecursion(TagNode root, int row){
		TagNode orig = root;
		TagNode temp = null;
		TagNode boldTagNode = new TagNode("b", null, null);

		if (root == null){ 
			return null;
		}

		if(root.tag.equalsIgnoreCase("table")){ 
			root = root.firstChild; 

			for(int i = 0; i < row - 1; i++){
				if (root.sibling == null){ 
					return null;
				}
				root = root.sibling;
			}
			
			root = root.firstChild; 

			while(root != null){
				temp = root.firstChild;
				root.firstChild = boldTagNode;
				boldTagNode.firstChild = temp;

				boldTagNode = new TagNode("b", null, null);

				root = root.sibling; // increment
			}
			return orig;
		}
		
		boldRowRecursion(root.firstChild, row);
		boldRowRecursion(root.sibling, row);
		return root;
	}

	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and,
	 * in addition, all the li tags immediately under the removed tag are converted to p tags.
	 *
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		if (tag.equalsIgnoreCase("p") || tag.equalsIgnoreCase("em") || tag.equalsIgnoreCase("b")){
			removeTagpOremOrb(root, tag);
		}

		if (tag.equalsIgnoreCase("ol") || tag.equalsIgnoreCase("ul")){
			removeTagolOrul(root, tag);
		}
	}

	private void removeTagpOremOrb(TagNode root, String tag){
		if(root == null){ 
			return;
		}
		
		for(TagNode pointer = root; pointer != null; pointer = pointer.sibling){
			if ((pointer.tag.equalsIgnoreCase("p") || pointer.tag.equalsIgnoreCase("em") || pointer.tag.equalsIgnoreCase("b")) && pointer.firstChild != null){
				pointer.tag = pointer.firstChild.tag;
				pointer.firstChild = null;
			}

			if (pointer.firstChild != null) {
				removeTagpOremOrb(pointer.firstChild, tag);
			}
		}
	}

	private void removeTagolOrul(TagNode root, String target){
		if(root == null){ 
			return;
		}

		removeTagolOrul(root.firstChild, target);

		if(root.sibling != null && root.sibling.tag.equals(target)){ 
			TagNode pointer = root.sibling.firstChild;
			while(pointer.sibling != null){
				pointer.tag = "p";
				pointer = pointer.sibling;
			}
			pointer.tag = "p";
			pointer.sibling = root.sibling.sibling;
			root.sibling = root.sibling.firstChild;
		}

		if(root.firstChild != null && root.firstChild.tag.equals(target)){
			TagNode pointer = root.firstChild.firstChild;
			while(pointer.sibling != null){
				pointer.tag = "p";
				pointer = pointer.sibling;
			}
			pointer.tag = "p";
			pointer.sibling = root.firstChild.sibling;
			root.firstChild = root.firstChild.firstChild;
		}

		removeTagolOrul(root.sibling, target);
	}

	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 *
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag){
		if (tag.equals("em") || tag.equals("b")){
			root = addTagRecursion(this.root, word, tag);
		}
	}

	private TagNode addTagRecursion(TagNode node, String text, String tag){

		if(node.sibling != null){
			node.sibling = addTagRecursion(node.sibling, text, tag);
		}

		if(node.firstChild != null){
			node.firstChild = addTagRecursion(node.firstChild, text, tag);
		}

		if(node.tag.contains(text)){
			TagNode newTag = new TagNode(tag, node, node.sibling);
			node.sibling = null;
			return newTag;
		}

		return node;
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}