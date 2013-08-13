import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.jar.Attributes;

import fsp.FSPModel2Dot;
import fsp.FSPModel2Dot2;
import fsp.FSPSpecPrinter;
import fsp.YSpec2FSP;
import fsp.FSPSpecPrinter;
import fsp.model.FSPSpecification;

import yawl.model.YSpecification;
import yawl.parser.YAWLParser;
import yawl.util.YModel2Dot;


 
public class YAWL2FSPMain {
 
	
	public static void main(String argv[]) {
		
		if(argv.length < 1)
			throw new Error("Bad usage: input file has to be supplied as minimum.");
		
		
		File input = null;
		File output = null;
		int max_instances = -1;
		
		for(String s : argv) {
			if(s.startsWith("-")) {
				/* Parsing flags */
				if(s.startsWith("-M=")) {
					max_instances = Integer.parseInt(s.substring(3));
				}
				continue;
			}
			if(input == null) {
				input = new  File(s);
				if(!(input.exists() && input.canRead()))
					throw new Error("Bad input file: input file doesn't exist or it can't be read.");
				continue;
			}
			if(output == null) {
				output = new  File(s);
				continue;
			}			
		}
		
		if(output == null) {
			output = new  File("output.lts");
		}			
		
		
			
		try {
			YAWLParser p = new YAWLParser(input);
		
			System.out.print("Parsing file: "+input+" ... ");
			YSpecification s = p.parseSpecification();
			//YModel2Dot dot = new YModel2Dot("output/");
			//dot.YSpec2Dot(s);
			System.out.println("Done.");
			
			System.out.print("Translating YAWL -> FSP ... ");
			Attributes a = new Attributes();		
			a.putValue("multiple_tasks_high_bound", max_instances==-1?null:""+max_instances);
			YSpec2FSP translator = new YSpec2FSP(s,a);
			FSPSpecification fsp = translator.translate();
			System.out.println("Done.");
			
			
			//FSPModel2Dot2 dott = new FSPModel2Dot2("output/fsp/");
			//dott.translate(fsp);
			
			
			FSPSpecPrinter _p = new FSPSpecPrinter();
			//fsp.accept(_p);
			//System.out.println(_p.getString());
			
		
			//System.out.println(fsp.toFSP(null).dump());
			System.out.println("Writing output to file: "+output);
			
			FileWriter fstream = new FileWriter(output);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(fsp.toFSP(null).dump());
			out.close();
			System.out.println("Done.");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
}