package fsp.model;

public class FSP_YNET extends FSPAtom {

	
	
	public FSP_YNET() {
		this.input_names.put("i_cond", null);
		this.output_names.put("o_cond", null);
	}

	public FSP_YNET(String n) {
		super(n);
		this.input_names.put("i_cond", null);
		this.output_names.put("o_cond", null);
	}

	

	@Override
	public void accept(FSPSpecVisitor v) {
		v.visit(this);
		
	}

	@Override
	public FSPProgram toFSP(FSPProgram p) {
		String def = "";
		def += "YNET = (i_cond -> o_cond -> YNET).\n";
		p.add_prelude_definition(this.fsp_name(), def);
		return p;
	}
	
	protected final String fsp_name() {	return "YNET"; }

	@Override
	protected String fsp_name_w_mult() { return "YNET"; }
	

}
