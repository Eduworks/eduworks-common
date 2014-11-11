package com.eduworks.ontology.test;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import com.eduworks.ontology.Ontology;
import com.eduworks.ontology.OntologyClass;
import com.eduworks.ontology.OntologyTDBModelGetter;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelGetter;
import com.hp.hpl.jena.tdb.TDBFactory;

public class TestRandom {

	public static final String localDirectory = "/Users/djunker/Java/etc/test-java-competencies/tdb";
	
	public static Dataset ds;
	
	@BeforeClass
	public static void setUpBeforeClass(){
		ds = Ontology.setTDBLocation(localDirectory);
	}
	
	//@Test
	public void test_Query(){
		String dir = "/Users/djunker/Documents/competencies/";
		String ontName = "example-competencies";
		
		
		Ontology o = Ontology.loadOntology(ds, ontName);
		
		System.out.println(o.getClassIdList());
		
		
		
		// Testing basic query capabilities
		
		System.out.println("All Required Skills for ?skill");
		
		String query = "PREFIX comp: <http://www.eduworks.com/competencies#> " +
						"SELECT ?skill ?required WHERE {" +
							"?skill comp:requires ?required" + 
						"}";
		
		JSONObject result = o.query(query, false);
		try{
			JSONArray res = result.getJSONArray("result");
			
			for(int i = 0; i< res.length(); i++){
				System.out.println(res.get(i));
			}
		}catch(JSONException e){
			
		}
		
		System.out.println();
		System.out.println("All Enabling Skills for ?skill");
		
		query = "PREFIX comp: <http://www.eduworks.com/competencies#> " +
				"SELECT ?skill ?required WHERE {" +
					"?skill comp:enabledBy ?enabler" + 
				"}";
		
		result = o.query(query, false);
		try{
			JSONArray res = result.getJSONArray("result");
			
			for(int i = 0; i< res.length(); i++){
				System.out.println(res.get(i));
			}
		}catch(JSONException e){
			
		}
		
		System.out.println();
		System.out.println("Search for Skills with 'Software' in the name or description");
		String searchRegexString = ".*?(Software).*";
		
		query = "PREFIX comp: <http://www.eduworks.com/competencies#>" +
				"SELECT ?id " +
				"WHERE " +
						"{ " +
							"?id comp:competencyTitle ?title . "+
				"			FILTER regex(str(?title), '" + searchRegexString +
			 																"', 'i')" +
			 	"		}";// +
//			 	"		UNION " +
//			 	"		{ comp:Competency comp:competencyDescription ?desc " +
//			 	"				FILTER regex(?desc, '"+ searchRegexString +
//			 																"', 'i')" +
//			 	"		}" +
//			 	"}";
		
		result = o.query(query, false);
		try{
			JSONArray res = result.getJSONArray("result");
			
			for(int i = 0; i< res.length(); i++){
				System.out.println(res.get(i));
			}
		}catch(JSONException e){
			
		}
	}
	
	
	//@Test
	public void test_secondOntology(){
		String dir = "/Users/djunker/Documents/competencies/";
		String firstOnt = "competency-structure";
		
		Ontology ont = Ontology.loadOntology(ds, firstOnt);
		
		String secondOnt = "navy-competencies";
		ont.addOntology(ds, secondOnt);
		
		OntologyClass cls = ont.getClass("Competency");
		
		System.out.println(ont.getBaseIRI());
		System.out.println(ont.getClassIdList());
		System.out.println(cls.getJSONRepresentation());
		
		System.out.println(cls.getAllInstances());
		System.out.println(ont.getObjectPropertyIdList());
		System.out.println(ont.getDataPropertyIdList());
		
	}
	
	@Test
	public void test_TDB(){
		String dir = "/Users/djunker/Documents/competencies/tdb";	
		
		Ontology.setTDBLocation(dir);
		ds.begin(ReadWrite.WRITE);
		
		for(String s : Ontology.listModelIdentifiers(ds)){
			System.out.println(s);
		}
		
		System.out.println();
		
		ds.commit();
		ds.end();
		
		Dataset dataset = TDBFactory.createDataset(dir);
		
		dataset.begin(ReadWrite.READ);
		
		String id = "http://www.eduworks.com/structure-user";
		Model m = dataset.getNamedModel(id);
		
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
		spec.setImportModelGetter(new OntologyTDBModelGetter(dataset));
		
		OntModel model = ModelFactory.createOntologyModel(spec, m);
		
		List<OntClass> list = model.listClasses().toList();
		
		System.out.println("Classes in "+id+":");
		for(OntClass cls : list){
			if(!cls.isAnon()){
				System.out.println(cls.getURI());
			}
		}
		
		dataset.commit();
		dataset.end();
		
		dataset.begin(ReadWrite.READ);
		
		m = dataset.getNamedModel("structure-user");
		
		model = ModelFactory.createOntologyModel(spec, m);
		
		list = model.listClasses().toList();
		
		System.out.println("Classes:");
		for(OntClass cls : list){
			if(!cls.isAnon()){
				System.out.println(cls.getURI());
			}
		}
		
		System.out.println();
		//System.out.println("URI: " + model.listOntologies().toList().get(0).getURI());
		
		dataset.end();
	}
	
	@Test
	public void test_ModelGetter(){
		OntModelSpec m = OntModelSpec.OWL_MEM_MICRO_RULE_INF;
		
		ModelGetter mg = m.getImportModelGetter();
		
		System.out.println(mg.getClass());
	}
	
	@Test
	public void test_TDBLoad(){
		String uri = "http://www.eduworks.com/competencies/";
		String tdbDir = "/Users/djunker/Documents/competencies/test/tdb";
		
		String inputPath = "/Users/djunker/Documents/competencies/structure-competency.owl";
		String identifier = "structure-competency";
		
		Ontology.setTDBLocation(tdbDir);
		Ontology.setDefaultURI(uri);
		
		Ontology.importToTDB(ds,inputPath, identifier);
		
		List<String> ids = Ontology.listModelIdentifiers(ds);
		
		for(String id : ids){
			System.out.println(id);
		}
		
		System.out.println();
		
		inputPath = "/Users/djunker/Documents/competencies/structure-user.owl";
		identifier = "structure-user";
		
		Ontology.importToTDB(ds, inputPath, identifier);
		
		ids = Ontology.listModelIdentifiers(ds);
		
		for(String id : ids){
			System.out.println(id);
		}
	}
	
	@Test
	public void test_loaded(){
		String uri = "http://www.eduworks.com/competencies/";
		String tdbDir = "/Users/djunker/Documents/competencies/test/tdb";
		
		Ontology.setTDBLocation(tdbDir);
		Ontology.setDefaultURI(uri);
		
		List<String> ids = Ontology.listModelIdentifiers(ds);
		
		for(String id : ids){
			System.out.println(id);
		}
	}
}
