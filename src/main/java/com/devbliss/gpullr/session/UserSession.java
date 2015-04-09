package com.devbliss.gpullr.session;

import com.devbliss.gpullr.domain.User;
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

  private User user;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

}
