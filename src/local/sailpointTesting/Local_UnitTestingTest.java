package local.sailpointTesting;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import sailpoint.api.SailPointContext;
import sailpoint.object.Identity;
import sailpoint.object.Rule;
import sailpoint.object.TaskResult;
import sailpoint.tools.GeneralException;
import sailpoint.services.standard.junit.SailPointConnectionFactory;
import sailpoint.services.standard.junit.SailPointJUnitTestHelper;

public class Local_UnitTestingTest extends SailPointJUnitTestHelper {

	private static Logger log = Logger.getLogger(Local_UnitTestingTest.class);

	public Local_UnitTestingTest() throws Exception {
		super("spadmin", "admin");
		getConsole();
	}

	@Test
	public void addValuesTest() throws GeneralException {
		try {
			SailPointConnectionFactory factory = SailPointConnectionFactory.getInstance();
			SailPointContext context = factory.getContext("spadmin", "admin");
			//System.out.println("context :: " + context);
			Identity iden = context.getObjectByName(Identity.class, "spadmin");
			System.out.println("spadmin :: " + iden);
			Rule rule = context.getObjectByName(Rule.class, "Add values rule");
			if (rule == null) {
				log.error("Rule not found with ID: c0a800dc9e381c79819e38af026f0706");
				return;
			}
			System.out.println("Rule is found ");
			Map<String, Object> args = new HashMap<String, Object>();
			int value1 = 1;
			int value2 = 2;
			args.put("value1", value1);
			args.put("value2", value2);
			System.out.println("args :: " + args);
			Object result = console.runRule(rule, args);
			log.error("Add " + value1 + " + " + value2 + " == " + result);
			if (result != null) {
				assert ((int) result == (value1 + value2));
			} else {
				log.error("Rule returned null");
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Failed error *********** :: " + e.getMessage());
		}
	}
	
	@Test
	public void testAggregation() throws GeneralException{
		
		//running aggregation
		TaskResult result;
		try {
			result = IIQSnippets.singleAccountAggregation("EpicDimensions", "1a2c3d");
			System.out.println("########Account: "  + result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Error while aggregating :: " + e.getMessage());
		}
		
		/*
		TaskDefinition refreshIdentity = context.getObjectByName(TaskDefinition.class, "Refresh Identity Cube");
		System.out.println("########Task: "  + refreshIdentity);
		
		TaskResult resultRefresh = console.runTask(refreshIdentity);
		System.out.println("########ResultRefresh: "  + resultRefresh);*/
		
		Identity identity  = context.getObjectByName(Identity.class, "1a2c3d");
		System.out.println("########Identity: "  + identity);
		
		String firstName = (String) identity.getAttribute("firstname");
		System.out.println("########firstName: "  + firstName);
		
		String frsName = "Susan";

		assert (frsName.equalsIgnoreCase(firstName));
		//assertEquals(frsName, firstName);
		

	}
}
