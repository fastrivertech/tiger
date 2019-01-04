package com.frt.fhir.rest.end2end;

import static org.junit.Assert.assertEquals;
import static java.nio.file.StandardCopyOption.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.hl7.fhir.dstu3.model.Patient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.frt.fhir.model.ResourceDictionary;
import com.frt.fhir.model.ResourceMapper;
import com.frt.fhir.model.ResourceMapperFactory;
import com.frt.fhir.model.base.BaseMapper;

import ca.uhn.fhir.context.FhirContext;

public class FHIRResourceClientTest {
	// http://localhost:8080/frt-fhir-rest/1.0/Patient/2
	public static final String FHIR_RESOURCE_SERVICE_EP = "http://localhost:8088/frt-fhir-rest/1.0/Patient";
	public static final String WEBAPP_DIR_LOCATION = "./src/test/webapp/";
	public static final String TOMCAT_BASEDIR_LOCATION = "src/test/";
	public static final String FRT_WAR_LOCATION = "src/../target/frt-fhir-rest.war";
	public static final String FRT_DERBY_DB_LOCATION = "src/../../../frt-schema/derby/";
	public static final String CREATE_PT_SQL = FRT_DERBY_DB_LOCATION + "create_patient_tables.sql";
	public static final String DROP_PT_SQL = FRT_DERBY_DB_LOCATION + "drop_patient_tables.sql";
	public static final String DERBY_EMBEDDED_DRIVER_NAME = "org.apache.derby.jdbc.EmbeddedDriver";
	public static final String DERBY_EMBEDDED_DB_URL = "jdbc:derby:" + FRT_DERBY_DB_LOCATION + "TEST_TIGER_DB;create=true";
	// <property name="javax.persistence.jdbc.url"
	// value="jdbc:derby:C:\apache\derby\tiger_db"/>

	private Client client;
	private WebTarget webTarget;
	private ca.uhn.fhir.context.FhirContext context;
	private ca.uhn.fhir.parser.JsonParser parser;

	@Before
	public void setUp() throws Exception {

		client = ClientBuilder.newClient();
		webTarget = client.target(FHIR_RESOURCE_SERVICE_EP);
		context = FhirContext.forDstu3();
		parser = (ca.uhn.fhir.parser.JsonParser) context.newJsonParser();

		boolean ret = prepareDatabase();

		if (!ret) {
			throw new Exception("prepare DB error, test aborted.");
		}

		// deploy frt-fhir-rest.war to web location
		deployFRT();

		// launch tomcat in another thread
		ExecutorService executor = Executors.newFixedThreadPool(2);
		Future<LifecycleState> future = executor.submit(() -> {
			return startTomcat();
		});

		if (!future.get().equals(LifecycleState.STARTED)) {
			// wait another 10 sec
			Thread.currentThread().sleep(10000);
		}
	}

	@Test
	public void test() {
		// create patient
		Response response = createPatient();
		if (response != null && response.getStatus() == 200) {
			System.out.println("Patient created, return code: " + response.getStatus());
		} else {
			if (response != null) {
				System.err.println("Patient create failed, return code: " + response.getStatus() + ", response: "
						+ response.getStatusInfo().toString());
			} else {
				System.err.println("Patient create error, createPatient() return null");
			}
			return;
		}
		// read the patient back
		Patient p = readPatient();
		if (p != null) {
			System.out.println("Patient retrieved, patient:" + p.toString());
			ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get("PATIENT");
			ResourceMapperFactory factory = ResourceMapperFactory.getInstance();
			ResourceMapper mapper = factory.create("Patient");
			Object target = mapper.from(resourcePair.getFhir()).to(resourcePair.getFrt()).map(p);
			if (target instanceof com.frt.dr.model.base.Patient) {
				String frtStr = BaseMapper.resourceToJson((com.frt.dr.model.base.Patient) target);
				try {
					String gold = readFromFile("src/test/data/frt_patient_sample_gold.json");
					System.out.println("frt=" + frtStr);
					System.out.println("gold=" + gold);
					assertEquals("FRT Patient json does not match the cannonical json string.", gold, frtStr);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println("Gold file not found: src/test/data/frt_patient_sample_gold.json");
				}
			} else {
				System.err.println("End2End test failed: Expect FRT Patient:" + target.getClass().getCanonicalName());
			}
		} else {
			System.err.println("End2End test failed: Expect HAPI Patient returned from request.");
		}
	}

	@After
	public void tearDown() {

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
				if ((line.startsWith("CREATE ") || line.startsWith("INSERT ") || line.startsWith("DROP "))&&line.endsWith(";")) {
					// a new SQL statement started
					int len = line.length();
					line = line.substring(0, len-1);
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

			System.out.println("SQL commands: " + statements.toString());
			
			Statement sqlstatement = conn.createStatement();
			
			for (String s : statements) {
				try {
					int ret = sqlstatement.executeUpdate(s);
				}
				catch (SQLException e) {
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
		Class.forName(DERBY_EMBEDDED_DRIVER_NAME);
		return DriverManager.getConnection(DERBY_EMBEDDED_DB_URL);
	}

	private void deployFRT() throws IOException {
		File war = new File(FRT_WAR_LOCATION);
		File webAppDir = new File(WEBAPP_DIR_LOCATION);
		File destWar = new File(webAppDir, "frt-fhir-rest.war");
		if (!war.exists() || !war.isFile()) {
			// things do not look right
			throw new IllegalStateException(
					"FRT WAR file not available at location : /target, please built the war file first.");
		}
		if (!webAppDir.exists()) {
			webAppDir.mkdirs();
		}
		Files.copy(war.toPath(), destWar.toPath(), REPLACE_EXISTING);
	}

	private LifecycleState startTomcat() {
		Tomcat tomcat = new Tomcat();
		// avoid default 8080 - assume it is in use by user for manual test
		String webPort = System.getenv("PORT");
		if (webPort == null || webPort.isEmpty()) {
			webPort = "8088";
		}

		tomcat.setPort(Integer.valueOf(webPort));
		tomcat.setHostname("localhost");
		String appBase = WEBAPP_DIR_LOCATION;
		tomcat.getHost().setAppBase(appBase);
		
		File docBase = new File(TOMCAT_BASEDIR_LOCATION);
		Context context = tomcat.addContext("", docBase.getAbsolutePath());
		
		try {
			Thread.currentThread().sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tomcat.getServer().getState();
	}

	private Response createPatient() {
		// create a HAPI patient
		File f = new File("src/test/data/fhir_patient_resource_sample_6k.json");
		FileReader fr = null;
		try {
			fr = new FileReader(f);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.err.println("File not found: " + f.getPath());
			return null;
		}

		org.hl7.fhir.dstu3.model.Patient hapi = parser.doParseResource(org.hl7.fhir.dstu3.model.Patient.class, fr);
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		return invocationBuilder.post(Entity.entity(hapi, MediaType.APPLICATION_JSON));
	}

	private Patient readPatient() {
		WebTarget patientRS = webTarget.path("2");
		Invocation.Builder invocationBuilder = patientRS.request(MediaType.APPLICATION_JSON);
		return invocationBuilder.get(Patient.class);
	}

	private static String readFromFile(String filePath) throws IOException {
		return new String(Files.readAllBytes(Paths.get(filePath)));
	}

}
