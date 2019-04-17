package com.frt.fhir.rest.end2end;

import static org.junit.Assert.*;
import static java.nio.file.StandardCopyOption.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Patient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.frt.dr.model.base.PatientReference;
import com.frt.fhir.model.ResourceDictionary;
import com.frt.fhir.model.map.ResourceMapperInterface;
import com.frt.fhir.model.map.base.BaseMapper;

import ca.uhn.fhir.context.FhirContext;

public class FHIRResourceClientTest {
	public static final String CATALINA_HOME = System.getenv("CATALINA_HOME");
	public static final String FHIR_RESOURCE_SERVICE_EP = "http://localhost:8080/frt-fhir-rest/1.0/Patient";
	public static final String FRT_WAR_LOCATION = "src/../target/frt-fhir-rest.war";
	public static final String FRT_DERBY_DB_LOCATION = "src/../../../frt-schema/derby/";
	public static final String CREATE_PT_SQL = FRT_DERBY_DB_LOCATION + "create_patient_tables.sql";
	public static final String DROP_PT_SQL = FRT_DERBY_DB_LOCATION + "drop_patient_tables.sql";
	public static final String DERBY_NET_DRIVER_NAME = "org.apache.derby.jdbc.ClientDriver";
	public static final String DERBY_NET_DB_URL = "jdbc:derby://localhost:1527/tiger_db;create=true";
	public static final String[] TOMCAT_READY_MARK = new String[] {"org.apache.catalina.startup.Catalina.start Server startup in "};
	public static final String[] DERBY_READY_MARK = new String[] {
			"Apache Derby Network Server", 
			"started and ready to accept connections on port"};

	private WebTarget webTarget;
	private boolean setUpOk;
	private ca.uhn.fhir.parser.JsonParser parser;
	private String testPatientID;
	
	@Before
	public void setUp() throws Exception {
		setUpOk = true;
		
		FhirContext context = FhirContext.forR4();
		parser = (ca.uhn.fhir.parser.JsonParser)context.newJsonParser();

		webTarget = ClientBuilder.newClient().target(FHIR_RESOURCE_SERVICE_EP);
		
		if (CATALINA_HOME==null||CATALINA_HOME.isEmpty()) {
			setUpOk = false;
			System.out.println("CATALINA_HOME not set, test not ready, setUpOk=" + setUpOk);
		}
		
		if (setUpOk) {
			setUpOk = startDerby();
		}
		
		System.out.println("After startDerby()..., setUpOk=" + setUpOk);
		
		if (setUpOk) {
			System.out.println("Before prepareDatabase()..., setUpOk=" + setUpOk);
			setUpOk = prepareDatabase();
		}
		
		System.out.println("Before deployFRTWebApp()...");
		
		deployFRTWebApp();

		System.out.println("Before startTomcat()...setUpOk=" + setUpOk);
		
		if (setUpOk) {
			//setUpOk = startTomcat();
			startTomcat();
			System.out.println("After startTomcat()...setUpOk=" + setUpOk);
		}
	}

