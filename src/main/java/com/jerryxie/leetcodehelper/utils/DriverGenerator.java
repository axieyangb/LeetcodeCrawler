package com.jerryxie.leetcodehelper.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public class DriverGenerator {
	public static WebDriver FirefoxDriver() {
		System.setProperty("webdriver.gecko.driver", "src/main/resources/webdrivers/geckodriver");
		return new FirefoxDriver();
	}

	public static WebDriver FirefoxDriver(String geckodriverPath) {
		System.setProperty("webdriver.gecko.driver", geckodriverPath);
		return new FirefoxDriver();
	}

	// without ui
	public static WebDriver PhantomJSDriver() {
		System.setProperty("phantomjs.binary.path", "src/main/resources/webdrivers/phantomjs");
		return new PhantomJSDriver();
	}

	public static WebDriver PhantomJSDriver(String phantomjsPath) {
		System.setProperty("phantomjs.binary.path", phantomjsPath);
		return new PhantomJSDriver();
	}

}
