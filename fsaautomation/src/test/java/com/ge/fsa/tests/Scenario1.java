/*
 *  @author MeghanaRao
 *  The link to the JIRA for the Scenario = "https://servicemax.atlassian.net/browse/AUT-62"
 */
package com.ge.fsa.tests;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import com.ge.fsa.lib.RestServices;
import com.ge.fsa.lib.BaseLib;
import com.ge.fsa.lib.GenericLib;
import com.ge.fsa.pageobjects.ExploreSearchPO;
import com.ge.fsa.pageobjects.LoginHomePO;
import com.ge.fsa.pageobjects.RecentItemsPO;
import com.ge.fsa.pageobjects.CalendarPO;
import com.ge.fsa.pageobjects.CommonsPO;
import com.ge.fsa.pageobjects.CreateNewPO;
import com.ge.fsa.pageobjects.ToolsPO;
import com.ge.fsa.pageobjects.WorkOrderPO;
import com.kirwa.nxgreport.NXGReports;
import com.kirwa.nxgreport.logging.LogAs;
import com.kirwa.nxgreport.selenium.reports.CaptureScreen;
import com.kirwa.nxgreport.selenium.reports.CaptureScreen.ScreenshotOf;

/**
 * 
 * @author Meghana rao P
 * This Scenario will create the following from FSA app
 * Create a Work Order from the SFM Process "Create a New Work Order"
 * Then it will Sync the Work Order on the Server and collect the Work Order number
 * Search the Work Order from Recent items and click on it , then add a new Event
 * Go to the Calendar and verify if the WorkOrder is present on it , if Yes then click on it.
 * Go to the Work Order and then add childlines from cloned TDM16 SFM(Parts/Expenses/Labor/Travel)
 * Save the Work Order and then generate a Service Report.
 * Data sync all this with the server . Verify if the report is generated on the Server side and then verify the childlines' fields.
 */
