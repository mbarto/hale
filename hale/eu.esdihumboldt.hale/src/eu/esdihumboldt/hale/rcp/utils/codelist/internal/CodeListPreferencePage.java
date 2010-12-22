/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.rcp.utils.codelist.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.rcp.utils.codelist.CodeListService;
import eu.esdihumboldt.hale.rcp.utils.tree.CollectionTreeNodeContentProvider;

/**
 * Code list preference page
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class CodeListPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
	private TreeViewer listViewer;
	
	private List<SearchPathNode> searchPath;

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		
		page.setLayout(new GridLayout(2, true));
		
		Label label = new Label(page, SWT.NONE);
		label.setText("Search paths");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 2, 1));
		
		// search path list
		listViewer = new TreeViewer(page);
		listViewer.setContentProvider(new CollectionTreeNodeContentProvider());
		listViewer.setLabelProvider(new SearchPathLabelProvider()); //new MultiColumnTreeNodeLabelProvider(0));
		listViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		List<String> paths = CodeListPreferenceInitializer.getSearchPath();
		searchPath = new ArrayList<SearchPathNode>(paths.size());
		for (String path : paths) {
			searchPath.add(new SearchPathNode(path));
		}
		listViewer.setInput(searchPath);
		
		// add button (using a directory dialog)
		Button add = new Button(page, SWT.PUSH);
		add.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		add.setText("Add...");
		add.setToolTipText("Add a search path");
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				final Display display = Display.getCurrent();
				DirectoryDialog dialog = new DirectoryDialog(display.getActiveShell());
				dialog.setText("Add search path");
				dialog.setMessage("Select the search path to add");
				String path = dialog.open();
				if (path != null) {
					searchPath.add(new SearchPathNode(path));
					listViewer.refresh(false);
				}
			}
		});
		
		// remove button
		Button remove = new Button(page, SWT.PUSH);
		remove.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		remove.setText("Remove");
		remove.setToolTipText("Remove the selected search path");
		remove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = listViewer.getSelection();
				if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
					TreeNode selected = (TreeNode) ((IStructuredSelection) selection).getFirstElement();
					while (selected.getParent() != null) {
						selected = selected.getParent();
					}
					searchPath.remove(selected);
					listViewer.refresh(false);
				}
			}
			
		});
		
		return page;
	}

	/**
	 * @see PreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		// save preferences
		List<String> paths = new ArrayList<String>(searchPath.size());
		for (SearchPathNode sp : searchPath) {
			paths.add(sp.getSearchPath());
		}
		CodeListPreferenceInitializer.setSearchPath(paths);
		
		// update service
		CodeListService codeListService = (CodeListService) PlatformUI.getWorkbench().getService(CodeListService.class);
		codeListService.searchPathChanged();
		
		return true;
	}

	/**
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		// ignore
	}

}
