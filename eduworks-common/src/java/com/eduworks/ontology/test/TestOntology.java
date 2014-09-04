package com.eduworks.ontology.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.eduworks.ontology.Ontology;
import com.eduworks.ontology.OntologyClass;
import com.eduworks.ontology.OntologyInstance;
import com.hp.hpl.jena.query.ReadWrite;

public class TestOntology extends OntologyTestHarness { 
	
	public static final String localDirectory = "/Users/djunker/Documents/competencies/tdb";
	
	public static String identifier;
	
	public static String idChar = ":";
	
	public static String createdOntId = "simple-create";
	public static String loadedOntId = "simple-load";
	public static String deletedOntId = "simple-delete";
	
	public static String structureOntId = "http://www.eduworks.com/competencies/structure-competency";
	public static String structuredOntId = "test-structured";
	public static String secondStructuredOntId = "test-structured-II";
	
	@BeforeClass
	public static void setUpBeforeClass() {
		
		Ontology.setTDBLocation(localDirectory);
		
		HashSet<String> toDelete = new HashSet<String>();
		
		toDelete.add(createdOntId);
		toDelete.add(loadedOntId);
		toDelete.add(deletedOntId);
		toDelete.add(structuredOntId);
		toDelete.add(secondStructuredOntId);
		
		for (String ontId : toDelete)
		{
			Ontology.getTDBDataset().begin(ReadWrite.WRITE);
			try
			{
				Ontology ont = Ontology.loadOntology(ontId);
				ont.delete();
				
				Ontology.getTDBDataset().commit();
			}
			catch (RuntimeException e)
			{
				Ontology.getTDBDataset().abort();
			}
			finally{
				Ontology.getTDBDataset().end();
			}
		}
		
		Ontology.getTDBDataset().begin(ReadWrite.READ);
		try
		{
			for (String s : Ontology.listModelIdentifiers())
			{
				System.out.println(s);
			}
		}
		finally
		{
			Ontology.getTDBDataset().end();
		}
		
		Ontology.getTDBDataset().begin(ReadWrite.WRITE);
		try{
			Ontology.createOntology(loadedOntId);
			Ontology.createOntology(deletedOntId);
			Ontology.createOntology(structuredOntId);
			Ontology.createOntology(secondStructuredOntId);
			
			Ontology.getTDBDataset().commit();
		}
		finally
		{
			Ontology.getTDBDataset().end();
		}
		
	}

	@AfterClass
	public static void tearDownAfterClass(){
		
	}

	/**
	 * FAILING TESTS
	 */
	
	// CREATE
	
	@Test (expected = RuntimeException.class)
	public void test_CreateWithEmptyString() {
		identifier = "";
		
		Ontology.getTDBDataset().begin(ReadWrite.WRITE);
		try
		{
			Ontology ont = Ontology.createOntology(identifier);
			Ontology.getTDBDataset().commit();
		}
		catch (RuntimeException e)
		{
			Ontology.getTDBDataset().abort();
			throw e;
		}
		finally
		{
			Ontology.getTDBDataset().end();
		}
	}
	
	@Test (expected = NullPointerException.class)
	public void test_CreateWithNullString(){
		identifier = null;
		
		Ontology.getTDBDataset().begin(ReadWrite.WRITE);
		try
		{
			Ontology ont = Ontology.createOntology(identifier);
			Ontology.getTDBDataset().commit();
		}
		catch (RuntimeException e)
		{
			Ontology.getTDBDataset().abort();
			throw e;
		}
		finally
		{
			Ontology.getTDBDataset().end();
		}
	}

	// LOAD
	
	@Test (expected = RuntimeException.class)
	public void test_LoadWithEmptyString(){
		identifier = "";
		
		Ontology.getTDBDataset().begin(ReadWrite.WRITE);
		try
		{
			Ontology ont = Ontology.loadOntology(identifier);
			Ontology.getTDBDataset().commit();
		}
		catch (RuntimeException e)
		{
			Ontology.getTDBDataset().abort();
			throw e;
		}
		finally
		{
			Ontology.getTDBDataset().end();
		}
	}
	
