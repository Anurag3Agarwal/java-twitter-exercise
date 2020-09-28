package com.ing.javaexercise.service;

import com.ing.javaexercise.model.Tweet;
import java.util.List;


public interface TwitterStreamService {

List<Tweet> retrieveAndProcessTweets();
}
