package fsp.model;

public class FSPCancelableConditon extends FSPAbstractCondition {

	
	
	public FSPCancelableConditon(int n, int m) {
		super();
		for(int i = 1; i <= n; i++) {
			this.input_names.put("in["+i+"]", null);
		}
		for(int i = 1; i <= m; i++) {
			this.output_names.put("out["+i+"]", null);
		}
		ins(n);
		outs(m);
		this.control_names.put("o_cond", null);
		this.control_names.put("cancel", null);
	}

	public FSPCancelableConditon(String s, int n, int m) {
		super(s);
		for(int i = 1; i <= n; i++) {
			this.input_names.put("in["+i+"]", null);
		}
		for(int i = 1; i <= m; i++) {
			this.output_names.put("out["+i+"]", null);
		}
		ins(n);
		outs(m);

		this.control_names.put("o_cond", null);
		this.control_names.put("cancel", null);
	}

	@Override
	public FSPProgram toFSP(FSPProgram p) {
		String def = "";
		def += "CANCELABLE_CONDITION(N=2,M=2) = CCOND_INACTIVE,\n";
		def += "CCOND_INACTIVE = (\n";
		def += "\t	o_cond -> CCOND_INACTIVE\n";
		def += "\t|	in[1..N] -> CCOND_ACTIVE\n";
		def += "\t|	cancel -> CCOND_ZOMBIE\n";
		def += "),\n";
		def += "CCOND_ACTIVE = (\n";
		def += "\t	out[1..M] -> CCOND_INACTIVE\n";
		def += "\t|	cancel -> CCOND_ZOMBIE	\n";
		def += "),\n";
		def += "CCOND_ZOMBIE = (\n";
		def += "\to_cond -> CANCELABLE_CONDITION\n";
		def += ").\n";
		p.add_prelude_definition(this.fsp_name(), def);
		return p;
	}

	@Override
	public void accept(FSPSpecVisitor v) {
		v.visit(this);
		
	}
	
	protected final String fsp_name() {	return "CANCELABLE_CONDITION"; }
	
	protected final String fsp_name_w_mult() {	return "CANCELABLE_CONDITION("+ins()+","+outs()+")"; }

}
