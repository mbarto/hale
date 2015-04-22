package it.geosolutions.hale.functions.test;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Geometry;

import static org.junit.Assert.*;

import eu.esdihumboldt.cst.ConceptualSchemaTransformer;
import eu.esdihumboldt.cst.test.AbstractTransformationTest;
import eu.esdihumboldt.cst.test.TransformationExample;
import eu.esdihumboldt.hale.common.align.transformation.service.impl.DefaultInstanceSink;
import eu.esdihumboldt.hale.common.core.io.impl.NullProgressIndicator;
import eu.esdihumboldt.hale.common.core.service.ServiceManager;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultGroup;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;

public class ReprojectGeometryTest extends AbstractTransformationTest {


	protected List<Instance> transformData(TransformationExample example)
			throws Exception {
		ConceptualSchemaTransformer transformer = new ConceptualSchemaTransformer();
		DefaultInstanceSink sink = new DefaultInstanceSink();
		// FIXME global scope not supported yet
		ServiceProvider serviceProvider = new ServiceManager(ServiceManager.SCOPE_PROJECT);
		transformer.transform(example.getAlignment(), example.getSourceInstances(), sink,
				serviceProvider, new NullProgressIndicator());
		return sink.getInstances();
	}

	@Test
	public void testReproject() throws Exception {
		TransformationReproject tr = new TransformationReproject(TestDataConfiguration.REPROJECT);
		List<Instance> result = transformData(tr);
		assertTrue(result.size() > 0);

		Geometry aspectedGeometry = null;
		InstanceCollection sourceInstances = tr.getSourceInstances();		
		Iterator<Instance> sit =  sourceInstances.iterator();
		if(sit.hasNext()){
			Instance i = sit.next();			
			DefaultInstance di = (DefaultInstance)(i.getProperty(new QName("eu:esdihumboldt:hale:test", "geometry"))[0]);
			DefaultGroup dg = (DefaultGroup)(di.getProperty(new QName("http://www.opengis.net/gml/_Geometry", "choice"))[0]);
			DefaultInstance dig = (DefaultInstance)(dg.getProperty(new QName("http://www.opengis.net/gml", "Point"))[0]);
			DefaultGeometryProperty value = (DefaultGeometryProperty) dig.getValue();
			DefaultGeometryProperty geom = (DefaultGeometryProperty)value;
			MathTransform transform = CRS.findMathTransform(geom.getCRSDefinition().getCRS(), CRS.decode("EPSG:4326"), false);
			aspectedGeometry = JTS.transform( geom.getGeometry(), transform);
		}
		assertNotNull(aspectedGeometry);

		Instance resultInstance = result.get(0);
		DefaultGeometryProperty geom = (DefaultGeometryProperty)((DefaultInstance)resultInstance.getProperty(new QName("eu:esdihumboldt:hale:test", "geometry"))[0]).getValue();
		String code = CRS.lookupIdentifier( geom.getCRSDefinition().getCRS(), true );
		assertEquals("EPSG:4326",code);
		assertEquals(aspectedGeometry.getCoordinate().x,geom.getGeometry().getCoordinate().x,0);
		assertEquals(aspectedGeometry.getCoordinate().y,geom.getGeometry().getCoordinate().y,0);
		
	}



}
