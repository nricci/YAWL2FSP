package fsp;

import fsp.model.FSPBox;
import fsp.model.FSPCancelableConditon;
import fsp.model.FSPCancelableTask;
import fsp.model.FSPCondition;
import fsp.model.FSPLink;
import fsp.model.FSPNet;
import fsp.model.FSPSpecVisitor;
import fsp.model.FSPSpecification;
import fsp.model.FSPTask;
import fsp.model.FSP_AndJoin;
import fsp.model.FSP_AndSplit;
import fsp.model.FSP_OrJoin;
import fsp.model.FSP_OrSplit;
import fsp.model.FSP_TresholdOrJoin;
import fsp.model.FSP_XorJoin;
import fsp.model.FSP_XorSplit;
import fsp.model.FSP_YNET;

public class FSPSpecPrinter implements FSPSpecVisitor {

	private String str = "";
	
	public String getString() {
		return str;
	}
	
	@Override
	public void visit(FSPSpecification s) {
		str += "Specification = " + s.name() + "\n";

	}

	@Override
	public void visit(FSPNet n) {
		str += "\tNet = " + n.name() + "\n";
	}

	@Override
	public void visit(FSPTask t) {
		str += "\t\t Task = " + t.name() + "\n";

	}

	@Override
	public void visit(FSPLink l) {
		str += "\t\t Link <name=" + l.name() + ",links=";
		for(FSPBox b : l.members()) {
			str += b.name() + " ";
		}
		str +=   ">\n";

	}

	@Override
	public void visit(FSPCondition c) {
		str += "\t\t Condition = " + c.name() + "\n";

	}

	@Override
	public void visit(FSP_YNET a) {
		str += "\t\t YNET\n";

	}

	@Override
	public void visit(FSP_XorSplit g) {
		str += "\t\t Gate = " + g + "\n";

	}

	@Override
	public void visit(FSP_XorJoin g) {

		str += "\t\t Gate = " + g + "\n";

	}

	@Override
	public void visit(FSP_TresholdOrJoin g) {

		str += "\t\t Gate = " + g + "\n";

	}

	@Override
	public void visit(FSP_OrSplit g) {

		str += "\t\t Gate = " + g + "\n";

	}

	@Override
	public void visit(FSP_OrJoin g) {

		str += "\t\t Gate = " + g + "\n";

	}

	@Override
	public void visit(FSP_AndSplit g) {

		str += "\t\t Gate = " + g + "\n";

	}

	@Override
	public void visit(FSP_AndJoin g) {

		str += "\t\t Gate = " + g + "\n";

	}

	@Override
	public void visit(FSPCancelableTask fspCancelableTask) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FSPCancelableConditon fspCancelableConditon) {
		// TODO Auto-generated method stub
		
	}

}
