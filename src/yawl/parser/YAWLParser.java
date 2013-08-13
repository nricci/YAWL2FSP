package yawl.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import yawl.model.YSpecification;
import yawl.util.YModel2Dot;

import com.sun.istack.internal.Builder;



public class YAWLParser {

	private File yawl_file;
	
	public YAWLParser(String fpath) {
		yawl_file = new File(fpath);
	}
	
	public YAWLParser(File input) {
		yawl_file = input;
	}

	public YSpecification parseSpecification() throws Exception {
		
		try {
			 
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
		 
			YSpecificationHandler handler = new YSpecificationHandler();
			
			YSAXHandler preliminar_info_handler = new YPreliminarInfoHandler();	
			saxParser.parse(this.yawl_file, preliminar_info_handler);
		 
		    handler.references().put("decomposition_types", preliminar_info_handler.values());
		    
			saxParser.parse(this.yawl_file, handler);
			
			YSpecification s = handler.builder().buildSpecification();
			return s;
			
		} catch (Exception e) {
			throw new Exception("Error attempting to parse specification.",e);
		}
	}
	
}
