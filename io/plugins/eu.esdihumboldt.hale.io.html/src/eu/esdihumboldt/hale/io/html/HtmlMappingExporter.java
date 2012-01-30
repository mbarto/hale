package eu.esdihumboldt.hale.io.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import com.google.common.io.Files;

import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentWriter;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoAware;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.ui.common.graph.content.CellGraphContentProvider;
import eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider;
import eu.esdihumboldt.hale.ui.util.DisplayThread;
import eu.esdihumboldt.hale.ui.util.graph.OffscreenGraph;

/**
 * Export a Mapping to HTML for documentation purposes.
 * 
 * @author Kevin Mais
 */
public class HtmlMappingExporter extends AbstractAlignmentWriter implements
		ProjectInfoAware {

	private VelocityContext context;
	private VelocityEngine ve;
	private ProjectInfo pi;
	private File file_template;
	private File tempDir;
	private Alignment alignment = null;

	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.ProjectInfoAware#setProjectInfo(eu.esdihumboldt.hale.common.core.io.project.ProjectInfo)
	 */
	@Override
	public void setProjectInfo(ProjectInfo projectInfo) {
		pi = projectInfo;
	}

	@Override
	protected String getDefaultTypeName() {
		return null;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		alignment = getAlignment();

		Template template = null;
		URL headlinePath = this.getClass().getResource("bg-headline.png"); //$NON-NLS-1$
		URL cssPath = this.getClass().getResource("style.css"); //$NON-NLS-1$
		URL linkPath = this.getClass().getResource("int_link.png"); //$NON-NLS-1$

		final String filesSubDir = FilenameUtils.removeExtension(FilenameUtils
				.getName(getTarget().getLocation().getPath())) + "_files"; //$NON-NLS-1$
		final File filesDir = new File(FilenameUtils.getFullPath(getTarget()
				.getLocation().getPath()), filesSubDir); //$NON-NLS-1$
		filesDir.mkdirs();

		System.out.println("Path to save the file to: "
				+ getTarget().getLocation().getPath());

		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// generates a byteArray out of the style sheet
		byte[] cssByteArray = null;
		try {
			cssByteArray = this.urlToByteArray(cssPath);
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		// Create CSS export file
		File cssOutputFile = new File(filesDir, "style.css"); //$NON-NLS-1$
		try {
			this.byteArrayToFile(cssOutputFile, cssByteArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// generates a byteArray out of the headline picture
		byte[] headlineByteArray = null;
		try {
			headlineByteArray = this.urlToByteArray(headlinePath);
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		// Create headline picture

		File headlineOutputFile = new File(filesDir, "bg-headline.png"); //$NON-NLS-1$
		try {
			byteArrayToFile(headlineOutputFile, headlineByteArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// generates a byteArray out of the link picture
		byte[] linkByteArray = null;
		try {
			linkByteArray = this.urlToByteArray(linkPath);
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		// Create link picture

		File linkOutputFile = new File(filesDir, "int_link.png"); //$NON-NLS-1$
		try {
			this.byteArrayToFile(linkOutputFile, linkByteArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (alignment != null) {
			createImages();
		}

		File htmlExportFile = null;
		if (pi != null) {
			Date date = new Date();
			SimpleDateFormat dfm = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); //$NON-NLS-1$
			if (getTarget().getLocation() == null) {
				return null;
			}
			htmlExportFile = new File(getTarget().getLocation().getPath());

			String projectName = "Project Name : " + pi.getName();
			String author = "Project Author : " + pi.getAuthor();
			String haleVers = "Hale Version : "
					+ pi.getHaleVersion().toString();
			String exportDate = "Export Date : " + dfm.format(date);
			String description = "Description : " + pi.getDescription();
			String created = "Created Date : " + dfm.format(pi.getCreated());

			context = new VelocityContext();

			// associate variables with information datas
			context.put("author", author);
			context.put("project", projectName);
			context.put("haleVers", haleVers);
			context.put("exportDate", exportDate);
			context.put("createdDate", created);
			context.put("pi", pi);
			context.put("description", description);
			context.put("filesDir", filesSubDir);
		} else {
			// do nothing
		}

		try {
			template = ve.getTemplate(file_template.getName(), "UTF-8");
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (template != null && htmlExportFile != null) {
			FileWriter fw = new FileWriter(htmlExportFile);
			template.merge(context, fw);
			fw.close();
		}

		// delete tempDir for cleanup
		tempDir.deleteOnExit();

		return null;
	}

	/**
	 * Initialize temporary directory and template engine.
	 * 
	 * @throws Exception
	 *             if an error occurs during the initialization
	 */
	private void init() throws Exception {
		synchronized (this) {
			if (ve == null) {
				ve = new VelocityEngine();
				// create a temporary directory
				tempDir = Files.createTempDir();

				file_template = new File(tempDir, "template.vm");
				URL templatePath = this.getClass().getResource("template.html");
				FileOutputStream fos = new FileOutputStream(file_template);
				InputStream stream = templatePath.openStream();

				// copys the InputStream into FileOutputStream
				IOUtils.copy(stream, fos);

				stream.close();
				fos.close();

				ve.setProperty("file.resource.loader.path",
						tempDir.getAbsolutePath());
				// initialize VelocityEngine
				ve.init();
			}
		}
	}

	// private void sortAlignment() {
	// for (Iterator<? extends Cell> iterator =
	// alignment.getTypeCells().iterator(); iterator
	// .hasNext();) {
	// Cell cell = iterator.next();
	//
	// // Retype
	// String cellName;
	// if (cell.getEntity1().getTransformation() == null) {
	// cellName = cell.getEntity2().getTransformation().getService()
	// .getLocation();
	// } else {
	// cellName = cell.getEntity1().getTransformation().getService()
	// .getLocation();
	// }
	//			String[] tempSplit = cellName.split("\\."); //$NON-NLS-1$
	// String graphConnectionNodeName = tempSplit[tempSplit.length - 1];
	//			if (graphConnectionNodeName.equals("RenameFeatureFunction")) { //$NON-NLS-1$
	// this.retypes.add(cell);
	// }
	//
	// // Augmentation
	// if (cell.getEntity1().getTransformation() == null
	// || cell.getEntity1().getAbout().getAbout()
	//							.equals("entity/null")) { //$NON-NLS-1$
	// this.augmentations.add(cell);
	// }
	//
	// // Transformation
	// if (cell.getEntity1().getTransformation() != null) {
	// this.transformations.add(cell);
	// }
	// }
	// }

	private void byteArrayToFile(File file, byte[] byteArray)
			throws FileNotFoundException, IOException {
		if (byteArray != null) {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(byteArray);
			fileOutputStream.close();
		}
	}

	private byte[] urlToByteArray(URL url) throws Exception, IOException,
			UnsupportedEncodingException {
		URLConnection connection = url.openConnection();
		int contentLength = connection.getContentLength();
		InputStream inputStream = url.openStream();
		byte[] data = new byte[contentLength];
		inputStream.read(data);
		inputStream.close();
		return data;
	}

	private void createImages() {

		Display display;
		if (Display.getCurrent() != null) {
			// use the current display if available
			display = Display.getCurrent();
		} else {
			try {
				// use workbench display if available
				display = PlatformUI.getWorkbench().getDisplay();
			} catch (Throwable e) {
				// use a dedicated display thread if no workbench is
				// available
				display = DisplayThread.getInstance().getDisplay();
			}
		}

		final String filesSubDir = FilenameUtils.removeExtension(FilenameUtils
				.getName(getTarget().getLocation().getPath())) + "_files"; //$NON-NLS-1$
		final File filesDir = new File(FilenameUtils.getFullPath(getTarget()
				.getLocation().getPath()), filesSubDir);

		Collection<? extends Cell> cells = alignment.getTypeCells();
		Iterator<? extends Cell> it = cells.iterator();
		for (int i = 0; i < cells.size(); i++) {
			final Cell cell = it.next();
			display.syncExec(new Runnable() {
				
				@Override
				public void run() {
					OffscreenGraph off_graph = new OffscreenGraph(300, 100) {

						@Override
						protected void configureViewer(GraphViewer viewer) {
							LayoutAlgorithm algo = new TreeLayoutAlgorithm(
									TreeLayoutAlgorithm.LEFT_RIGHT);

							CellGraphContentProvider cgcp = new CellGraphContentProvider();
							GraphLabelProvider glp = new GraphLabelProvider();
							viewer.setContentProvider(cgcp);
							viewer.setLabelProvider(glp);
							viewer.setInput(cell);
							viewer.setLayoutAlgorithm(algo);
						}
					};

					try {// TODO: 
						off_graph.saveImage(new FileOutputStream(new File(
								filesDir, "img_" + "IDENTIFIER")), null);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			});
			i++;
		}
	}
}