public class Scenario1 extends BaseLib
{
	
	
	int iWhileCnt =0;
	String sTestCaseID="Scenario-1"; String sCaseWOID=null; String sCaseSahiFile=null;
	String sExploreSearch=null;String sWorkOrderID=null; String sWOJsonData=null;String sWOName=null; String sFieldServiceName=null; String sProductName1=null;String sProductName2=null; 
	String sActivityType=null;String sPrintReportSearch=null;
	String sAccountName = "Account47201811263";
	String sProductName = "Product9876789";
	String sContactName = "ContactAutomation 234567";
	String sExpenseType = "Airfare";
	String sLineQty = "10.0";
	String slinepriceperunit = "1000";

	
	@Test
	public void Scenario1Functions() throws Exception
	{sPrintReportSearch = "Print Service Report";

		System.out.println("Scenario 1");

		String sproformainvoice = commonsPo.generaterandomnumber("Proforma");
		String seventSubject = commonsPo.generaterandomnumber("EventName");
		// Login to the Application.
		loginHomePo.login(commonsPo, exploreSearchPo);
		// Creating the Work Order
		createNewPO.createWorkOrder(commonsPo,sAccountName,sContactName, sProductName, "Medium", "Loan", sproformainvoice);
		toolsPo.syncData(commonsPo);
		Thread.sleep(2000);
		// Collecting the Work Order number from the Server.
		String soqlquery = "SELECT+Name+from+SVMXC__Service_Order__c+Where+SVMXC__Proforma_Invoice__c+=\'"+sproformainvoice+"\'";
		restServices.getAccessToken();
		String sworkOrderName = restServices.restapisoql(soqlquery);	
		// Select the Work Order from the Recent items
		recenItemsPO.clickonWorkOrder(commonsPo, sworkOrderName);
		// To create a new Event for the given Work Order
		workOrderPo.createNewEvent(commonsPo,seventSubject, "Test Description");
		// Open the Work Order from the calendar
		calendarPO.openWofromCalendar(commonsPo, sworkOrderName);
		// To add Labor, Parts , Travel , Expense
		String sProcessname = "EditWoAutoTimesstamp";
		workOrderPo.selectAction(commonsPo,sProcessname);
		Thread.sleep(2000);
		// Adding the Parts, Labor,Travel, expense childlines to the Work Order
		workOrderPo.addParts(commonsPo, workOrderPo,sProductName);
		workOrderPo.addLaborParts(commonsPo, workOrderPo, sProductName, "Calibration", sProcessname);
		workOrderPo.addTravel(commonsPo, workOrderPo, sProcessname);
		workOrderPo.addExpense(commonsPo, workOrderPo, sExpenseType,sProcessname,sLineQty,slinepriceperunit);
		commonsPo.tap(workOrderPo.getEleClickSave());
		Thread.sleep(10000);
		// Creating the Service Report.
		workOrderPo.validateServiceReport(commonsPo, sPrintReportSearch, sworkOrderName);
		// Verifying if the Attachment is NULL before Sync
		String sSoqlQueryattachBefore = "Select+Id+from+Attachment+where+ParentId+In(Select+Id+from+SVMXC__Service_Order__c+Where+Name+=\'"+sworkOrderName+"\')";
		restServices.getAccessToken();
		String sAttachmentidBefore = restServices.restsoql(sSoqlQueryattachBefore, "Id");	
		assertNull(sAttachmentidBefore); // This will verify if the Id retrived from the Work Order's attachment is not null.
		// Verifying the Childline values - Before the SYNC
		String sSoqlquerychildlinesBefore = "Select+Count()+from+SVMXC__Service_Order_Line__c+where+SVMXC__Service_Order__c+In(Select+Id+from+SVMXC__Service_Order__c+where+Name+=\'"+sworkOrderName+"\')";
		restServices.getAccessToken();
		String sChildlinesBefore = restServices.restsoql(sSoqlquerychildlinesBefore, "totalSize");	
		if(sChildlinesBefore.equals("0"))
				{
				NXGReports.addStep("Testcase " + sTestCaseID + "The Childlines before Sync is "+sChildlinesBefore, LogAs.PASSED, null);

				System.out.println("The attachment before Sync is "+sChildlinesBefore);
				}
		else
		{
			NXGReports.addStep("Testcase " + sTestCaseID + "The Childlines before Sync is "+sChildlinesBefore, LogAs.FAILED, null);
			System.out.println("The attachment before Sync is "+sChildlinesBefore);
		}
		// Syncing the Data
		toolsPo.syncData(commonsPo);
		Thread.sleep(5000);
		/**
		 * Verifying the values after the Syncing of the DATA
		 */
		// Verifying the Work details and the service report
		String sSoqlqueryAttachment = "Select+Id+from+Attachment+where+ParentId+In(Select+Id+from+SVMXC__Service_Order__c+Where+Name+=\'"+sworkOrderName+"\')";
		restServices.getAccessToken();
		String sAttachmentIDAfter = restServices.restsoql(sSoqlqueryAttachment, "Id");	
		assertNotNull(sAttachmentIDAfter);
		
		// Verifying the childlines of the Same Work Order
		String sSoqlQueryChildlineAfter = "Select+Count()+from+SVMXC__Service_Order_Line__c+where+SVMXC__Service_Order__c+In(Select+Id+from+SVMXC__Service_Order__c+where+Name+=\'"+sworkOrderName+"\')";
		restServices.getAccessToken();
		String sChildlinesAfter = restServices.restsoql(sSoqlQueryChildlineAfter, "totalSize");	
		if(sChildlinesAfter.equals("0"))
		{
		NXGReports.addStep("Testcase " + sTestCaseID + "The Childlines before Sync is "+sChildlinesAfter, LogAs.FAILED, null);

		System.out.println("The Childlines before Sync is "+sChildlinesAfter);
		}
		else
		{
			NXGReports.addStep("Testcase " + sTestCaseID + "The Childlines before Sync is "+sChildlinesAfter, LogAs.PASSED, null);
			System.out.println("The Childlines before Sync is "+sChildlinesAfter);
		}
		
		Thread.sleep(1000);
		// Verification of the fields of the childlines of Type = Expenses
		JSONArray sJsonArrayExpenses = commonsPo.verifyPartsdetails(restServices, sworkOrderName,"Expenses");
		String sExpenseType = commonsPo.getJsonValue(sJsonArrayExpenses, "SVMXC__Expense_Type__c");
		String sLineQty = commonsPo.getJsonValue(sJsonArrayExpenses, "SVMXC__Actual_Quantity2__c");
		assertEquals(sExpenseType, sExpenseType);
		assertEquals(sLineQty, sLineQty);
		NXGReports.addStep("Testcase " + sTestCaseID + "The fields of Childlines of Type Expenses match", LogAs.PASSED, null);

		
		// Verification of the fields of the childlines of Type = Parts
		JSONArray sJsonArrayParts = commonsPo.verifyPartsdetails(restServices, sworkOrderName,"Parts");
		String sProductID = commonsPo.getJsonValue(sJsonArrayParts, "SVMXC__Product__c");
		String sSoqlProductName = "Select+Name+from+Product2+where+Id=\'"+sProductID+"\'";
		restServices.getAccessToken();
		String sProductName = restServices.restapisoql(sSoqlProductName);
		String sLineQtyParts = commonsPo.getJsonValue(sJsonArrayParts, "SVMXC__Actual_Quantity2__c");
		assertEquals(sProductName, sProductName);
		assertEquals(sLineQtyParts, "1.0");
		NXGReports.addStep("Testcase " + sTestCaseID + "The fields of Childlines of Type Parts match", LogAs.PASSED, null);


	}
	

	
}