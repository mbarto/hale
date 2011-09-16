/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.gml.writer;

import eu.esdihumboldt.hale.common.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProviderFactory;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriterFactory;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;

/**
 * Factory for XML {@link InstanceWriter}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public class XmlInstanceWriterFactory extends AbstractIOProviderFactory<InstanceWriter>
		implements InstanceWriterFactory {

	private static final String PROVIDER_ID = "eu.esdihumboldt.hale.io.xml.writer";

	/**
	 * Default constructor
	 */
	public XmlInstanceWriterFactory() {
		super(PROVIDER_ID);
		
		addSupportedContentType("XML"); // must match the content type definition
	}

	/**
	 * @see IOProviderFactory#createProvider()
	 */
	@Override
	public InstanceWriter createProvider() {
		return new StreamGmlWriter(false);
	}

	/**
	 * @see IOProviderFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "XML (Custom root element)";
	}

}
