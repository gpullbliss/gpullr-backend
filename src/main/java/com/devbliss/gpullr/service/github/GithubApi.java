package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.exception.UnexpectedException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface GithubApi {

  /**
   * Retrieves all repositories (public, private, forked, etc.) belonging to our organization, from GitHub.
   *
   * @return possibly empty list of repositories
   */
  List<Repo> fetchAllGithubRepos() throws UnexpectedException;

  /**
   * Fetches data of an existing pull request.
   *
   * @param pullRequest
   * @param etagHeader
   * @return response object containing the actual pull request plus response meta data required for next request
   */
  GithubPullRequestResponse fetchPullRequest(PullRequest pullRequest, Optional<String> etagHeader);

  GithubPullRequestBuildStatusResponse fetchBuildStatus(PullRequest pullRequest,
      Optional<String> etagHeader);

  GithubEventsResponse fetchAllEvents(Repo repo, Optional<String> etagHeader);

  List<User> fetchAllOrgaMembers() throws IOException;

  void assignUserToPullRequest(User user, PullRequest pull);

  void unassignUserFromPullRequest(User user, PullRequest pull);
}
