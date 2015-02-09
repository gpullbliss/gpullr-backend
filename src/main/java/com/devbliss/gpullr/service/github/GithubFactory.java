package com.devbliss.gpullr.service.github;

import com.jcabi.github.Github;

public interface GithubFactory {
  
  Github createClient();
}
