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

package eu.esdihumboldt.hale.ui.service.instance.internal.orient;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.record.impl.ODocument;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.OInstance;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;

/**
 * Store instances in a database
 * @author Simon Templer
 */
public abstract class StoreInstancesJob extends Job {
	
	private static final ALogger log = ALoggerFactory.getLogger(StoreInstancesJob.class);

	private final InstanceCollection instances;
	private final LocalOrientDB database;

	/**
	 * Create a job that stores instances in a database
	 * 
	 * @param name the (human readable) job name
	 * @param instances the instances to store in the database
	 * @param database the database
	 */
	public StoreInstancesJob(String name, LocalOrientDB database, InstanceCollection instances) {
		super(name);
		
		setUser(true);
		
		this.database = database;
		this.instances = instances;
	}

	/**
	 * @see Job#run(IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		boolean exactProgress = instances.hasSize();
		monitor.beginTask("Store instances in database", 
				(exactProgress)?(instances.size()):(IProgressMonitor.UNKNOWN));

		int count = 0;
		
		// get database connection
		DatabaseReference<ODatabaseDocumentTx> ref = database.openWrite();
		ODatabaseDocumentTx db = ref.getDatabase();
		ATransaction trans = log.begin("Store instances in database");
		try {
			// use intent
			db.declareIntent(new OIntentMassiveInsert());
			
			//TODO decouple next() and save()?
			
			ResourceIterator<Instance> it = instances.iterator();
			try {
				while (it.hasNext() && !monitor.isCanceled()) {
					Instance instance = it.next();
					
					// get/create OInstance
					OInstance conv = ((instance instanceof OInstance)?
							((OInstance) instance):(new OInstance(instance)));
					
					// configure the document
					ODocument doc = conv.configureDocument(db);
					// and save it
					doc.save();
					
					count++;
					
					if (exactProgress) {
						monitor.worked(1);
					}
				}
			} finally {
				it.close();
			}
			
			db.declareIntent(null);
		} finally {
			ref.dispose();
			trans.end();
		}
		
		onComplete();
		
		monitor.done();
		String message = MessageFormat.format("Stored {0} instances in the database", count);
		log.info(message);
		return new Status((monitor.isCanceled())?(IStatus.CANCEL):(IStatus.OK), 
				HALEUIPlugin.PLUGIN_ID, message );
	}

	/**
	 * Called when the job has been completed
	 */
	protected abstract void onComplete();

}
