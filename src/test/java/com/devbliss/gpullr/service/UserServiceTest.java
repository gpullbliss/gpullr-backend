package com.devbliss.gpullr.service;

import org.junit.Before;

import com.devbliss.gpullr.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.devbliss.gpullr.Application;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class UserServiceTest {

  @Autowired
  private UserRepository userRepository;
  
  private UserService userService;
  
  @Before
  public void setup() {
    userService = new UserService(userRepository);
  }
}
