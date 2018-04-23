package com.jerryxie.leetcodehelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.jerryxie.leetcodehelper.models.Question;
import com.jerryxie.leetcodehelper.models.Question.Level;
import com.jerryxie.leetcodehelper.utils.DriverGenerator;

public class Parser {
	Logger logger = Logger.getLogger(Parser.class);
	private WebDriver driver;
	private JavascriptExecutor js;
	private HashMap<Integer,Boolean> visitedQuestions;
	WebDriverWait wait_5;
	WebDriverWait wait_20;
	private String username;
	private String password;
	MongoDBAccess dbAccess;

	public Parser() {
		username = System.getProperty("username","");
		password = System.getProperty("password","");
		initDriver();
		dbAccess = new MongoDBAccess();
		visitedQuestions = dbAccess.getVisitedQuestionSeqs();
		System.out.println("Current " + visitedQuestions.size() + " questions have been visited");
	}

	private void initDriver() {
		driver = DriverGenerator.FirefoxDriver();
		driver.manage().window().setSize(new Dimension(1920, 1080));
		js = (JavascriptExecutor) driver;
		wait_5 = new WebDriverWait(driver, 20);
		wait_20 = new WebDriverWait(driver, 20);
	}

	public WebDriver getDriver() {
		return this.driver;
	}

	public void navigate(String url) {
		driver.navigate().to(url);
		this.js.executeScript(String.format("window.localStorage.setItem('%s','%s');", "problem-list:itemsPerPage",
				"9007199254740991"));
		this.js.executeScript(String.format("window.localStorage.setItem('%s','%s');",
				"problem-list:itemsPerPage-updated-time", "9007199254740991", System.currentTimeMillis()));
		driver.navigate().to(url);
	}

	public void signin() {
		WebElement signinBox = driver.findElement(By.cssSelector("a.sign-in-btn"));
		js.executeScript("window.scrollTo(0," + signinBox.getLocation().y + ")");
		signinBox.click();
		WebElement usernameBox = driver.findElement(By.cssSelector("input[name='login']"));
		WebElement passwordBox = driver.findElement(By.cssSelector("input[name='password']"));
		usernameBox.sendKeys(username);
		passwordBox.sendKeys(password);
		WebElement submitBtn = driver.findElement(By.cssSelector("button[name='signin_btn']"));
		submitBtn.click();
	}

	public void releaseMemory() {
		driver.close();
		initDriver();
		driver.navigate().to("https://leetcode.com/problemset/all/");
		signin();
	}

	public List<Question> getQuestions() {
		List<Question> questions = new ArrayList<>();

		WebElement table = wait_20.until(ExpectedConditions.visibilityOfElementLocated(By.className("reactable-data")));
		List<WebElement> rows = table.findElements(By.tagName("tr"));
		int nth = 0;
		for (WebElement row : rows) {
			System.out.println("Loading " + (nth++) + "/" + rows.size() + "...");
			Boolean visited = false;
			List<WebElement> cols = row.findElements(By.tagName("td"));
			Question q = new Question();
			for (int i = 0; i < cols.size(); i++) {
				if (i == 0) {
					List<WebElement> checked = cols.get(i).findElements(By.cssSelector("span.fa-check"));
					if (checked.size() > 0) {
						q.setSolved(true);
					}
				} 
				else if (i == 1) {
					String seqStr = cols.get(i).getText();
					int seq = Integer.parseInt(seqStr);
					if (visitedQuestions.containsKey(seq)) {
						visited = true;
						if(q.isSolved() != visitedQuestions.get(seq)) {
							dbAccess.updateSolvedStatus(seq, q.isSolved());
						}
						break;
					}
					q.setSequenceNum(seq);
				}

				else if (i == 2) {
					String questionName = cols.get(i).getAttribute("value");
					String href_postfix = cols.get(i).findElement(By.tagName("a")).getAttribute("href");
					List<WebElement> locks = cols.get(i).findElements(By.cssSelector("i.fa-lock"));
					if (locks.size() > 0) {
						q.setLocked(true);
					}
					q.setQuestionName(questionName);
					q.setQuestionUrl(href_postfix);
				} else if (i == 3) {
					List<WebElement> officialAnswer = cols.get(i).findElements(By.tagName("a"));
					if (officialAnswer.size() > 0) {
						q.getSolution().setOfficialAnswer(true);
						String officialAnswerUrl = officialAnswer.get(0).getAttribute("href");
						q.getSolution().setOfficialAnswerUrl(officialAnswerUrl);
					}
				} else if (i == 4) {
					String acceptRateStr = cols.get(i).getAttribute("value");
					double acceptRate = Double.parseDouble(acceptRateStr);
					q.setAcceptRate(acceptRate);
				} else if (i == 5) {
					String difficulty = cols.get(i).getText();
					if (difficulty.toLowerCase().equals("hard")) {
						q.setLevel(Level.Hard);
					} else if (difficulty.toLowerCase().equals("medium")) {
						q.setLevel(Level.Meidum);
					} else {
						q.setLevel(Level.Easy);
					}
				}

			}
			if (visited) {
				continue;
			}

			questions.add(q);
		}
		int visitedCnt = 0;
		for (Question q : questions) {
			if (visitedCnt++ % 50 == 0) {
				releaseMemory();
			}
			System.out.println("Inspecting the " + q.getSequenceNum() + " nth question");
			if (q.isLocked()) {
				dbAccess.insertQuestion(q);
				System.out.println("Successfuly parsed [locked question] :" + q.getQuestionName());
				continue;
			}
			int retries = 5;
			boolean succeed = false;
			while (!succeed && retries-- > 0) {
				if (retries < 4) {
					System.out.println("retrying " + retries + "....");
				}
				succeed = grabDetail(q);
			}
			if (succeed) {
				System.out.println("Successfuly parsed [unlocked question] :" + q.getQuestionName());
				dbAccess.insertQuestion(q);
			} else {
				System.out.println("Skipped  " + q.getQuestionName());
			}

		}
		driver.close();
		return questions;
	}

