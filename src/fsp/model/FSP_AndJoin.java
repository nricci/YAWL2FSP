package fsp.model;

import java.io.Serializable;

public class FSP_AndJoin extends FSPJoin {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5542158982620111568L;

	protected final String fsp_name() {	return "AND_JOIN"; }
	
	private int ins;
	
	public FSP_AndJoin(int n) {
		ins = n;
		for(int i = 1; i <= n; i++) {
			this.input_names.put("in["+i+"]", null);
		}	
		this.output_names.put("out", null);
		this.control_names.put("o_cond", null);
	}

	public FSP_AndJoin(String s, int n) {
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

		def += "AND_JOIN_COMPONENT(I=1) = (\n";
		def += "\tin[I] -> JOINING\n";
		def += "\t|o_cond -> AND_JOIN_COMPONENT\n";
		def += "),\n";
		def += "JOINING = (\n";
		def += "\tout -> AND_JOIN_COMPONENT\n";
		def += "\t|o_cond -> AND_JOIN_COMPONENT\n";
		def += ").\n";
		def += "|| AND_JOIN(N=2) = (\n";
		def += "\tforall [i:1..N] AND_JOIN_COMPONENT(i)\n";
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
