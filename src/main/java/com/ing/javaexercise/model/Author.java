package com.ing.javaexercise.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ing.javaexercise.constants.ApplicationConstant;
import java.util.Comparator;
import java.util.Date;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Author implements Comparable{

  @JsonProperty(ApplicationConstant.USER_ID)
  private String userId;

  @JsonProperty(ApplicationConstant.CREATED_AT)
  private Date creationDate;

  @JsonProperty(ApplicationConstant.NAME)
  private String name;

  @JsonProperty(ApplicationConstant.SCREEN_NAME)
  private String screenName;

  public String getUserId() {
    return userId;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public String getName() {
    return name;
  }

  public String getScreenName() {
    return screenName;
  }

  @Override
  public String toString(){
    return "Author: [" +
        ApplicationConstant.USER_ID + ": " + userId + "," +
        ApplicationConstant.CREATED_AT + ": " + creationDate + "," +
        ApplicationConstant.NAME + ": " + name + "," +
        ApplicationConstant.SCREEN_NAME + ": " + screenName +
        "]";
  }

  private static Comparator<Date> nullSafeDateComparator = Comparator
      .nullsLast(Date::compareTo);

  @Override
  public int compareTo(Object o) {
    Author author = (Author) o;
    return nullSafeDateComparator.compare(this.getCreationDate(), author.getCreationDate());

  }
}
