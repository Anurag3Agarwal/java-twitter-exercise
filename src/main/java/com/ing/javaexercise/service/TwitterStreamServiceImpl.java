package com.ing.javaexercise.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.ing.javaexercise.authentication.Authenticator;
import com.ing.javaexercise.constants.ApplicationConstant;
import com.ing.javaexercise.exception.TweetNotFoundException;
import com.ing.javaexercise.model.Tweet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


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
  private volatile Map<String, List<Tweet>> tweetByUser;

  /**
   * Tweet list to be returned
   */
  private volatile List<Tweet> tweetList = new ArrayList<>();

  /**
   * @param searchString: The string for which the tweets are to be streamed
   * @return tweetList: List of tweets fetch and sorted as per business requirement
   */

  @Override
  public List<Tweet> retrieveAndProcessTweets(String searchString) throws IOException,TweetNotFoundException {
    HttpRequestFactory httpRequestFactory = authenticator.getAuthorizedHttpRequestFactory();
    try {
      setMapOfTweetsSortedAndGroupedByUserChronologically(
          retrieveAndParseTweetsFromTwitterStream(httpRequestFactory, searchString));
      if (!CollectionUtils.isEmpty(tweetByUser)) {
        printFinalSortedTweets();
      } else {
        throw new TweetNotFoundException("No Tweets fetched for the search String");
      }

    } catch (IOException e) {
      logger.error(e.getMessage());
      throw e;
    }

//to do
    return tweetList;
  }


  private List<Tweet> retrieveAndParseTweetsFromTwitterStream(HttpRequestFactory httpRequestFactory,
      String searchString) throws IOException {
    logger.debug("Entering retrieveAndParseTweetsFromTwitterStream...");
    List<Tweet> tweetList = new ArrayList<>();
    HttpRequest request = httpRequestFactory.buildGetRequest(
        new GenericUrl(
            ApplicationConstant.ENDPOINT_STREAM_TWITTER.concat("?track=").concat(searchString)));
    HttpResponse response = request.execute();
    InputStream in = response.getContent();
    ObjectMapper mapper = new ObjectMapper();
    DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
    mapper.setDateFormat(dateFormat);
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
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
    logger.debug("Exiting retrieveAndParseTweetsFromTwitterStream!!");
    return tweetList;
  }

  /**
   * Gets the map of tweets group by user.
   *
   * @param tweetList Generated Tweet List.
   */
  private void setMapOfTweetsSortedAndGroupedByUserChronologically(List<Tweet> tweetList) {
    logger.debug("Entering setMapOfTweetsSortedAndGroupedByUserChronologically...");
    tweetByUser = tweetList.stream()
        .sorted(Comparator
            .nullsLast((tweet, atweet) -> tweet.getAuthor().compareTo(atweet.getAuthor())))
        .filter(tweet -> null != tweet.getAuthor().getUserId())
        .collect(Collectors.groupingBy(
            tweet -> tweet.getAuthor().getUserId()));

    for (List<Tweet> userTweetList : tweetByUser.values()) {
      userTweetList.sort((Comparator.naturalOrder()));
    }

    logger.debug("Exiting setMapOfTweetsSortedAndGroupedByUserChronologically!!");
  }

  /**
   * Method to print the output to the console
   */

  private void printFinalSortedTweets() {
    logger.debug("Entering printFinalSortedTweets...");
    logger.info("Final output: ");
    for (List<Tweet> userTweetList : tweetByUser.values()) {
      for (Tweet tweet : userTweetList) {
        logger.info(tweet.toString());
        tweetList.add(tweet);

      }
    }

    logger.info("Exiting printFinalSortedTweets!!");
  }

}
