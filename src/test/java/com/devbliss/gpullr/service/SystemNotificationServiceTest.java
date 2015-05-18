package com.devbliss.gpullr.service;

import static org.junit.Assert.assertTrue;

import com.devbliss.gpullr.domain.ApiRateLimitReachedEvent;
import com.devbliss.gpullr.domain.notifications.SystemNotification;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SystemNotificationServiceTest {

  private SystemNotificationService service;

  @Before
  public void setup() {
    service = new SystemNotificationService();
  }

  @Test
  public void emptySystemNotifications() {
    List<SystemNotification> notifications = service.getNotifications();
    assertTrue(notifications.isEmpty());
  }

  @Test
  public void oneSystemNotification() {
    ApiRateLimitReachedEvent event = new ApiRateLimitReachedEvent(this, Instant.now().plusSeconds(10));
    service.onApplicationEvent(event);
    List<SystemNotification> notifications = service.getNotifications();

    assertTrue(notifications.size() == 1);
  }

  @Test
  public void newerNotificationReplacesOld() {
    ApiRateLimitReachedEvent event;
    Instant nowTime = Instant.now();

    event = new ApiRateLimitReachedEvent(this, nowTime);
    service.onApplicationEvent(event);

    event = new ApiRateLimitReachedEvent(this, nowTime.plusSeconds(1));
    service.onApplicationEvent(event);

    event = new ApiRateLimitReachedEvent(this, nowTime.plusSeconds(2));
    service.onApplicationEvent(event);

    event = new ApiRateLimitReachedEvent(this, nowTime.plusSeconds(10));
    service.onApplicationEvent(event);

    List<SystemNotification> notifications = service.getNotifications();

    assertTrue(notifications.size() == 1);

    ZonedDateTime expectedResetTime = notifications.get(0).validUntil;
    assertTrue(expectedResetTime.isEqual(ZonedDateTime.ofInstant(nowTime.plusSeconds(10), ZoneId.systemDefault())));
  }

  @Test
  public void noExpiredNotifications() {
    ApiRateLimitReachedEvent event = new ApiRateLimitReachedEvent(this, Instant.now().minusSeconds(10));
    service.onApplicationEvent(event);
    List<SystemNotification> notifications = service.getNotifications();

    assertTrue(notifications.size() == 0);
  }

}