package yawl.model;

public interface YSpecVisitor {


	public void visit(YSpecification s);
	
	public void visit(YNet n);
	
	public boolean visited(YNetElement e);

	public void visit(YCondition t);
	
	public void visit(YTask t);
	
	public void visit(YCompositeTask t);
	
	public void visit(YMultipleTask t);
	
	public void visit(YMultipleCompositeTask t);
	
	
}
