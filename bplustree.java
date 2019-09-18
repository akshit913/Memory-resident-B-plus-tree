import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class bplustree {
	public boolean found=false;
	private int m;//order of the tree
	private  Node root;//root node of the tree
	/*
	 * Initializes a B+ tree with order m
	 */
	public void initialize(int m) {
		this.m=m;
		this.root=null;
	}
	/* To insert a key and value pair in the B+ tree.*/
	public void insert(int key,Double value) {
		/*When there is no node in the tree. 
		 * The first added node will become the root node.*/
		if(this.root==null) {
			Node node=new Node();
			List<Key> keys=node.getKeys();
			Key newKey=new Key(key,value);
			keys.add(newKey);
			this.root=node;
			this.root.setParent(null);
		}/*Insertion when there is only one node which is not full.*/ 
		else if(this.root.getKeys().size()<(this.m-1) && root.getChild().isEmpty()) {
			int keyIndex = internalSearch(key, root.getKeys());
			if (keyIndex != 0 && root.getKeys().get(keyIndex - 1).getKey() == key) {
				//key already exists
			} else {
				// add key and value since they don't exist
				Key nKey = new Key(key, value);
				root.getKeys().add(keyIndex, nKey);
			}
		}/*Insert with splitting */
		else {
			Node pNode;
			//traverse to the external nodes since values are inserted only at the external nodes.
			pNode=getNode(this.root,key);
			int keyIndex = internalSearch(key, pNode.getKeys());
			if (keyIndex != 0 && pNode.getKeys().get(keyIndex - 1).getKey() == key) {
				//key already exists
			} else {
				// add key and value since they don't exist
				Key nKey = new Key(key, value);
				pNode.getKeys().add(keyIndex, nKey);
			}
			/*check if the node is full.
			 * if full split the node.*/
			if (pNode.getKeys().size() == this.m) {
				externalSplit(pNode, this.m);
			}
		}
	}
	/*used to split the node if it overflows*/
	private void externalSplit(Node pNode, int m) {
		int mid = m / 2;//middle index

		Node middle = new Node();///middle Node
		Node right = new Node();//right Node
		//store the mid and right part in the right Node variable.
		right.setKeys(pNode.getKeys().subList(mid, pNode.getKeys().size()));
		//set the middle element parent of right and convert it into an internal node.
		right.setParent(middle);
		middle.getKeys().add(new Key(pNode.getKeys().get(mid).getKey()));
		middle.getChild().add(right);
		pNode.getKeys().subList(mid, pNode.getKeys().size()).clear();
		boolean isFirst = true;
		internalSplit(pNode.getParent(), pNode, m, middle, isFirst);
	}
	//to split the internal node
	private void internalSplit(Node pNode, Node prevNode, int m, Node toInsert, boolean isFirst) {
		if (pNode==null) {
			this.root = toInsert;
			//find the index of the position at which the child is to be inserted. 
			int prevIndex = internalSearch(prevNode.getKeys().get(0).getKey(), toInsert.getKeys());
			prevNode.setParent(toInsert);
			toInsert.getChild().add(prevIndex, prevNode);
			//split the node only for the external node
			if (isFirst) {
				if (prevIndex == 0) {
					toInsert.getChild().get(0).setNext(toInsert.getChild().get(1));
					toInsert.getChild().get(1).setPrev(toInsert.getChild().get(0));
				} else {
					toInsert.getChild().get(prevIndex + 1).setPrev(toInsert.getChild().get(prevIndex));
					toInsert.getChild().get(prevIndex - 1).setNext(toInsert.getChild().get(prevIndex));
				}
			}
		} else {//merge internal node with the result of previous merge
			internalMerge(toInsert, pNode);
			//split again if the internal node becomes full.
			if (pNode.getKeys().size() == m) {
				int mid = (int) Math.ceil(m / 2.0) - 1;
				Node middle = new Node();
				Node right = new Node();
				right.setKeys(pNode.getKeys().subList(mid + 1, pNode.getKeys().size()));
				right.setParent(middle);
				middle.getKeys().add(pNode.getKeys().get(mid));
				middle.getChild().add(right);
				//add the children in the right part 
				splitHelp(pNode, middle, right);
				pNode.getKeys().subList(mid, pNode.getKeys().size()).clear();
				//split again since the node got full and move it to one level above
				internalSplit(pNode.getParent(), pNode, m, middle, false);
			}
		}
	}

	//to merge the internal nodes.
	private void internalMerge(Node toMerge, Node desMerge) {
		Key key = toMerge.getKeys().get(0);
		Node child = toMerge.getChild().get(0);
		//search the position where the key is to be inserted.
		int insertIndex = internalSearch(key.getKey(), desMerge.getKeys());
		int childPos = insertIndex;
		if (key.getKey() <= child.getKeys().get(0).getKey()) {
			childPos = insertIndex + 1;
		}
		//set the node to be merged as the parent
		child.setParent(desMerge);
		desMerge.getChild().add(childPos, child);
		desMerge.getKeys().add(insertIndex, key);
		//update the list of external nodes
		intMerge(desMerge, childPos);
	}
	//to delete the node 
	public void delete(int key) {
		Node pNode;
		//fetch the keys of the node which has the key that is to be deleted
		pNode=getNode(this.root,key);
		List<Key> keys = pNode.getKeys();
		for (int i = 0; i < keys.size(); i++) {
			if (key == keys.get(i).getKey()) {
				//if key is found remove it
				keys.remove(i);
			}
		}
	}
	//search for a given key
	public double search(int key) {
		double resValue=0;
		Node pNode;
		//get the node with the key
		pNode=getNode(this.root,key);
		List<Key> keys = pNode.getKeys();
		//search for the key 
		//if found store it in resValue and set the found variable to true.
		for (int i = 0; i < keys.size(); i++) {
			if (key == keys.get(i).getKey()) {
				resValue = keys.get(i).getValue();
				found=true;
			}
			if (key < keys.get(i).getKey()) {
				//if not found set the variable to false
				found=false;
				break;
			}
		}

		return resValue;
	}
	//search for the keys in between key1 and key2
	public List<Double> search(int key1, int key2) {
		List<Key> resKeys = new ArrayList<>();
		Node pNode;
		//find the node with key1
		pNode=getNode(this.root,key1);
		//store the Keys which have the key between key1 and key2 and sets isEnd true if a value greater than key2 is found
		boolean isEnd = false;
		while (null != pNode && !isEnd) {
			for (int i = 0; i < pNode.getKeys().size(); i++) {
				if (pNode.getKeys().get(i).getKey() >= key1 && pNode.getKeys().get(i).getKey() <= key2)
					resKeys.add(pNode.getKeys().get(i));
				if (pNode.getKeys().get(i).getKey() > key2) {
					isEnd = true;
				}
			}
			pNode = pNode.getNext();
		}
		//extracts the value from the key value pair
		List<Double> resVal=new ArrayList<>();
		for(int i=0;i<resKeys.size();i++) {
			resVal.add(resKeys.get(i).getValue());
		}
		return resVal;
	}
	//split helper function to add the nodes to the right side
	public void splitHelp(Node node,Node midNode,Node rNode) {
		List<Node> curChild = node.getChild();
		List<Node> rightChild = new ArrayList<>();
		int leftChild = curChild.size() - 1;
		for (int i = curChild.size() - 1; i >= 0; i--) {
			List<Key> currKeysList = curChild.get(i).getKeys();
			if (midNode.getKeys().get(0).getKey() <= currKeysList.get(0).getKey()) {
				//set right node as parent
				curChild.get(i).setParent(rNode);
				//add current node as a child of right
				rightChild.add(0, curChild.get(i));
				leftChild--;
			} else {
				break;
			}
		}
		rNode.setChild(rightChild);
		node.getChild().subList(leftChild + 1, curChild.size()).clear();
	}
	//helper function to update the list of external nodes
	public void intMerge(Node node,int pos) {
		if (!node.getChild().isEmpty() && node.getChild().get(0).getChild().isEmpty()) {
			//update the pointer if merge takes place at the last node 
			if (node.getChild().size() - 1 != pos && node.getChild().get(pos + 1).getPrev() == null) {
				node.getChild().get(pos + 1).setPrev(node.getChild().get(pos));
				node.getChild().get(pos).setNext(node.getChild().get(pos + 1));
			}
			else if (pos != 0 && node.getChild().get(pos - 1).getNext() == null) {
				node.getChild().get(pos).setPrev(node.getChild().get(pos - 1));
				node.getChild().get(pos - 1).setNext(node.getChild().get(pos));
			}
			//if merge takes place in between update the prev node of the next element and
			//the next element of the prev node.
			else {
				node.getChild().get(pos).setNext(node.getChild().get(pos - 1).getNext());
				node.getChild().get(pos).getNext().setPrev(node.getChild().get(pos));
				node.getChild().get(pos - 1).setNext(node.getChild().get(pos));
				node.getChild().get(pos).setPrev(node.getChild().get(pos - 1));
			}
		}
	}
	//improvised to search to consider the internal nodes too.
	public int internalSearch(int key, List<Key> keys) {
		int start = 0;
		int length = keys.size() - 1;
		int mid;
		int index = -1;
		if (key < keys.get(start).getKey()) {
			return 0;
		}
		if (key >= keys.get(length).getKey()) {
			return keys.size();
		}
		while (start <= length) {
			mid = (start + length) / 2;
			//added condition to find a position with key in between the present key and previous key
			if (key < keys.get(mid).getKey() && key >= keys.get(mid - 1).getKey()) {
				index = mid;
				break;
			}
			else if (key >= keys.get(mid).getKey()) {
				start = mid + 1;
			} else {
				length = mid - 1;
			}
		}
		return index;
	}
	//to traverse and get node
	public Node getNode(Node node,int key) {
		while (!node.getChild().isEmpty()) {
			node = node.getChild().get(internalSearch(key, node.getKeys()));
		}
		return node;
	}
	//main function to perform all the file related functions and to call all the implemented functions
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			//taking input from the scanner
			Scanner s = new Scanner(bplustree.class.getResourceAsStream(args[0]));
			// For creating output file
			BufferedWriter bw = openFile();

			bplustree tree = new bplustree();
			//tree.initialize(Integer.parseInt(sc.nextLine()));

			while (s.hasNextLine()) {
				String newLine = s.nextLine();
				// splitting input file line based on regex
				String[] input = newLine.split("\\(|,|\\)");
				//compare the input read from the file to call the user desired functions
				if(input[0].equals("Initialize")){
					tree.initialize(Integer.parseInt(input[1]));
				}else if(input[0].equals("Insert")){
					tree.insert(Integer.parseInt(input[1]), Double.parseDouble(input[2]));
				} else if(input[0].equals("Delete")) {
					if(input[1].substring(0)!=" ") {
					tree.delete(Integer.parseInt(input[1].substring(1)));
					}else{
					tree.delete(Integer.parseInt(input[1]));
					}
				  }
			else if(input[0].equals("Search")) {
					// for finding all key value pairs between two keys
					if (input.length == 2) {
						double res = tree.search(Integer.parseInt(input[1]));
						if(tree.found==false) {
							bw.write("null");	
						}else {
							bw.write(String.valueOf(res));
							bw.newLine();
						}
					}
					else {
						List<Double> res = tree.search(Integer.parseInt(input[1]), Integer.parseInt(input[2]));
						if (res.isEmpty()) {
							// if there is no value between the given 2 keys
							bw.write("Null");
						} else {
							bw.write(String.valueOf(res.get(0)));
							for(int i=1;i<res.size();i++) {
								bw.write(String.valueOf(","+res.get(i)));
							}
						}
						bw.newLine();
					}
					}
				}
			s.close();
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {	
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//to open a new file to write the output to it.
	private static BufferedWriter openFile() throws IOException {
		// Creating a new file to write output to (output_file.txt)
		File file = new File("output_file.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		return bw;
	}
}

