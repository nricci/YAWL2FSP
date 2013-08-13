package yawl;

import java.util.LinkedList;

import yawl.model.GateType;
import yawl.model.YSpecification;
import yawl.parser.TranslationInfo;

public interface YSpecificationBuilder {

	/**
	 * Creates a specification with the given id.
	 * 
	 * @param id  the intended id for the new specification.
	 * @throws YSpecBuildException 
	 */
	public void createSpecification(String id) throws YSpecBuildException;
	
	/**
	 * Creates a new net with the specified id within 
	 * the given specification.
	 *
	 * @param net_id  the intended id for the new net.
	 * @param spec_id  the specification that contains the new net. 
	 * @throws YSpecBuildException 
	 */
	public void createNet(String net_id, String spec_id) throws YSpecBuildException;	
		
	/**
	 * @param net_id
	 * @throws YSpecBuildException 
	 */
	public void makeNetRoot(String net_id) throws YSpecBuildException;	
	
	/**
	 * Creates a new task with the given id within the 
	 * specified net.
	 * 
	 * @param id
	 * @param net_id
	 * @throws YSpecBuildException 
	 */
	public void createTask(String id, String net_id) throws YSpecBuildException;
	
	/**
	 * @param task_id
	 * @param decomp_net_id
	 * @throws YSpecBuildException 
	 */
	public void makeComposite(String task_id, String decomp_net_id) throws YSpecBuildException;
	
	/**
	 * @param task_id
	 * @param low
	 * @param high
	 * @throws YSpecBuildException 
	 */
	public void makeMultiple(String task_id, int low, int high, int thresh, String mode) throws YSpecBuildException;
	
	/**
	 * @param task_id
	 * @param t
	 * @throws YSpecBuildException 
	 */
	public void setInputGate(String task_id, GateType t) throws YSpecBuildException;
	
	/**
	 * @param task_id
	 * @param t
	 * @throws YSpecBuildException 
	 */
	public void setOutputGate(String task_id, GateType t) throws YSpecBuildException;
	
	/**
	 * @param task_id
	 * @param other_id
	 * @throws YSpecBuildException 
	 */
	public void setDefaultFlow(String task_id, String other_id) throws YSpecBuildException;
	
	/**
	 * @param task_id
	 * @param cancel_set_ids
	 * @throws YSpecBuildException 
	 */
	public void addCancelSet(String task_id, LinkedList<String> cancel_set_ids) throws YSpecBuildException;
	
	/**
	 * @param cond_id
	 * @param net_id
	 * @throws YSpecBuildException 
	 */
	public void createCondition(String cond_id, String net_id) throws YSpecBuildException;
	
	/**
	 * @param cond_id
	 * @throws YSpecBuildException 
	 */
	public void makeInput(String cond_id) throws YSpecBuildException;
	
	/**
	 * @param cond_id
	 * @throws YSpecBuildException 
	 */
	public void makeOutput(String cond_id) throws YSpecBuildException;
		
	/**
	 * @param id_from
	 * @param id_to
	 * @throws YSpecBuildException 
	 */
	public void addFlow(String id_from, String id_to) throws YSpecBuildException;
	
	/**
	 * @return
	 */
	public YSpecification buildSpecification();

	public String dumpPlainInfo();

	public TranslationInfo translation_info();
	
	
}
