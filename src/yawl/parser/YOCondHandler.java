package yawl.parser;

import org.xml.sax.Attributes;

import yawl.YSpecBuildException;

public class YOCondHandler extends YSAXHandler {

	public YOCondHandler() {
		super("outputCondition");
		this.installHandler(new YFlowHandler());
	}

	@Override
	protected void charactersOwn(char[] ch, int start, int length) {}

	@Override
	protected void charactersSkip(char[] ch, int start, int length) {}

	@Override
	protected void endElementOwn(String uri, String localName, String qName) 
	{
		values().putValue("net-component", null);
	}

	@Override
	protected void endElementSkip(String uri, String localName, String qName) {}

	@Override
	protected void startElementOwn(String uri, String localName, String qName,
			Attributes attributes) {
		
		String ocond_id = attributes.getValue("id");
		values().putValue("net-component",ocond_id);
		try {
			builder().createCondition(ocond_id,values().getValue("net"));
		} catch (YSpecBuildException e) {
			e.printStackTrace();
			throw new Error("Fatal error creating condition.");
		}
		try {
			builder().makeOutput(ocond_id);
		} catch (YSpecBuildException e) {
			e.printStackTrace();
			throw new Error("Fatal error making output condition.");
		}
	}

	@Override
	protected void startElementSkip(String uri, String localName, String qName,
			Attributes attributes) {}
}