	@Test
	public void test() throws IOException {
		assertTrue("setUp() did not go through correctly, fail the test.", setUpOk);
		Response response = createPatient();
		if (response != null && response.getStatus() == 200) {
			System.out.println("Patient created, return code: " + response.getStatus());
		} else {
			if (response != null) {
				System.err.println("Patient create failed, return code: " + response.getStatus() + ", response: "
						+ response.getStatus());
			} else {
				System.err.println("Patient create error, createPatient() return null");
			}
			fail("Invoke FRT create Patient RESTFul service error.");
		}
		// read the patient back
		Patient p = readPatient();
		assertTrue("readPatient() expects return a HAPI Patient.", p!=null&&(p instanceof Patient));

		ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get(ResourceMapperInterface.PATIENT);
		
		ResourceMapperInterface mapper = ResourceDictionary.getMapper(ResourceMapperInterface.PATIENT);
		Object target = mapper.from(resourcePair.getFhir()).to(resourcePair.getFrt()).map(p);
		assertTrue("End2End test failed: Expect HAPI Patient returned from request.",
				target instanceof com.frt.dr.model.base.Patient);
		com.frt.dr.model.base.Patient pt = (com.frt.dr.model.base.Patient) target;
		// check FRT Patient generalPractitioner and managingOrganization
		PatientReference ptref = pt.getManagingOrganization();
		assertNotNull("Expect Patient.managingOrganization present.", ptref);
		// check FRT Patient gen practitioner
		List<PatientReference> genPracs = pt.getGeneralPractitioners();
		assertNotNull("Expect Patient.generalPractitioner present.", genPracs);
		assertTrue("Expect Patient.generalPractitioner has one element.", genPracs.size()==1);

		pt.setId("8");
		
		String frtStr = BaseMapper.resourceToJson(pt);

//		writeFile("src/test/data/frt_patient_sample_gold_gen.json" + System.currentTimeMillis(), frtStr);
		
		try {
			String gold = readFromFile("src/test/data/frt_patient_sample_gold.json");
//			System.out.println("frt=" + frtStr);
//			System.out.println("gold=" + gold);
			String diff = StringUtils.difference(gold, frtStr);
			System.out.println("diff=" + diff);
			assertEquals("FRT Patient json does not match the cannonical json string.", gold, frtStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("IOException when read gold file: src/test/data/frt_patient_sample_gold.json");
		}
	}

	@After
	public void tearDown() {
		shutDownTomcat();
		shutDownDerby();
	}

	private void shutDownTomcat() {
		executeCommand(CATALINA_HOME + File.separator + "bin" + File.separator + "catalina.bat stop", null, 5000l);
	}

	private void shutDownDerby() {
		executeCommand(FRT_DERBY_DB_LOCATION + "shutdown-derby.bat", null, 5000l);
	}

	private boolean prepareDatabase() throws ClassNotFoundException, SQLException, IOException {
		Connection conn = createConnection();
		try {
			executeBatch(conn, DROP_PT_SQL, true); // ignore error i.e. drop table t, t does not exist.
			return executeBatch(conn, CREATE_PT_SQL, false);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private boolean executeBatch(Connection conn, String sql, boolean ignoreErr) throws SQLException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(sql)));
		List<String> statements = new ArrayList<String>();
		String line = null;
		StringBuilder statement = new StringBuilder();
		try {
			boolean ok = true;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("/*")) {
					boolean isEnd = false;
					// comment start, read until end of comment "*/"
					do {
						line = line.trim();
						if (line.endsWith("*/")) {
							// comment ends
							isEnd = true;
						}
					} while (!isEnd && (line = br.readLine()) != null);
					if (line == null) {
						// EOF encountered while skipping comment lines
						break;
					}
					continue;
				}
				int idx = line.indexOf("--");
				if (idx >= 0) {
					if (idx == 0) {
						continue;
					}
					line = line.substring(0, idx); // strip comment
				}
				// a solid line
				if ((line.startsWith("CREATE ") || line.startsWith("INSERT ") || line.startsWith("DROP "))
						&& line.endsWith(";")) {
					// a new SQL statement started
					int len = line.length();
					line = line.substring(0, len - 1);
					statements.add(line);
					continue;
				}
				if (line.endsWith(");")) {
					// end of a statement
					statement.append(")");
					statements.add(statement.toString());
					statement = new StringBuilder();
				} else {
					statement.append(line);
				}
			}

			if (statement.length() > 0) {
				statements.add(statement.toString());
			}

//			System.out.println("SQL commands: " + statements.toString());
//
			Statement sqlstatement = conn.createStatement();

