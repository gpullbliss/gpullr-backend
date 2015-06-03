package com.devbliss.gpullr.domain.notifications;

/**
 * Notification type used in the {@link com.devbliss.gpullr.service.UserNotificationService} and
 * the {@link UserNotification}.
 */
public interface UserNotificationType {

  public static String PULLREQUEST_CLOSED = "PULLREQUEST_CLOSED";

  public static String PULLREQUEST_COMMENTED = "PULLREQUEST_COMMENTED";

}
