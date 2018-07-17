/*
 *  @author MeghanaRao
 */
package com.ge.fsa.pageobjects;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

import com.ge.fsa.lib.BaseLib;
import com.ge.fsa.lib.GenericLib;
import com.ge.fsa.lib.RestServices;
import com.kirwa.nxgreport.NXGReports;
import com.kirwa.nxgreport.logging.LogAs;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;

public class CalendarPO 
{
	public CalendarPO(AppiumDriver driver)
	{
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	AppiumDriver driver = null;
	TouchAction touchAction = null;
	
	@FindBy(xpath="//div[@class='svmx-menu-icon-label'][text()='Calendar']")
	private WebElement elecalendarClick;
	public WebElement getEleCalendarClick()
	{
		return elecalendarClick;
	}
	
	private WebElement eleProductNameTxt;
	public WebElement getEleProductNameTxt(String sProductName)
	{
		eleProductNameTxt=driver.findElement(By.xpath("//div[@class='x-inner-el'][text()='"+sProductName+"']"));
		return eleProductNameTxt;
	}
	
	private WebElement eleworkordernumonCalendar;
	public WebElement getEleworkordernumonCalendar(String sWorkOrdernumber)
	{
		eleworkordernumonCalendar=driver.findElement(By.xpath("//div[contains(@class, 'sfmevent-title')][text()='"+sWorkOrdernumber+"']"));
		return eleworkordernumonCalendar;
	}
	
	/**
	 * Author - Meghana Rao
	 * @param commonsPo - Passing CommonsPO Function
	 * @param workordername - Passing the Work Order number
	 * @throws Exception - Throwing Required Exception
	 */
	public void verifyworkorderCalendar(CommonsPO commonsPo, String workordername) throws Exception 
	{
		commonsPo.tap(getEleCalendarClick());
		Thread.sleep(2000);
		

			if(getEleworkordernumonCalendar(workordername) != null){
				
				commonsPo.tap(getEleworkordernumonCalendar(workordername));
				
			}
			else
			{
				throw new Exception("WorkOrder not found on the Calendar");
				
			}
		

	}
	
}