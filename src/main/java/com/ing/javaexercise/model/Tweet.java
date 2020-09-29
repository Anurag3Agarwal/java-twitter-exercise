package com.ing.javaexercise.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ing.javaexercise.constants.ApplicationConstant;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tweet implements Comparable {

    @JsonProperty(ApplicationConstant.MESSAGE_ID)
    private String messageId;

    @JsonProperty(ApplicationConstant.CREATED_AT)
    private Date creationDate;

    @JsonProperty(ApplicationConstant.TEXT)
    private String messageText;

    @JsonProperty(ApplicationConstant.AUTHOR)
    private Author author;

    public String getMessageId() {
    return messageId;
  }

    public Date getCreationDate() {
    return creationDate;
  }

    public String getMessageText() {
    return messageText;
  }

    public Author getAuthor() {
      return (null != author) ?  author : new Author();
  }

    public int compareTo(Object o) {
    final int EQUAL = 0;
    int result;

    Tweet oTweet = (Tweet) o;

    if(this == oTweet)
      result = EQUAL;
    else
      result = this.getCreationDate().compareTo(oTweet.getCreationDate());

    return result;
  }

    @Override
    public String toString(){
    return "Tweet: [" +
        ApplicationConstant.MESSAGE_ID + ": " + messageId + "," +
        ApplicationConstant.CREATED_AT + ": " + creationDate + "," +
        ApplicationConstant.TEXT + ": " + messageText + "," +
        ApplicationConstant.AUTHOR + ": " + author.toString() +
        "]";
  }

}
