package com.jerryxie.leetcodehelper;

import java.util.List;

import com.jerryxie.leetcodehelper.models.Question;

public class Entry {
	public static void main(String [] args) {
		Parser p = new Parser();
		p.navigate("https://leetcode.com/problemset/all/");
		p.signin();
		p.getQuestions();
	}

}
