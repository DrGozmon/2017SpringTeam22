package com.emc.metalnx.irods.test.Selenium;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class LoginTest {
	
	private String baseUrl;

	@Test
	public void testLogin() {
		WebDriver driver = new HtmlUnitDriver();
		baseUrl = "http://sd-vm15.csc.ncsu.edu:8080/emc-metalnx-web/";
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "login/");
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("rods");
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("irods");
		driver.findElement(By.name("password")).sendKeys(Keys.RETURN);
		assertEquals("http://sd-vm15.csc.ncsu.edu:8080/emc-metalnx-web/dashboard/", driver.getCurrentUrl().toString());
	}
	
	@Test
	public void testRulesPage() {
		WebDriver driver = new HtmlUnitDriver();
		baseUrl = "http://sd-vm15.csc.ncsu.edu:8080/emc-metalnx-web/";
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "login/");
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("rods");
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("irods");
		driver.findElement(By.name("password")).sendKeys(Keys.RETURN);
		
		driver.findElement(By.partialLinkText("Rules")).click();
		assertEquals("http://sd-vm15.csc.ncsu.edu:8080/emc-metalnx-web/rules/", driver.getCurrentUrl().toString());
	}
	
	@Test
	public void testUploadPrompt() {
		WebDriver driver = new HtmlUnitDriver();
		baseUrl = "http://sd-vm15.csc.ncsu.edu:8080/emc-metalnx-web/";
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "login/");
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("rods");
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("irods");
		driver.findElement(By.name("password")).sendKeys(Keys.RETURN);
		
		driver.findElement(By.partialLinkText("Rules")).click();
		
		driver.findElement(By.className("btn-primary")).click();
		assertEquals(driver.findElement(By.id("browseButton")) != null, true);
	}

}
