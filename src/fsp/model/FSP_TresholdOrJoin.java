package fsp.model;

public class FSP_TresholdOrJoin extends FSPJoin {

	protected final String fsp_name() {	return "TRSH_OR_JOIN"; }	
	
	private int ins;
	
	private int treshold;
	
	public FSP_TresholdOrJoin(int n, int t) {
		ins = n;
		treshold = t;
		for(int i = 1; i <= n; i++) {
			this.input_names.put("in["+i+"]", null);
		}	
		this.output_names.put("out", null);
		this.control_names.put("o_cond", null);
	}

	public FSP_TresholdOrJoin(String s, int n, int t) {
		super(s);		
		treshold = t;
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
		def += "TRSH_OR_JOIN(N=2,T=1) = TRSH_OR_JOIN_DEF[0],\n";
		def += "TRSH_OR_JOIN_DEF[b:0..N] = (\n";
		def += "\t o_cond -> TRSH_OR_JOIN\n";
		def += "\t | in[1..N] -> TRSH_OR_JOIN_DEF[b+1]\n";
		def += "\t | when (b>=T) out -> TRSH_OR_JOIN\n";
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
		return this.fsp_name() + "(" + ins +","+ treshold+")";
	}

}
