package yawl.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


/**
 * Stores the information changed during the
 * translation in order to be able to revert 
 * the process.
 * 
 * */
public class TranslationInfo {

	/* These maps store transformed
	 * names mapped to its original
	 * forms. The only associations
	 * mapped are of the form:
	 * 
	 * (new_name -> old_name) 
	 * 
	 * because the maps are guaranteed
	 * to be bijective.
	 * 
	*/

	/** Associates new specnames with old ones */
	private Map<String,String> specsMap;
	
	/** Associates new netnames with old ones */
	private Map<String,String> netsMap;
	
	/** A map for each net associating new names
	 * with old ones
	 */
	private Map<String,String> elemsMap;
	
	
	/**
	 * Constructor 
	 */
	public TranslationInfo() {
		super();
		this.elemsMap = new HashMap<String,String>();
		this.netsMap = new HashMap<String, String>();
		this.specsMap = new HashMap<String, String>();
	}

	/**
	 * @return the specsMap
	 */
	public Map<String, String> getSpecsMap() {
		return specsMap;
	}

	/**
	 * @param specsMap the specsMap to set
	 */
	public void setSpecsMap(Map<String, String> specsMap) {
		this.specsMap = specsMap;
	}

	/**
	 * @return the netsMap
	 */
	public Map<String, String> getNetsMap() {
		return netsMap;
	}

	/**
	 * @param netsMap the netsMap to set
	 */
	public void setNetsMap(Map<String, String> netsMap) {
		this.netsMap = netsMap;
	}

	/**
	 * @return the elemsMap
	 */
	public Map<String, String> getElemsMap() {
		return elemsMap;
	}

	/**
	 * @param elemsMap the elemsMap to set
	 */
	public void setElemsMap(Map<String, String> elemsMap) {
		this.elemsMap = elemsMap;
	}
	
	
	
	
	
	
	
	
}