			for (String s : statements) {
				try {
					int ret = sqlstatement.executeUpdate(s);
				} catch (SQLException e) {
					if (ignoreErr) {
						continue;
					}
					ok = false;
					break;
				}
			}
			return ok;
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}

	private Connection createConnection() throws SQLException, ClassNotFoundException {
		Class.forName(DERBY_NET_DRIVER_NAME);
		return DriverManager.getConnection(DERBY_NET_DB_URL);
	}

	private void deployFRTWebApp() throws IOException {
		File war = new File(FRT_WAR_LOCATION);
		if (!war.exists() || !war.isFile()) {
			String warPath = war.getAbsolutePath();
			throw new IllegalStateException(
					"FRT WAR file not available at location : " + warPath + ", please built the war file first.");
		}
		File tomcatDir = new File(CATALINA_HOME);
		if (!tomcatDir.exists()) {
			throw new IllegalStateException("Tomcat installation not detected at CATALINA_HOME :" + CATALINA_HOME
					+ ", Please install tomcat and set CATALINA_HOME accordingly.");
		}
		File webAppDir = new File(tomcatDir, "webapps");
		if (!webAppDir.exists()) {
			webAppDir.mkdirs();
		}
		File destWar = new File(webAppDir, "frt-fhir-rest.war");
		Files.copy(war.toPath(), destWar.toPath(), REPLACE_EXISTING);
	}

	private Response createPatient() throws IOException {
		// create a HAPI patient
		File f = new File("src/test/data/fhir_patient_resource_sample_6k.json");
		String json = new String(Files.readAllBytes(Paths.get("src/test/data/fhir_patient_resource_sample_6k.json")),
				StandardCharsets.UTF_8);
		// decode to HAPI Patient
		Patient p = (Patient)parser.parseResource(json);
		p.setId((testPatientID="IDFROMEND2ENDTEST"+System.currentTimeMillis()));
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		Response resp = invocationBuilder.post(Entity.json(this.parser.encodeResourceToString(p)));
		return resp;
	}

	private Patient readPatient() {
		WebTarget patientRS = webTarget.path(testPatientID);
		Invocation.Builder invocationBuilder = patientRS.request(MediaType.APPLICATION_JSON);
		String p = invocationBuilder.get(String.class);
		Patient pt = (Patient)this.parser.parseResource(p);
		return pt;
	}

	private static String readFromFile(String filePath) throws IOException {
		return new String(Files.readAllBytes(Paths.get(filePath)));
	}

	private boolean startDerby() {
		return executeCommand(FRT_DERBY_DB_LOCATION + "start-derby.bat", DERBY_READY_MARK, 20000l);
	}

	private boolean startTomcat() {
		return executeCommand(CATALINA_HOME + File.separator + "bin" + File.separator + "catalina.bat run", TOMCAT_READY_MARK, 60000l);
	}

	private boolean executeCommand(String cmd, String[] ready_mark, long timeout) {
		boolean startedOK = true;
		System.out.println(">>>>>>>>>>>>>>>>>>>>Executed command: " + cmd);
		System.out.flush();
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			// read the output from the command
			System.out.println("Process's STDOUT:\n");
			boolean timedOut = readWithTimeout(stdInput, ready_mark, timeout);
			// read any errors from the attempted command
			if (timedOut&&ready_mark!=null) {
				// if process start timed out, drain some std err
				System.out.println("Process's STDERR:\n");
				readWithTimeout(stdError, null, timeout);
				startedOK = false;
			}
			System.out.println("Executed command: " + cmd);
		} catch (IOException e) {
			System.out.println("Exception when execute: " + cmd);
			e.printStackTrace();
			startedOK = false;
		}
		System.out.println(">>>>>>>>>>>>>>>>>>>>Executed command: " + cmd + ", return : " + startedOK);
		System.out.flush();
		return startedOK;
	}

	private boolean readWithTimeout(BufferedReader reader, String[] ready_mark, long timeout) throws IOException {
		boolean timedOut = false;
		String line = null;
		long start = System.currentTimeMillis();
		while (true) {
			if (System.currentTimeMillis()-start > timeout) {
				timedOut = true;
				break;
			}
			if (!reader.ready()) {
				try {
					Thread.currentThread().sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}
			line = reader.readLine();
			if (line!=null&&!line.isEmpty()) {
				System.out.println(line);
				if (ready_mark!=null) {
					int matchCount = ready_mark.length;
					for (String mark: ready_mark) {
						if (line.contains(mark)) {
							matchCount--;
						}
					}
					if (matchCount==0) {
						System.out.println("READY MARK DETECTED: PROCESS STARTED AND READY.\n");
						break;
					}
				}
			}
			else {
				System.out.print(".");
			}
		}
		return timedOut;
	}

	private void writeFile(String filePath, String content) {
		FileWriter fw=null;
		try {
			fw = new FileWriter(new File(filePath));
			fw.write(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (fw!=null) {
				try {
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
