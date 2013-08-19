import flwa_to_fsp.FLWAtoFSP;

public class FLWAtoFSPTranslationInfoTester {

	public static void main(String[] args) {
		FLWAtoFSP t_info = FLWAtoFSP.load("flwa2fsp_translation_info");
		//System.out.println(t_info);
		System.out.println(t_info.fwla_to_fsp(args[0]));
	}

}
