package fsp.model;

public class FSP_XorSplit extends FSPSplit {


	protected final String fsp_name() {	return "XOR_SPLIT"; }
	
	private int outs;
	
	public FSP_XorSplit(int n) {
		outs = n;
		for(int i = 1; i <= n; i++) {
			this.output_names.put("out["+i+"]", null);
		}	
		this.output_names.put("default_out", null);
		this.input_names.put("in", null);
		this.control_names.put("o_cond", null);
	}

	public FSP_XorSplit(String s, int n) {
		super(s);		
		outs = n;
		for(int i = 1; i <= n; i++) {
			this.output_names.put("out["+i+"]", null);
		}
		this.output_names.put("default_out", null);
		this.input_names.put("in", null);
		this.control_names.put("o_cond", null);
	}

	@Override
	public FSPProgram toFSP(FSPProgram p) {
		String def = "";
		def += "XOR_SPLIT(N=2) = (\n";
		def += "\tin -> SPLITING \n";
		def += "\t|o_cond -> XOR_SPLIT\n";
		def += "),\n";
		def += "SPLITING = (\n";
		def += "\tout[1..N] -> XOR_SPLIT\n";
		def += "\t| default_out -> XOR_SPLIT\n";
		def += "\t| o_cond -> XOR_SPLIT\n";
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
		return this.fsp_name() + "(" + outs + ")";
	}

}
