package com.ing.javaexercise.controller;

import com.ing.javaexercise.exception.TweetNotFoundException;
import java.io.IOException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TwitterClientControllerAdvice {

  @ExceptionHandler(TweetNotFoundException.class)
  public String handleException(Model model, TweetNotFoundException ex) {
    model.addAttribute("error", "No tweets found for the search string");
    return "error";
  }

  @ExceptionHandler(IOException.class)
  public String handleException(Model model, Exception ex) {
    model.addAttribute("error", "Please try again!!");
    return "error";
  }


}
