package com.ing.javaexercise.controller;

import com.ing.javaexercise.model.Tweet;
import com.ing.javaexercise.service.TwitterStreamService;
import com.ing.javaexercise.service.TwitterStreamServiceImpl;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to fetch tweets as per business logic
 *
 */

@Controller
@RequestMapping("/")
public class TwitterClientController {

  private static final Logger logger = LoggerFactory.getLogger(TwitterClientController.class);

  @Autowired
  private TwitterStreamService twitterStreamService;

  @Value("${searchString}")
  private String searchString;


  @GetMapping("tweets")
  public String fetchTweets(Model model) {
    logger.debug("Entering fetchTweets method");
    List<Tweet> tweets = twitterStreamService.retrieveAndProcessTweets(searchString);
    model.addAttribute("tweets", tweets);
    model.addAttribute("searchString", searchString);
    logger.debug("Exiting fetchTweets");
    return "tweets";
  }

}
