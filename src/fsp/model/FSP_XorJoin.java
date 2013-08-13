package fsp.model;

public class FSP_XorJoin extends FSPJoin {

	protected final String fsp_name() {	return "XOR_JOIN"; }	
	
	private int ins;
	
	public FSP_XorJoin(int n) {
		ins = n;
		for(int i = 1; i <= n; i++) {
			this.input_names.put("in["+i+"]", null);
		}	
		this.output_names.put("out", null);
		this.control_names.put("o_cond", null);
	}

	public FSP_XorJoin(String s, int n) {
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
		def += "XOR_JOIN(N=2) = (\n";
		def += "\tin[1..N] -> JOINING\n";
		def += "\t|o_cond -> XOR_JOIN\n";
		def += "),\n";
		def += "JOINING = (\n";
		def += "\tout -> XOR_JOIN\n";
		def += "\t|o_cond -> XOR_JOIN\n";
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
