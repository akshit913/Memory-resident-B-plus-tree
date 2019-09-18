public class Key {
	int key;
	double value;
	//constructor to initialize  a key value pair
	public Key(int key,double value) {
		this.key=key;
		this.value=value;
	}
	//to initialize without a value (used for internal node)
	public Key(int key) {
		this.key=key;
	}
	//to get the key 
	public int getKey() {
		return key;
	}
	//to get the value from the key value pair
	public Double getValue() {
		return value;
	}
}
