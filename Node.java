
import java.util.ArrayList;
import java.util.List;

public class Node {
	private List<Key> keys;//to keep a track of key value pairs 
	private List<Node> internalChild;//to keep the track of internal nodes
	private Node prev;//previous Node
	private Node next;//next Node
	private Node parent;//parent Node
	//constructor to initialize a new node
	public Node() {
		this.keys = new ArrayList<>();
		this.internalChild = new ArrayList<>();
		this.prev = null;
		this.next = null;
	}
	//to get the list of the  keys
	public List<Key> getKeys() {
		return keys;
	}
	//to set the list of keys
	public void setKeys(List<Key> keys){
		for(int i=0;i<keys.size();i++) {
			this.keys.add(keys.get(i));
		}
	}
	//gets the node of the children 
	public List<Node> getChild(){
		return internalChild;
	}
	//sets the node of the children 
	public void setChild(List<Node> internalChild) {
		this.internalChild=internalChild;
	}
	//get the previous node
	public Node getPrev() {
		return prev;
	}
	//set the previous node
	public void setPrev(Node prev) {
		this.prev=prev;
	}
	//get the next node
	public Node getNext() {
		return next;
	}
	//set the next node
	public void setNext(Node next) {
		this.next = next;
	}
	//get the parent of the node
	public Node getParent() {
		return parent;
	}
	//set the parent of the node
	public void setParent(Node parent) {
		this.parent = parent;
	}
}
