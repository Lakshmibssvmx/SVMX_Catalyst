package com.ge.fsa.pageobjects;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import com.kirwa.nxgreport.NXGReports;
import com.kirwa.nxgreport.logging.LogAs;

import io.appium.java_client.AppiumDriver;

public class TasksPO {
	
	public TasksPO(AppiumDriver driver)
	{
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	AppiumDriver driver = null;
	
	@FindBy(xpath="//div[.='Tasks']")
	private WebElement eleTasksIcn;
	public WebElement getEleTasksIcn()
	{
		return eleTasksIcn;
	}
	
	@FindBy(xpath="//span[.='Add Task']")
	private WebElement eleAddTasksBtn;
	public WebElement getEleAddTasksBtn()
	{
		return eleAddTasksBtn;
	}
	
	@FindBy(xpath="//span[.='Save']")
	private WebElement eleSaveBtn;
	public WebElement getEleSaveBtn()
	{
		return eleSaveBtn;
	}
	
	@FindBy(xpath="//span[.='High']/following::div[contains(@id,'radioinput')]")
	private WebElement eleHighRadioBtn;
	public WebElement getEleHighRadioBtn()
	{
		return eleHighRadioBtn;
	}
	
	@FindBy(xpath="//span[.='Description']/following::textarea[1]")
	private WebElement eleDescriptionTxtArea;
	public WebElement getEleDescriptionTxtArea()
	{
		return eleDescriptionTxtArea;
	}
	
	@FindBy(xpath="//span[.='Tasks'])[1]")
	private WebElement eleTasksLbl;
	public WebElement getEleTasksLbl()
	{
		return eleTasksLbl;
	}
	
	@FindBy(xpath="//div[@class='tasks-list-item-subject']")
	private List<WebElement> eleInTasksList;
	public List<WebElement> getEleInTasksList() {
		return eleInTasksList;
	}
	
	private WebElement elePriorityIcon;
	public WebElement getElePriorityIcon(String taskName) {
		
		elePriorityIcon = driver.findElement(By.xpath("//div[.='"+taskName+"']/following-sibling::span[contains(@class,'double-exclamation')]"));
		return elePriorityIcon;
	}
	
	
	public void addTask(CommonsPO commonsPo) throws InterruptedException {
		commonsPo.tap(getEleTasksIcn());	
		Assert.assertTrue(getEleTasksLbl().isDisplayed(), "Tasks screen is not displayed");
		NXGReports.addStep("Tasks screen is displayed successfully", LogAs.PASSED, null);
		getEleAddTasksBtn().click();
		String desc = commonsPo.generaterandomnumber("Desc");
		getEleDescriptionTxtArea().sendKeys(desc);
		getEleHighRadioBtn().click();
		getEleSaveBtn().click();
		List<WebElement> tasksList = new ArrayList<WebElement>();
		tasksList = getEleInTasksList();
		Assert.assertTrue(tasksList.contains(desc),"Task was not added successfully to the list");
		NXGReports.addStep("Tasks added successfully", LogAs.PASSED, null);
		Assert.assertTrue(getElePriorityIcon(desc).isDisplayed(),"High Priority Icon is not displayed");
		NXGReports.addStep("High Priority Icon is displayed successfully", LogAs.PASSED, null);
	}
	
	
	

}
