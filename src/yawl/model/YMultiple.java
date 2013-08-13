package yawl.model;

public interface YMultiple {
	
	public int lowerBound();

	public void lowerBound(int lowerBound);

	public int upperBound();

	public void upperBound(int upperBound);
	
	public int threshold();

	public void threshold(int t);
	
	public CreationMode creationMode();

	public void creationMode(CreationMode c); 

}
