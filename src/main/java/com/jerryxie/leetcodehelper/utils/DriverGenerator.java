package com.jerryxie.leetcodehelper.utils;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.jerryxie.leetcodehelper.Constants;

public class DriverGenerator {
	static Logger logger = Logger.getLogger(DriverGenerator.class);
	public static WebDriver GetFirefoxDriver() {
		System.setProperty("webdriver.gecko.driver", Constants.FIREFOX_DRIVER);
		return new FirefoxDriver();
	}

	public static WebDriver FirefoxDriver(String geckodriverPath) {
		System.setProperty("webdriver.gecko.driver", geckodriverPath);
		return new FirefoxDriver();
	}
	
	public static WebDriver getDriver() {
		System.out.println("Firefox:" + Constants.FIREFOX_DRIVER);
		System.out.println("Phantomjs: "+ Constants.PHANTOM_JS_DRIVER);
		if(Constants.FIREFOX_DRIVER != null) {
			return  GetFirefoxDriver();
		}
		else if(Constants.PHANTOM_JS_DRIVER !=null) {
			return GetPhantomJSDriver();
		}
		else {
			logger.error("Not given any web driver to crawl");
			System.exit(1);
			return null;
		}
	
	}

	// without ui
	public static WebDriver GetPhantomJSDriver() {
		System.setProperty("phantomjs.binary.path", Constants.PHANTOM_JS_DRIVER);
		ArrayList<String> cliArgsCap = new ArrayList<String>();
		cliArgsCap.add("--proxy-type=none");
		DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
		capabilities.setCapability(
		    PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
		return new PhantomJSDriver(capabilities);
	}

	public static WebDriver PhantomJSDriver(String phantomjsPath) {
		System.setProperty("phantomjs.binary.path", phantomjsPath);
		return new PhantomJSDriver();
	}

}
