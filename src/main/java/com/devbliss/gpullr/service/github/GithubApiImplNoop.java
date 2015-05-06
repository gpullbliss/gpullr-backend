package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.exception.UnexpectedException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GithubApiImplNoop implements GithubApi {

  @Override
  public List<Repo> fetchAllGithubRepos() throws UnexpectedException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GithubPullRequestResponse fetchPullRequest(PullRequest pullRequest, Optional<String> etagHeader) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GithubPullRequestBuildStatusResponse fetchBuildStatus(PullRequest pullRequest, Optional<String> etagHeader) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GithubEventsResponse fetchAllEvents(Repo repo, Optional<String> etagHeader) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<User> fetchAllOrgaMembers() throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void assignUserToPullRequest(User user, PullRequest pull) {
    // TODO Auto-generated method stub

  }

  @Override
  public void unassignUserFromPullRequest(User user, PullRequest pull) {
    // TODO Auto-generated method stub

  }

}
