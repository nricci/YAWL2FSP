package fsp.model;

public class FSP_OrSplit extends FSPSplit {

	protected final String fsp_name() {	return "OR_SPLIT"; }		
	
	private int outs;
	
	public FSP_OrSplit(int n) {
		outs = n;
		for(int i = 1; i <= n; i++) {
			this.output_names.put("out["+i+"]", null);
		}	
		this.output_names.put("default_out", null);
		this.input_names.put("in", null);
		this.control_names.put("o_cond", null);
	}

	public FSP_OrSplit(String s, int n) {
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
		def += "OR_SPLIT_TRIGGER(I=1) = (\n";
		def += "\tin -> TRIGGERING\n";
		def += "\t|o_cond -> OR_SPLIT_TRIGGER\n";
		def += "),\n";
		def += "TRIGGERING = (\n";
		def += "\tout[I] -> OR_SPLIT_TRIGGER\n";
		def += "\t|abort[I] -> OR_SPLIT_TRIGGER\n";
		def += "\t|o_cond -> OR_SPLIT_TRIGGER\n";
		def += ").\n";
		def += "OR_SPLIT_DEFAULT_TRIGGER(N=2) = (\n";
		def += "\tin -> OR_SPLIT_DEFAULT_TRIGGER_DEF[0][0]\n";
		def += "\t|o_cond -> OR_SPLIT_DEFAULT_TRIGGER\n";
		def += "\t),\n";
		def += "OR_SPLIT_DEFAULT_TRIGGER_DEF[i:0..N][j:0..N] = (\n";
		def += "\to_cond -> OR_SPLIT_DEFAULT_TRIGGER\n";
		def += "\t|when (i+j<N) abort[k:1..N] -> OR_SPLIT_DEFAULT_TRIGGER_DEF[i+1][j]\n";
		def += "\t|when (i+j<N) out[k:1..N] -> OR_SPLIT_DEFAULT_TRIGGER_DEF[i][j+1]\n";
		def += "\t|when (i+j==N && j==0) default_out -> OR_SPLIT_DEFAULT_TRIGGER\n";
		def += "\t|when (i+j==N && j!=0) invisible-> OR_SPLIT_DEFAULT_TRIGGER\n";
		def += ").\n";
		def += "||OR_SPLIT(N=2) = (\n";
		def += "\tforall [i:1..N] OR_SPLIT_TRIGGER(i)\n";
		def += "\t||OR_SPLIT_DEFAULT_TRIGGER(N) \n";
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
