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

package eu.esdihumboldt.hale.ui.views.properties;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * TODO Type description
 * @author Patrick Lieb
 * @param <T> the definition type
 */
public abstract class DefaultDefinitionSection<T extends Definition<?>> extends AbstractDefinitionSection<T> {
	
	/**
	 * @see AbstractSection#setInput(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void setInput(Object input) {
		if (input instanceof EntityDefinition) {
			setDefinition((T) ((EntityDefinition) input).getDefinition());
		}
		else {
			setDefinition((T) input);
		}
	}
	
}
