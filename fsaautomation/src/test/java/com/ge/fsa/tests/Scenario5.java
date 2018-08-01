/*
 *  @author lakshmibs
 */
package com.ge.fsa.tests;

import org.testng.annotations.Test;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import com.ge.fsa.lib.RestServices;
import com.ge.fsa.lib.BaseLib;
import com.ge.fsa.lib.GenericLib;
import com.ge.fsa.pageobjects.ExploreSearchPO;

import com.ge.fsa.pageobjects.LoginHomePO;
import com.ge.fsa.pageobjects.CommonsPO;
import com.ge.fsa.pageobjects.ToolsPO;
import com.ge.fsa.pageobjects.WorkOrderPO;
import com.kirwa.nxgreport.NXGReports;
import com.kirwa.nxgreport.logging.LogAs;
import com.kirwa.nxgreport.selenium.reports.CaptureScreen;
import com.kirwa.nxgreport.selenium.reports.CaptureScreen.ScreenshotOf;

public class Scenario5 extends BaseLib {
	GenericLib genericLib = null;
	RestServices restServices = null;
	LoginHomePO loginHomePo = null;
	ExploreSearchPO exploreSearchPo = null;
	WorkOrderPO workOrderPo = null;
	CommonsPO commonsPo = null;
	ToolsPO toolsPo = null;
	
	int iWhileCnt = 0;
	String sTestCaseID = null;
	String sCaseWOID = null;
	String sCaseSahiFile = null;
	String sExploreSearch = null;
	String sExploreChildSearchTxt = null;
	String sWorkOrderID = null;
	String sWOObejctApi = null;
	String sWOJsonData = null;
	String sWOName1 = null;
	String sWOName2 = null;
	String sFieldServiceName = null;
	String sProductName1 = null;
	String sProductName2 = null;
	String sActivityType = null;
	String sPrintReportSearch = null;
	String sIssueTxt = null;
	String sBillingType = null;
	String sWOSqlQuery = null;

	@BeforeMethod
	public void initializeObject() throws IOException { 
		genericLib = new GenericLib();
		restServices = new RestServices();
		loginHomePo = new LoginHomePO(driver);
		exploreSearchPo = new ExploreSearchPO(driver);
		workOrderPo = new WorkOrderPO(driver);	
		toolsPo = new ToolsPO(driver);
		commonsPo = new CommonsPO(driver);
		restServices.getAccessToken();
		sWOObejctApi="SVMXC__Service_Order__c?";
		
		//Creation of dynamic Work Order1
		sWOJsonData = "{\"SVMXC__Order_Status__c\":\"Canceled\",\"SVMXC__Billing_Type__c\":\"Contract\",\"SVMXC__City__c\":\"Delhi\",\"SVMXC__Zip__c\":\"110003\",\"SVMXC__Country__c\":\"India\",\"SVMXC__State__c\":\"Haryana\"}";
		sWorkOrderID=restServices.restCreate(sWOObejctApi,sWOJsonData);
		sWOSqlQuery ="SELECT+name+from+SVMXC__Service_Order__c+Where+id+=\'"+sWorkOrderID+"\'";				
		sWOName1 =restServices.restGetSoqlValue(sWOSqlQuery,"Name"); //"WO-00000456"; 
		
		//Creation of dynamic Work Order2
		sWOJsonData = "{\"SVMXC__Order_Status__c\":\"Open\",\"SVMXC__Billing_Type__c\":\"Contract\",\"SVMXC__City__c\":\"Delhi\",\"SVMXC__Zip__c\":\"110003\",\"SVMXC__Country__c\":\"India\",\"SVMXC__State__c\":\"Haryana\"}";
		sWorkOrderID=restServices.restCreate(sWOObejctApi,sWOJsonData);
		sWOSqlQuery ="SELECT+name+from+SVMXC__Service_Order__c+Where+id+=\'"+sWorkOrderID+"\'";				
		sWOName2 =restServices.restGetSoqlValue(sWOSqlQuery,"Name"); //"WO-00000455"; 
		
	}

