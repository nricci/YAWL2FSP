package fsp.model;

public class FSP_AndSplit extends FSPSplit {

	protected final String fsp_name() {	return "AND_SPLIT"; }	
	
	private int outs;
	
	public FSP_AndSplit(int n) {
		outs = n;
		for(int i = 1; i <= n; i++) {
			this.output_names.put("out["+i+"]", null);
		}	
		this.input_names.put("in", null);
		this.control_names.put("o_cond", null);
	}

	public FSP_AndSplit(String s, int n) {
		super(s);		
		outs = n;
		for(int i = 1; i <= n; i++) {
			this.output_names.put("out["+i+"]", null);
		}
		this.input_names.put("in", null);
		this.control_names.put("o_cond", null);
	}

	@Override
	public FSPProgram toFSP(FSPProgram p) {
		String def = "";

		def += "AND_SPLIT_COMPONENT(I=1) = (\n";
		def += "\tin -> TRIGGERING\n";
		def += "\t|o_cond -> AND_SPLIT_COMPONENT\n";
		def += "),\n";
		def += "TRIGGERING = (\n";
		def += "\tout[I] -> AND_SPLIT_COMPONENT\n";
		def += "\t|o_cond -> AND_SPLIT_COMPONENT\n";
		def += ").\n";
		def += "||AND_SPLIT(N=2) = (\n";
		def += "\tforall[i:1..N] AND_SPLIT_COMPONENT(i)\n";
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
