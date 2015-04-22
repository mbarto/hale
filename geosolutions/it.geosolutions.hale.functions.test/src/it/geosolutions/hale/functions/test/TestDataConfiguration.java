package it.geosolutions.hale.functions.test;

public enum TestDataConfiguration {

	REPROJECT("/testdata/reproject/geom-gml2.xsd","/testdata/reproject/geom-gml2.xsd","/testdata/reproject/reproject.halex.alignment.xml","/testdata/reproject/sample-point-gml2.xml");

	private String sourceSchema;
	private String targetSchema;
	private String alignment;
	private String sourceData;

	private TestDataConfiguration(String sourceSchema, String targetSchema,
			String alignment, String sourceData) {
		this.sourceSchema = sourceSchema;
		this.targetSchema = targetSchema;
		this.alignment = alignment;
		this.sourceData = sourceData;
	}

	public String getAlignment() {
		return alignment;
	}

	public String getSourceSchema() {
		return sourceSchema;
	}

	public String getTargetSchema() {
		return targetSchema;
	}
	
	public String getSourceData() {
		return sourceData;
	}

}
