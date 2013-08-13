package fsp.model;

public class FSPCondition extends FSPAbstractCondition {
	
	
	
	public FSPCondition(int n, int m) {
		super();
		for(int i = 1; i <= n; i++) {
			this.input_names.put("in["+i+"]", null);
		}
		for(int i = 1; i <= m; i++) {
			this.output_names.put("out["+i+"]", null);
		}
		ins(n);
		outs(m);
	}

	public FSPCondition(String s, int n, int m) {
		super(s);
		for(int i = 1; i <= n; i++) {
			this.input_names.put("in["+i+"]", null);
		}
		for(int i = 1; i <= m; i++) {
			this.output_names.put("out["+i+"]", null);
		}
		ins(n);
		outs(m);
	}

	@Override
	public FSPProgram toFSP(FSPProgram p) {
		String def = "";
		def += "CONDITION(N=2,M=2) = (\n";
		def += "\tin[1..N] -> out[1..M] -> CONDITION\n";
		def += ").\n";
		p.add_prelude_definition(this.fsp_name(), def);
		return p;
	}

	@Override
	public void accept(FSPSpecVisitor v) {
		v.visit(this);
		
	}

	protected final String fsp_name() {	return "CONDITION"; }
	
	protected final String fsp_name_w_mult() {	return "CONDITION("+ins()+","+outs()+")"; }

}