	@Test (expected = NullPointerException.class)
	public void test_LoadWithNullString(){
		identifier = null;
		
		Ontology.getTDBDataset().begin(ReadWrite.WRITE);
		try
		{
			Ontology ont = Ontology.loadOntology(identifier);
			Ontology.getTDBDataset().commit();
		}
		catch (RuntimeException e)
		{
			Ontology.getTDBDataset().abort();
			throw e;
		}
		finally
		{
			Ontology.getTDBDataset().end();
		}
	}
	
	@Test (expected = RuntimeException.class)
	public void test_LoadNonExistent(){
		identifier = "doesnt-exist";
		
		Ontology.getTDBDataset().begin(ReadWrite.WRITE);
		try
		{
			Ontology ont = Ontology.loadOntology(identifier);
			Ontology.getTDBDataset().commit();
		}
		catch (RuntimeException e)
		{
			Ontology.getTDBDataset().abort();
			throw e;
		}
		finally
		{
			Ontology.getTDBDataset().end();
		}
	}
	
	@Test (expected = RuntimeException.class)
	public void test_createInstanceWithoutClass(){
		
		Ontology.getTDBDataset().begin(ReadWrite.WRITE);
		try
		{
			Ontology ont = Ontology.loadOntology(structuredOntId);
	
			JSONObject values = new JSONObject();
			
			values.put(idChar+"competencyTitle", "test_competency");
			values.put(idChar+"CompetencyLevels", new JSONArray("["+idChar+"true, "+idChar+"false]"));
			
			ont.createInstance(idChar+"Competency", values);
			
			Ontology.getTDBDataset().commit();
		}
		catch (JSONException e)
		{
			Ontology.getTDBDataset().abort();
			throw new RuntimeException("Couldn't put values in Value Object");
		}
		catch (RuntimeException e)
		{
			Ontology.getTDBDataset().abort();
			throw e;
		}
		finally
		{
			Ontology.getTDBDataset().end();
		}
		
	}
	
	/**
	 * PASSING TESTS
	 */
	
	// CREATE
	
	@Test
	public void test_CreateSimple(){

		Ontology.getTDBDataset().begin(ReadWrite.WRITE);
		try
		{
			Ontology ont = Ontology.createOntology(createdOntId);
			Ontology.getTDBDataset().commit();
		}
		finally
		{
			Ontology.getTDBDataset().end();
		}
		
		Ontology.getTDBDataset().begin(ReadWrite.READ);
		assertTrue(Ontology.getTDBDataset().containsNamedModel(createdOntId));
		Ontology.getTDBDataset().end();
	}
	
	// LOAD
	
	@Test
	public void test_LoadSimple(){
		Ontology.getTDBDataset().begin(ReadWrite.WRITE);
		try
		{
			Ontology ont = Ontology.loadOntology(loadedOntId);
			
			assertNotNull(ont);
			
			Ontology.getTDBDataset().commit();
		}
		finally
		{
			Ontology.getTDBDataset().end();
		}
	}
	
	// DELETE
	
	@Test
	public void test_DeleteSimple(){
		Ontology.getTDBDataset().begin(ReadWrite.WRITE);
		try
		{
			Ontology ont = Ontology.loadOntology(deletedOntId);
			ont.delete();
			
			Ontology.getTDBDataset().commit();
		}
		finally
		{
			Ontology.getTDBDataset().end();
		}
		
		Ontology.getTDBDataset().begin(ReadWrite.READ);
		assertFalse(Ontology.getTDBDataset().containsNamedModel(deletedOntId));
		Ontology.getTDBDataset().end();
	}
	
