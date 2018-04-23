package com.jerryxie.leetcodehelper;

import java.util.HashMap;
import java.util.HashSet;

import org.bson.Document;

import com.jerryxie.leetcodehelper.models.Question;
import com.jerryxie.leetcodehelper.utils.GsonConverter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.UpdateResult;

public class MongoDBAccess {
	protected MongoClient mClient;
	protected MongoDatabase db;

	public MongoDBAccess() {
		String mongoAddr = System.getProperty("mongodb", "localhost");
		String mongoPort = System.getProperty("mongoport", "27017");
		mClient = MongoClients.create("mongodb://" + mongoAddr + ":" + mongoPort);
		db = mClient.getDatabase("leetcode");
	}

	public void insertQuestion(Question question) {
		String jStr = GsonConverter.getInstance().toJson(question);
		Document doc = Document.parse(jStr);
		MongoCollection<Document> coll = db.getCollection("Questions");
		coll.insertOne(doc);
	}

	public HashMap<Integer, Boolean> getVisitedQuestionSeqs() {
		HashMap<Integer, Boolean> ret = new HashMap<>();
		MongoCollection<Document> coll = db.getCollection("Questions");
		MongoCursor<Document> cursor = coll.find().projection(Projections.include("sequenceNum", "isSolved"))
				.iterator();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			int seqNum = doc.getInteger("sequenceNum");
			boolean isSolved = doc.getBoolean("isSolved");
			ret.put(seqNum, isSolved);
		}
		return ret;
	}

	public long updateSolvedStatus(int seqNum, boolean isSolved) {
		MongoCollection<Document> coll = db.getCollection("Questions");
		BasicDBObject filter = new BasicDBObject("sequenceNum", seqNum);
		BasicDBObject updateIsSolved = new BasicDBObject("isSolved", true);
		BasicDBObject set = new BasicDBObject("$set", updateIsSolved);
		UpdateResult ur = coll.updateOne(filter, set);
		return ur.getModifiedCount();
	}
}
