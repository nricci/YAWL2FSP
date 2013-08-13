package fsp.model;

public class FSPTask extends FSPAbstractTask {

	
	
	public FSPTask() {
		this.input_names.put("start", null);
		this.output_names.put("end", null);
		
	}

	public FSPTask(String n) {
		super(n);
		this.input_names.put("start", null);
		this.output_names.put("end", null);
	}


	@Override
	public void accept(FSPSpecVisitor v) {
		v.visit(this);
		
	}

	@Override
	public FSPProgram toFSP(FSPProgram p) {
		String def = "";
		def += "TASK = (start -> end -> TASK).\n";
		p.add_prelude_definition(this.fsp_name(), def);
		return p;
	}

	protected final String fsp_name() {	return "TASK"; }
	
	@Override
	protected String fsp_name_w_mult() { return "TASK"; }

}
