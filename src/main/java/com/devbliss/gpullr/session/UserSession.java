package com.devbliss.gpullr.session;

import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.util.Log;
import org.slf4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Represent the session of a user and includes its {@link User} object.
 *
 * @author Philipp Karstedt <philipp.karstedt@devbliss.com>
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION,
       proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSession {

  @Log
  Logger logger;

  private User user;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

}