	@Test(enabled = true)
	public void toTest() throws Exception {
		sTestCaseID = "SANITY5";
		
		sExploreSearch = GenericLib.getExcelData(sTestCaseID, "ExploreSearch");
		sExploreChildSearchTxt = GenericLib.getExcelData(sTestCaseID, "ExploreChildSearch");
		sFieldServiceName = GenericLib.getExcelData(sTestCaseID, "ProcessName");
		sIssueTxt = GenericLib.getExcelData(sTestCaseID, "IssueText");
		sBillingType = GenericLib.getExcelData(sTestCaseID, "BillingType");
		try {
			
			//Pre Login to app
			loginHomePo.login(commonsPo, exploreSearchPo);
			
			//Data Sync for WO's created
			toolsPo.syncData(commonsPo);
			Thread.sleep(GenericLib.iMedSleep);
			
			//Navigation to SFM
			workOrderPo.navigateToWOSFM(commonsPo, exploreSearchPo, sExploreSearch, sExploreChildSearchTxt, sWOName1, sFieldServiceName);
			
			//Validation of not qualifying Work Order
			Assert.assertTrue(workOrderPo.getEleThisRecordDoesNotPopup().isDisplayed(), "Error popup is not displayed");
			NXGReports.addStep("Error popup This is record does not meet is displayed successfully", LogAs.PASSED, null);		
			commonsPo.tap(workOrderPo.getEleOKBtn());
			Thread.sleep(GenericLib.iLowSleep);
		
			//Navigation to SFM
			workOrderPo.navigateToWOSFM(commonsPo, exploreSearchPo, sExploreSearch, sExploreChildSearchTxt, sWOName2, sFieldServiceName);
			commonsPo.tap(workOrderPo.getEleClickSave());
			
			//Validation of qualifying workorder with Issue found text error.
			Assert.assertTrue(workOrderPo.getEleIssueFoundTxt().isDisplayed(), "Issue found error is not displayed");
			NXGReports.addStep("Issue found is displayed successfully", LogAs.PASSED, null);		
			
			//Validation of qualifying workorder with Issue found text popup.
			commonsPo.tap(workOrderPo.getEleIssueFoundTxt());	
			Assert.assertTrue(workOrderPo.getEleIssuePopupTxt(sIssueTxt).isDisplayed(), "Error popup is not displayed");
			NXGReports.addStep("Error popup Issue found is displayed successfully", LogAs.PASSED, null);		
			
			commonsPo.tap(workOrderPo.getEleIssueFoundTxt());
			Thread.sleep(GenericLib.iMedSleep);
			commonsPo.tap(workOrderPo.getEleCancelLnk());
			commonsPo.tap(workOrderPo.getEleDiscardBtn());
			
			//Navigation to WO
			workOrderPo.selectAction(commonsPo, sFieldServiceName);
			Thread.sleep(GenericLib.iMedSleep);
			
			//Selecting Billing Type to loan to make sure sfm is working fine.
			commonsPo.pickerWheel(workOrderPo.getEleBillingTypeLst(), sBillingType);
			commonsPo.tap(workOrderPo.getEleSaveLnk());
			
			//Validation of qualifying workorder with Issue found text error.
			Assert.assertTrue(workOrderPo.getEleSavedSuccessTxt().isDisplayed(), "Saved successfully is not displayed");
			NXGReports.addStep("Saved successfully text is displayed successfully", LogAs.PASSED, null);		
		
			NXGReports.addStep("Testcase " + sTestCaseID + " PASSED", LogAs.PASSED, null);
		} catch (Exception e) {
			NXGReports.addStep("Testcase " + sTestCaseID + " FAILED", LogAs.FAILED,new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
			throw e;
		}

	}
	
	
	
	

}
