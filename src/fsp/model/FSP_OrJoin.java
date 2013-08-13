package fsp.model;

public class FSP_OrJoin extends FSPJoin {

	protected final String fsp_name() {	return "OR_JOIN"; }	
	
	private int ins;
	
	public FSP_OrJoin(int n) {
		ins = n;
		for(int i = 1; i <= n; i++) {
			this.input_names.put("in["+i+"]", null);
		}	
		this.output_names.put("out", null);
		this.control_names.put("o_cond", null);
	}

	public FSP_OrJoin(String s, int n) {
		super(s);		
		ins = n;
		for(int i = 1; i <= n; i++) {
			this.input_names.put("in["+i+"]", null);
		}	
		this.output_names.put("out", null);
		this.control_names.put("o_cond", null);
	}

	@Override
	public FSPProgram toFSP(FSPProgram p) {
		String def = "";
		def += "OR_JOIN(N=2) = OR_JOIN_DEF[0],\n";
		def += "OR_JOIN_DEF[b:0..1] = (\n";
		def += "\to_cond -> OR_JOIN\n";
		def += "\t|in[1..N] -> OR_JOIN_DEF[1]\n";
		def += "\t|when (b!=0) out -> OR_JOIN\n";
		def += ").\n";
		p.add_prelude_definition(this.fsp_name(), def);
		return p;
	}

	@Override
	public void accept(FSPSpecVisitor v) {
		v.visit(this);
	}

	@Override
	protected String fsp_name_w_mult() {
		return this.fsp_name() + "(" + ins + ")";
	}
	
}