	public boolean grabDetail(Question q) {
		navigate(q.getQuestionUrl());

		try {
			WebElement languageSelect = wait_20
					.until(ExpectedConditions.visibilityOfElementLocated(By.id("react-select-2--value-item")));
			String selectLanguage = languageSelect.getText();
			q.setDefaultLanguage(selectLanguage);

			List<WebElement> likeSections = driver.findElements(By.cssSelector("div.like-buttons"));
			if(likeSections.size()>0) {
				WebElement likeSection = likeSections.get(0);
				WebElement thumbupBtn = likeSection.findElement(By.cssSelector("div.text-success"));
				String upStr = thumbupBtn.getText();
				WebElement thumbupDown = driver.findElement(By.cssSelector("div.text-danger"));
				String downStr = thumbupDown.getText();
				q.setThumbup(Integer.parseInt(upStr.trim()));
				q.setThumbdown(Integer.parseInt(downStr.trim()));
			}

			// System.out.println(upStr+":" +downStr );

			List<WebElement> relatedTopics = driver.findElements(By.cssSelector("a#show-tags-btn-topics"));
			if (relatedTopics.size() > 0) {
				WebElement relatedTopic = relatedTopics.get(0);
				js.executeScript("window.scrollTo(0," + relatedTopic.getLocation().y + ")");
				relatedTopic.click();
				List<WebElement> tags = wait_20
						.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#tags-topics")))
						.findElements(By.cssSelector("a.sidebar-tag"));
				for (WebElement tag : tags) {
					String tagName = tag.getText();
					// System.out.println(tagName);
					q.addTags(tagName);
				}
			}
			findBestSolutionInDiscuss(q);
		} catch (Exception ex) {
			System.out.println("######## Error ##############");
			System.out.println(ex);
			return false;
		}
		return true;
	}

	private void findBestSolutionInDiscuss(Question q) {
		WebElement naviSection = driver.findElement(By.cssSelector("nav.tab-view"));
		List<WebElement> items = naviSection.findElements(By.tagName("a"));
		WebElement discussionBtn = items.get(3);
		js.executeScript("window.scrollTo(0," + discussionBtn.getLocation().y + ")");
		discussionBtn.click();
		WebElement discussList = wait_20
				.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.discuss-list")));
		List<WebElement> discussItems = discussList.findElements(By.cssSelector("div.discuss-item"));
		if (discussItems.size() > 0) {
			WebElement item = discussItems.get(0);
			WebElement titleLink = item.findElement(By.cssSelector("div.title")).findElement(By.tagName("a"));
			String title = titleLink.getText();
			String url = titleLink.getAttribute("href");
			List<WebElement> votes = item.findElements(By.cssSelector("div.votes"));
			String voteStr = votes.get(0).findElement(By.cssSelector("div.value")).getText();
			List<WebElement> views = item.findElements(By.cssSelector("div.views"));
			String viewStr = views.get(0).findElement(By.cssSelector("div.value")).getText();
			viewStr = viewStr.trim();
			voteStr = voteStr.trim();
			char lastFlag = viewStr.charAt(viewStr.length() - 1);
			double viewCnt = 0;
			if (!Character.isDigit(lastFlag)) {
				viewStr = viewStr.substring(0, viewStr.length() - 1);
				viewCnt = Double.parseDouble(viewStr);
				if (Character.toLowerCase(lastFlag) == 'k') {
					viewCnt *= 1000;
				} else if (Character.toLowerCase(lastFlag) == 'm') {
					viewCnt *= 1000000;
				}
			} else {
				viewCnt = Double.parseDouble(viewStr);
			}

			double voteCnt = 0;
			char lastFlag_v = voteStr.charAt(voteStr.length() - 1);
			if (!Character.isDigit(lastFlag_v)) {
				voteStr = voteStr.substring(0, voteStr.length() - 1);
				voteCnt = Double.parseDouble(voteStr);
				if (Character.toLowerCase(lastFlag_v) == 'k') {
					voteCnt *= 1000;
				} else if (Character.toLowerCase(lastFlag_v) == 'm') {
					voteCnt *= 1000000;
				}
			} else {
				voteCnt = Double.parseDouble(voteStr);
			}

			q.getSolution().setTitleNname(title);
			q.getSolution().setVotes((int) voteCnt);
			q.getSolution().setViews((int) viewCnt);
			q.getSolution().setBestSolutionUrl(url);
		}
	}


}
