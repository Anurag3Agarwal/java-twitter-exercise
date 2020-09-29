package com.ing.javaexercise.service;

import com.ing.javaexercise.exception.TweetNotFoundException;
import com.ing.javaexercise.model.Tweet;
import java.io.IOException;
import java.util.List;


public interface TwitterStreamService {

List<Tweet> retrieveAndProcessTweets(String searchString) throws IOException, TweetNotFoundException;
}
