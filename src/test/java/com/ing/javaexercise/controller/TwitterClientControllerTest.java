package com.ing.javaexercise.controller;

import com.ing.javaexercise.exception.TweetNotFoundException;
import com.ing.javaexercise.service.TwitterStreamService;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

@RunWith(MockitoJUnitRunner.class)
public class TwitterClientControllerTest {

  @Spy
  @InjectMocks
  private TwitterClientController twitterClientController;

  @Mock
  private Model model;

  @Mock
  private TwitterStreamService twitterStreamService;

  @Test
  public void fetchTweetsTest() throws IOException, TweetNotFoundException {
    Mockito.when(twitterStreamService.retrieveAndProcessTweets(Mockito.anyString()))
        .thenReturn(Mockito.anyList());
    Assert.assertEquals(twitterClientController.fetchTweets(model), "tweets");
  }

}
