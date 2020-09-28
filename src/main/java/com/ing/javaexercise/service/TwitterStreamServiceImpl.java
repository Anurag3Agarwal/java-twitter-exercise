package com.ing.javaexercise.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.ing.javaexercise.authentication.Authenticator;
import com.ing.javaexercise.constants.ApplicationConstant;
import com.ing.javaexercise.model.Author;
import com.ing.javaexercise.model.Tweet;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class TwitterStreamServiceImpl implements TwitterStreamService {

  @Autowired
  private Authenticator authenticator;

  private static final Logger logger = LoggerFactory.getLogger(TwitterStreamServiceImpl.class);

  /**
   * Max number of tweets
   */
  private static final int MAX_TWEET_SIZE = 100;



  /**
   * Max interval of time
   */
  private static final long MAX_TIME_INTERVAL = 30000;

  /**
   * Map of tweets group by User
   */
  private Map<String, List<Tweet>> tweetByUser = new HashMap<>();

  @Value("${searchString}")
  private String host;

  @Override
  public List<Tweet> retrieveAndProcessTweets() {
    HttpRequestFactory httpRequestFactory = authenticator.getAuthorizedHttpRequestFactory();
    try {
      setMapOfTweetsSortedByUserChronologycally(parseInputStreamToTweetList(httpRequestFactory));
    } catch (IOException e) {
      e.printStackTrace();
    }

    printSortedTweets();

//to do
    return null;
  }


  private List<Tweet> parseInputStreamToTweetList(HttpRequestFactory httpRequestFactory) throws IOException {
    logger.info("Init parseInputStreamToTweetList...");
    List<Tweet> tweetList = new ArrayList<>();


    HttpRequest request = httpRequestFactory.buildGetRequest(
        new GenericUrl(ApplicationConstant.ENDPOINT_STREAM_TWITTER.concat("?track=").concat(host)));

    HttpResponse response = request.execute();

    InputStream in = response.getContent();

    ObjectMapper mapper = new ObjectMapper();
    DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
    mapper.setDateFormat(dateFormat);

    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//    LineIterator lineIterator = new LineIterator(
//        new BufferedReader(new InputStreamReader(in), 1024 * 1024 * 10));
    String line = reader.readLine();

    int countTweets = 0;
    long startTime = System.currentTimeMillis();

    while (line != null && countTweets < MAX_TWEET_SIZE &&
        (System.currentTimeMillis() - startTime < MAX_TIME_INTERVAL)) {
      // Parse tweet and add to the list
      tweetList.add(mapper.readValue(line, Tweet.class));

      line = reader.readLine();
      countTweets++;
      logger.info("Number of Input Tweets: " + countTweets);
    }

    logger.info("End parseInputStreamToTweetList!!");
    return tweetList;
  }

  /**
   * Gets the map of tweets group by user.
   *
   * @param tweetList Generated Tweet List.
   */
  private void setMapOfTweetsSortedByUserChronologycally(List<Tweet> tweetList) {
    logger.info("Init setMapOfTweetsSortedByUserChronologycally...");
    tweetByUser = tweetList.stream()
       // .sorted(Comparator.nullsLast((p1,p2) -> p1.getAuthor().getCreationDate().compareTo(p2.getAuthor().getCreationDate())))
        .sorted(Comparator.nullsLast((p1,p2)-> p1.getAuthor().compareTo(p2.getAuthor())))
        .collect(Collectors.groupingBy(
            p -> p.getAuthor().getUserId()));




    for (List<Tweet> userTweetList : tweetByUser.values()) {
      userTweetList.sort((p1, p2) -> p1.compareTo(p2));
    }

    logger.info("End setMapOfTweetsSortedByUserChronologycally!!");
  }

  private void printSortedTweets() {
    logger.info("Init printSortedTweets...");


    logger.info("Final tweet list: ");
    for (List<Tweet> userTweetList : tweetByUser.values()) {
      for(Tweet tweet: userTweetList) {
        logger.info(tweet.toString());
      }
    }

    logger.info("End printSortedTweets!!");
  }

}