	@Test
	public void test_LoadExternalStructure(){	

		Ontology.getTDBDataset().begin(ReadWrite.WRITE);
		try
		{
			Ontology ont = Ontology.loadOntology(structuredOntId);
			
			ont.addOntology(structureOntId);
			Ontology.getTDBDataset().commit();
		}
		finally
		{
			Ontology.getTDBDataset().end();
		}
		
		Ontology.getTDBDataset().begin(ReadWrite.WRITE);
		try
		{
			Ontology ont = Ontology.loadOntology(structuredOntId);
			
			JSONObject values = new JSONObject();
			
			values.put(idChar+"competencyTitle", "test_competency");
			values.put(idChar+"competencyLevels", new JSONArray("['"+idChar+"true', '"+idChar+"false']"));
			
			OntologyInstance in = ont.createInstance(idChar+"Competency", values);
			JSONObject oldRep = in.getJSONRepresentation();
		
			String id = in.getId();
			
			Ontology.getTDBDataset().commit();
			
			Ontology.getTDBDataset().begin(ReadWrite.READ);
			ont = Ontology.loadOntology(structuredOntId);
			
			in = ont.getInstance(id);
			JSONObject newRep = in.getJSONRepresentation();
			
			assertTrue("Instance has different representation after cache reload (old: "+oldRep+") (new: "+newRep+")", compareObjects(oldRep, newRep));
		
		}
		catch (JSONException e)
		{
			Ontology.getTDBDataset().abort();
			throw new RuntimeException("Couldn't put values in Value Object");
		}
		finally
		{
			Ontology.getTDBDataset().end();
		}
	}
	
	@Test
	public void test_LoadExternalSeparately(){
		Ontology.getTDBDataset().begin(ReadWrite.WRITE);
		try
		{
			Ontology ext1 = Ontology.loadOntology(structuredOntId);
			
			ext1.addOntology(structureOntId);
			
			Ontology ext2 = Ontology.loadOntology(secondStructuredOntId);
			
			ext2.addOntology(structureOntId);
			
			Ontology.getTDBDataset().commit();
			
			OntologyClass cls1 = ext1.getClass(idChar+"Competency");
			
			OntologyClass cls2 = ext2.getClass(idChar+"Competency");
			
			assertTrue("Different Class Representation for Ontologies that have class structure from same external ontology \n "+cls1.getJSONRepresentation()+"\n"+cls2.getJSONRepresentation(), compareObjects(cls1.getJSONRepresentation(), cls2.getJSONRepresentation()));
		}
		finally
		{
			Ontology.getTDBDataset().end();
		}
	}
	
	//@Test
	public void test_QueryWithExternalStructure(){
		Ontology ont = Ontology.loadOntology(secondStructuredOntId);
		
		ont.addOntology(structureOntId);
		
		JSONObject values = new JSONObject();
		
		try{
			values.put(idChar+"competencyTitle", "required_competency");
			values.put(idChar+"competencyLevels", new JSONArray("['"+idChar+"true', '"+idChar+"false']"));
		}catch(JSONException e){
			throw new RuntimeException("Couldn't put values in Value Object");
		}
		
		OntologyInstance required = ont.createInstance(idChar+"Competency", values);
		
		values = new JSONObject();
		
		try{
			values.put(idChar+"competencyTitle", "requiring_competency");
			values.put(idChar+"competencyLevels", new JSONArray("['"+idChar+"true', '"+idChar+"false']"));
			values.put(idChar+"requires", required.getId());
		}catch(JSONException e){
			throw new RuntimeException("Couldn't put values in Value Object");
		}
		
		OntologyInstance requiring = ont.createInstance(idChar+"Competency", values);
		
		assertTrue(requiring.getJSONRepresentation().has(idChar+"Requires"));
		
		String query = "PREFIX comp: <http://www.eduworks.com/competencies#> "+
			 			"SELECT ?required ?requiring "+
			 			"WHERE { "+ 
			 					"?id comp:competencyTitle ?required ." +
			 					"?id comp:requiredBy ?reqId ." +
			 					"?reqId comp:competencyTitle ?requiring" +
			 			"}";
		
		JSONObject result = ont.query(query, false);

		result = result.optJSONArray("result").optJSONObject(0);
		
		assertTrue(result.has("requiring") && result.optString("requiring").equals("requiring_competency"));
		assertTrue(result.has("required") && result.optString("required").equals("required_competency"));
			 					
	}

	
	
}
