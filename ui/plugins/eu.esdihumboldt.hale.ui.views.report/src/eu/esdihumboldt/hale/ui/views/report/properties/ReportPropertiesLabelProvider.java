/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.report.properties;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.ui.views.report.ReportListLabelProvider;

/**
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportPropertiesLabelProvider extends ReportListLabelProvider {

	/**
	 * @see ReportListLabelProvider#getImage(Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof IStructuredSelection) {
			// overwrite element with first element from selection
			element = ((IStructuredSelection) element).getFirstElement();
		}
		return super.getImage(element);
	}

	/**
	 * @see ReportListLabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof IStructuredSelection) {
			// overwrite element with first element from selection
			element = ((IStructuredSelection) element).getFirstElement();
		}

		return super.getText(element);
	}
}
