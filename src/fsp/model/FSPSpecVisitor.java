package fsp.model;

public interface FSPSpecVisitor {

	void visit(FSPSpecification s);

	void visit(FSPNet n);

	void visit(FSPTask t);

	void visit(FSPLink l);

	void visit(FSPCondition c);

	void visit(FSP_YNET a);

	void visit(FSP_XorSplit g);

	void visit(FSP_XorJoin g);

	void visit(FSP_TresholdOrJoin g);

	void visit(FSP_OrSplit g);

	void visit(FSP_OrJoin g);

	void visit(FSP_AndSplit g);

	void visit(FSP_AndJoin g);

	void visit(FSPCancelableTask fspCancelableTask);

	void visit(FSPCancelableConditon fspCancelableConditon);
		
	
	
}
