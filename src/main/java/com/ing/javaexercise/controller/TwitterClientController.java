package com.ing.javaexercise.controller;

import com.google.j2objc.annotations.AutoreleasePool;
import com.ing.javaexercise.service.TwitterStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class TwitterClientController {

  @Autowired
  private TwitterStreamService twitterStreamService;

//  @Autowired
//  private

  @RequestMapping("tweets")
  public String fetchTweets() {
    twitterStreamService.retrieveAndProcessTweets();
    // to do
    System.out.println("Controller called");
    return "Controller called";
  }

}
