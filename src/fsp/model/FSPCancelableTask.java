package fsp.model;

public class FSPCancelableTask extends FSPAbstractTask {


	

	public FSPCancelableTask() {
		this.input_names.put("start", null);
		this.output_names.put("end", null);
		this.control_names.put("cancel", null);
		this.control_names.put("o_cond", null);
		
	}

	public FSPCancelableTask(String n) {
		super(n);
		this.input_names.put("start", null);
		this.output_names.put("end", null);
		this.control_names.put("cancel", null);
		this.control_names.put("o_cond", null);
	}

	@Override
	public FSPProgram toFSP(FSPProgram p) {
		String def = "";
		def += "CANCELABLE_TASK = CTASK_INACTIVE,\n";
		def += "CTASK_INACTIVE = (\n";
		def += "\to_cond -> CTASK_INACTIVE\n";
		def += "\t|	start -> CTASK_ACTIVE\n";
		def += "\t|	cancel -> CTASK_ZOMBIE\n";
		def += "),\n";
		def += "CTASK_ACTIVE = (\n";
		def += "\tend -> CTASK_INACTIVE\n";
		def += "\t|	cancel -> CTASK_ZOMBIE\n";
		def += "),\n";
		def += "CTASK_ZOMBIE = (\n";
		def += "\to_cond -> CANCELABLE_TASK\n";
		def += ").\n";
		p.add_prelude_definition(this.fsp_name(), def);
		return p;
	}

	
	@Override
	public void accept(FSPSpecVisitor v) {
		v.visit(this);
		
	}
	
	protected final String fsp_name() {	return "CANCELABLE_TASK"; }

	@Override
	protected String fsp_name_w_mult() { return "CANCELABLE_TASK"; }

}
