package local.sailpointTesting;
import java.util.HashMap;

import org.apache.log4j.Logger;

import sailpoint.api.Aggregator;
import sailpoint.api.SailPointContext;
import sailpoint.connector.Connector;
import sailpoint.object.Application;
import sailpoint.object.Attributes;
import sailpoint.object.ResourceObject;
import sailpoint.object.Rule;
import sailpoint.object.TaskResult;
import sailpoint.services.standard.junit.SailPointConnectionFactory;

public class IIQSnippets {
	
	private static Logger log = Logger.getLogger(IIQSnippets.class);
	
	public static TaskResult singleAccountAggregation(String applicationName , String accountName) {
		// Initialize the error message to nothing.
		String errorMessage = "";
		try {
			SailPointConnectionFactory factory = SailPointConnectionFactory.getInstance();
			SailPointContext context = factory.getContext("spadmin", "admin");
			
			// We have already validated all of the arguments.  No just load the objects.
			Application appObject = context.getObjectByName(Application.class, applicationName);
			String appConnName = appObject.getConnector();
			log.error("Application " + applicationName + " uses connector " + appConnName);

			Connector appConnector = sailpoint.connector.ConnectorFactory.getConnector(appObject, null);
			if (null == appConnector) {
			   errorMessage = "Failed to construct an instance of connector [" + appConnName + "]";
			   //return errorMessage;
			}

			log.error("Connector instantiated, calling getObject() to read account details...");

			ResourceObject rObj = null;
			try {
			   
			   rObj = (ResourceObject) appConnector.getObject("account", accountName, null);
			   
			} catch (sailpoint.connector.ObjectNotFoundException onfe) {
			   errorMessage = "Connector could not find account: [" + accountName + "]";
			   errorMessage += " in application  [" + applicationName + "]";
			   log.error(errorMessage);
			   log.error(onfe);   
			   //return errorMessage;
			}

			if (null == rObj) {
			   errorMessage = "ERROR: Could not get ResourceObject for account: " + accountName;
			   log.error(errorMessage);
			   //return errorMessage;
			}

			log.debug("Got raw resourceObject: " + rObj.toXml());

			Rule customizationRule = appObject.getCustomizationRule();
			if (null != customizationRule) {

			   log.debug("Customization rule found for applicaiton " + applicationName);   
			   
			   try {
			   
			      // Pass the mandatory arguments to the Customization rule for the app.
			      HashMap ruleArgs = new HashMap();
			      ruleArgs.put("context",     context);
			      ruleArgs.put("log",         log);
			      ruleArgs.put("object",      rObj);
			      ruleArgs.put("application", appObject);
			      ruleArgs.put("connector",   appConnector);
			      ruleArgs.put("state",       new HashMap());
			   
			      // Call the customization rule just like a normal aggregation would.
			      ResourceObject newRObj = (ResourceObject) context.runRule(customizationRule, ruleArgs, null);
			      
			      // Make sure we got a valid resourceObject back from the rule.  
			      if (null != newRObj) {
			         rObj = newRObj;
			         log.debug("Got post-customization resourceObject: " + rObj.toXml());
			      }    
			      
			   } catch (Exception ex) {
			   
			      // Swallow any customization rule errors, the show must go on!
			      log.error("Error while running Customization rule for " + applicationName);
			         
			   } 

			}

			Attributes argMap = new Attributes();
			argMap.put("promoteAttributes",       "true");
			argMap.put("correlateEntitlements",   "true");
			argMap.put("noOptimizeReaggregation", "true");  // Note: Set to false to disable re-correlation.

			// Consturct an aggregator instance.
			Aggregator agg = new Aggregator(context, argMap);
			if (null == agg) {
			   errorMessage = "Null Aggregator returned from constructor.  Unable to Aggregate!";
			   log.error(errorMessage);
			   //return errorMessage;
			}

			// Invoke the aggregation task by calling the aggregate() method.
			// Note: the aggregate() call may take serveral seconds to complete.
			log.error("Calling aggregate() method... ");
			TaskResult taskResult = agg.aggregate(appObject, rObj);
			log.debug("aggregation complete.");

			if (null == taskResult) {
			   errorMessage = "ERROR: Null taskResult returned from aggregate() call.";
			   log.error(errorMessage);
			   //return errorMessage;
			}


			log.error("TaskResult details: \n" + taskResult);
			return taskResult;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;


	}

}
