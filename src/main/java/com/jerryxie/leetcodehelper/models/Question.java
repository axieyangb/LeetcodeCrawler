package com.jerryxie.leetcodehelper.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.jerryxie.leetcodehelper.utils.GsonConverter;



public class Question {
	public static enum Level {
		Hard, Meidum, Easy
	}

	public static class Solution {
		private String bestSolutionUrl;
		private boolean officialAnswer;

		public boolean isOfficialAnswer() {
			return officialAnswer;
		}

		public void setOfficialAnswer(boolean officialAnswer) {
			this.officialAnswer = officialAnswer;
		}

		public String getOfficialAnswerUrl() {
			return officialAnswerUrl;
		}

		public void setOfficialAnswerUrl(String officialAnswerUrl) {
			this.officialAnswerUrl = officialAnswerUrl;
		}

		private String officialAnswerUrl;

		public String getBestSolutionUrl() {
			return bestSolutionUrl;
		}

		public void setBestSolutionUrl(String bestSolutionUrl) {
			this.bestSolutionUrl = bestSolutionUrl;
		}

		public String getTitleNname() {
			return titleNname;
		}

		public void setTitleNname(String titleNname) {
			this.titleNname = titleNname;
		}

		public int getVotes() {
			return votes;
		}

		public void setVotes(int votes) {
			this.votes = votes;
		}

		public int getViews() {
			return views;
		}

		public void setViews(int views) {
			this.views = views;
		}

		private String titleNname;
		private int votes;
		private int views;
	}

	public Question() {
		solution = new Solution();
		tags = new ArrayList<>();

	}

	public Solution getSolution() {
		return solution;
	}

	public String getQuestionName() {
		return questionName;
	}

	public void setQuestionName(String questionName) {
		this.questionName = questionName;
	}

	public List<String> getTags() {
		return tags;
	}

	public void addTags(String tag) {
		this.tags.add(tag);
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	private Solution solution;
	private String questionName;
	private String questionUrl;
	private int thumbup;
	private int thumbdown;

	public String getQuestionUrl() {
		return questionUrl;
	}

	public void setQuestionUrl(String questionUrl) {
		this.questionUrl = questionUrl;
	}

	public int getThumbup() {
		return thumbup;
	}

	public void setThumbup(int thumbup) {
		this.thumbup = thumbup;
	}

	public int getThumbdown() {
		return thumbdown;
	}

	public void setThumbdown(int thumbdown) {
		this.thumbdown = thumbdown;
	}

	public String toString() {
		return GsonConverter.getInstance().toJsonPretty(this);
	}

	private List<String> tags;
	private int sequenceNum;

	public int getSequenceNum() {
		return sequenceNum;
	}

	public void setSequenceNum(int sequenceNum) {
		this.sequenceNum = sequenceNum;
	}

	private Level level;
	private double acceptRate;
	private String defaultLanguage;
	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public boolean isSolved() {
		return isSolved;
	}

	public void setSolved(boolean isSolved) {
		this.isSolved = isSolved;
	}

	private boolean isSolved;
	private boolean isLocked;
	
	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public double getAcceptRate() {
		return acceptRate;
	}

	public void setAcceptRate(double acceptRate) {
		this.acceptRate = acceptRate;
	}

}
