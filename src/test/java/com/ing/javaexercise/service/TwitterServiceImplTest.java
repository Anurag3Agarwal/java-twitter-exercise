package com.ing.javaexercise.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.json.Json;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.ing.javaexercise.authentication.Authenticator;
import com.ing.javaexercise.exception.TweetNotFoundException;
import com.ing.javaexercise.model.Tweet;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TwitterServiceImplTest {

  @Spy
  @InjectMocks
  private TwitterStreamServiceImpl twitterStreamService;

  @Mock
  private Authenticator authenticator;

  @Test(expected = IOException.class)
  public void retrieveAndParseTweetsFromTwitterStreamTestIOException()
      throws IOException, TweetNotFoundException {
    HttpTransport transport = new MockHttpTransport() {
      @Override
      public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
        return new MockLowLevelHttpRequest() {
          @Override
          public LowLevelHttpResponse execute() throws IOException {
            throw new IOException("Test IO exception");
          }
        };
      }
    };
    HttpRequestFactory requestFactory = transport.createRequestFactory();
    when(authenticator.getAuthorizedHttpRequestFactory()).thenReturn(requestFactory);

    twitterStreamService.retrieveAndProcessTweets("test");

  }

  @Test(expected = TweetNotFoundException.class)
  public void retrieveAndParseTweetsTestTweetNotFoundException()
      throws IOException, TweetNotFoundException {

    HttpTransport transport = new MockHttpTransport() {
      @Override
      public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
        return new MockLowLevelHttpRequest() {
          @Override
          public LowLevelHttpResponse execute() throws IOException {
            MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
            response.addHeader("custom_header", "value");
            response.setStatusCode(200);
            response.setContentType(Json.MEDIA_TYPE);
            response.setContent("");
            return response;
          }
        };
      }
    };
    HttpRequestFactory requestFactory = transport.createRequestFactory();
    when(authenticator.getAuthorizedHttpRequestFactory()).thenReturn(requestFactory);
    twitterStreamService.retrieveAndProcessTweets("test");

  }

  @Test
  public void retrieveAndParseTweetsTestSuccess() throws IOException, TweetNotFoundException {
    HttpTransport transport = new MockHttpTransport() {
      @Override
      public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
        return new MockLowLevelHttpRequest() {
          @Override
          public LowLevelHttpResponse execute() throws IOException {
            MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
            response.addHeader("custom_header", "value");
            response.setStatusCode(200);
            response.setContentType(Json.MEDIA_TYPE);
            response.setContent(
                "{\"id\":\"1221\",\"created_at\":\"Sat Sep 26 02:14:23 +0000 2020\",\"text\":\"test data\",\"user\":{\"id\":\"testing\"}}");

            return response;
          }
        };
      }
    };

    HttpRequestFactory requestFactory = transport.createRequestFactory();
    when(authenticator.getAuthorizedHttpRequestFactory()).thenReturn(requestFactory);
    Assert.assertThat((twitterStreamService.retrieveAndProcessTweets("test")).size(), is(1));

  }

  @Test
  public void retrieveAndParseTweetsGroupByAuthorandChronoOrderTest()
      throws IOException, TweetNotFoundException {
    HttpTransport transport = new MockHttpTransport() {
      @Override
      public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
        return new MockLowLevelHttpRequest() {
          @Override
          public LowLevelHttpResponse execute() throws IOException {
            MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
            response.addHeader("custom_header", "value");
            response.setStatusCode(200);
            response.setContentType(Json.MEDIA_TYPE);
            response.setContent(
                "{\"id\":\"1221\",\"created_at\":\"Sat Sep 26 02:14:23 +0000 2020\",\"text\":\"test data\",\"user\":{\"id\":\"tester1\",\"created_at\":\"Sat Sep 26 02:14:20 +0000 2020\"}}\n"
                    + "{\"id\":\"1222\",\"created_at\":\"Sat Sep 26 02:14:20 +0000 2020\",\"text\":\"test data\",\"user\":{\"id\":\"tester2\",\"created_at\":\"Sat Sep 26 02:14:25 +0000 2020\"}}\n"
                    + "{\"id\":\"1223\",\"created_at\":\"Sat Sep 26 02:14:19 +0000 2020\",\"text\":\"test data\",\"user\":{\"id\":\"tester1\",\"created_at\":\"Sat Sep 26 02:14:20 +0000 2020\"}}\n"
                    + "{\"id\":\"1224\",\"created_at\":\"Sat Sep 26 02:14:19 +0000 2020\",\"text\":\"test data\",\"user\":{\"id\":\"tester3\",\"created_at\":\"Sat Sep 26 02:14:23 +0000 2020\"}}\n");

            return response;
          }
        };
      }
    };
    HttpRequestFactory requestFactory = transport.createRequestFactory();
    when(authenticator.getAuthorizedHttpRequestFactory()).thenReturn(requestFactory);
    List<Tweet> tweetList = twitterStreamService.retrieveAndProcessTweets("test");
    Assert.assertThat(tweetList.size(), is(4));
    //Asserting author chronological order
    Assert.assertThat(tweetList.get(0).getAuthor().getCreationDate()
        .before(tweetList.get(3).getAuthor().getCreationDate()), is(true));
    //Asserting tweet chronological order for same author
    Assert.assertThat(tweetList.get(0).getAuthor().getUserId(),
        equalTo(tweetList.get(1).getAuthor().getUserId()));
    Assert.assertThat(tweetList.get(0).getCreationDate().before(tweetList.get(1).getCreationDate()),
        is(true));

  }

}
