package com.jerryxie.leetcodehelper.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonConverter {
	Gson gson_pretty;
	Gson gson_normal;
	private GsonConverter() {
		gson_pretty = new GsonBuilder().setPrettyPrinting().create();
		gson_normal = new GsonBuilder().create();
	}
	private static class GsonConverterHelper{
		public static final GsonConverter _instance = new GsonConverter();
	}
	public static GsonConverter getInstance() {
		return GsonConverterHelper._instance;
	}
	public <T> String toJsonPretty(T obj) {
		return gson_pretty.toJson(obj).toString();
	}
	public <T> String toJson(T obj) {
		return gson_normal.toJson(obj).toString();
	}
}
