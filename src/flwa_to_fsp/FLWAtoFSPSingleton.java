package flwa_to_fsp;


public class FLWAtoFSPSingleton {

	private static FLWAtoFSP _instance;
	
	public static FLWAtoFSP get_instance() {
		if (_instance == null)
			_instance = new FLWAtoFSP();
		return _instance;
	}
		
}
