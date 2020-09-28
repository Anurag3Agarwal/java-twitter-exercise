package com.ing.javaexercise.controller;

import com.ing.javaexercise.model.Tweet;
import com.ing.javaexercise.service.TwitterStreamService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 */

@Controller
@RequestMapping("/")
public class TwitterClientController {

  @Autowired
  private TwitterStreamService twitterStreamService;
  @Value("${searchString}")
  private String searchString;


  @GetMapping("tweets")
  public String fetchTweets(Model model) {
    List<Tweet> tweets = twitterStreamService.retrieveAndProcessTweets(searchString);
    model.addAttribute("tweets", tweets);
    model.addAttribute("searchString", searchString);
    return "tweets";
  }

}
