package com.devbliss.gpullr.domain;

public class RankingData {

  private Integer sumOfLinesRemoved;

  private Integer sumOfLinesAdded;

  private Integer sumOfFilesChanged;

  private Integer sumOfComments;

  private Integer closedCount;

  public boolean isValid() {
    return sumOfComments != null
        && sumOfFilesChanged != null
        && sumOfLinesAdded != null
        && sumOfComments != null
        && closedCount != null;
  }

  public int getSumOfLinesRemoved() {
    return sumOfLinesRemoved;
  }

  public void addToSumOfLinesRemoved(int sumOfLinesRemoved) {
    this.sumOfLinesRemoved = addOrSet(this.sumOfLinesRemoved, sumOfLinesRemoved);
  }

  public int getSumOfLinesAdded() {
    return sumOfLinesAdded;
  }

  public void addToSumOfLinesAdded(Integer sumOfLinesAdded) {
    this.sumOfLinesAdded = addOrSet(this.sumOfLinesAdded, sumOfLinesAdded);
  }

  public int getSumOfFilesChanged() {
    return sumOfFilesChanged;
  }

  public void addToSumOfFilesChanged(Integer sumOfFilesChanged) {
    this.sumOfFilesChanged = addOrSet(this.sumOfFilesChanged, sumOfFilesChanged);
  }

  public int getSumOfComments() {
    return sumOfComments;
  }

  public void addToSumOfComments(Integer sumOfComments) {
    this.sumOfComments = addOrSet(this.sumOfComments, sumOfComments);
  }

  public int getClosedCount() {
    return closedCount;
  }

  public void addToClosedCount(Integer closedCount) {
    this.closedCount = addOrSet(this.closedCount, closedCount);
  }

  private Integer addOrSet(Integer field, int toAdd) {
    if (field == null) {
      return toAdd;
    }

    return field + toAdd;
  }
}
