# LeetcodeCrawler
A simple crawler to fetch leetcode questions and its corresponding information. Great tool to analyse and further filtering.

#How to start
run mongo db:
cd mongodb && docker-compose up -d
cd .. && mvn clean install
cd target && java -jar -Dusername=<your leetcode's username> -Dpassword=<your leetcode's password> H1bCrawler-1.0-SNAPSHOT.jar
