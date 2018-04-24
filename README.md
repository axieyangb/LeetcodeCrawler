# LeetcodeCrawler
A simple crawler to fetch leetcode questions and its corresponding information. Great tool to analyze the question quality and make your coding journey more efficient.

## How to start
```
cd dockerfiles

vi docker-compose.yml 

# change the following two fields
#      - username=<your leetcode's username>
#      - password=<your leetcodes's password>

docker-compose up -d
```


That's it, crawlered data is stored in data folder. Of cause, you can access the mongodb using any thrid party tool (3t sudio) for further analyzing.

### If you want to build the maven project by yourself
```
mvn clean install
export firefox="browsers/geckodriver"
export phantomjs="browsers/phantomjs"
export username="<your leetcode login username>"
export password="<your leetcode login pasword>"
export  mongodb="127.0.0.1"
export mongoport="27017"
java -jar target/LeetCodeHelper-0.0.1-SNAPSHOT.jar
```

### If you want to build the docker image

```
mvn package docker:build
```


**Data format in mongo db**
_take two sum as example_
```
{ 
    "_id" : ObjectId("5add4a7bfe03d93e1f3b171c"), 
    "solution" : {
        "bestSolutionUrl" : "https://leetcode.com/problems/two-sum/discuss/3/Accepted-Java-O(n)-Solution", 
        "officialAnswer" : true, 
        "officialAnswerUrl" : "https://leetcode.com/articles/two-sum", 
        "titleNname" : "Accepted Java O(n) Solution", 
        "votes" : 359, 
        "views" : 200700
    }, 
    "questionName" : "Two Sum", 
    "questionUrl" : "https://leetcode.com/problems/two-sum", 
    "thumbup" : 5314, 
    "thumbdown" : 162, 
    "tags" : [
        "Array", 
        "Hash Table"
    ], 
    "sequenceNum" : 1, 
    "level" : "Easy", 
    "acceptRate" : 38.079717272447134, 
    "defaultLanguage" : "C++", 
    "isSolved" : true, 
    "isLocked" : false
}
```

