### Version 1.0.0-SNAPSHOT

 - [refactor] preparation to go open source
 - [refactor] PRT 109 - aligned README documentation to current state of the application
 - [feature] PRT 31 - enable/disable desktop notifications at user settings
 - [refactor] due to PRT 121 switch to new oauth application
 - [bug] old PRs not disappearing
 - [bug] activate repos as they become available again
 - [feature] api rate limit reached notifications
 - [feature] use a different github account for local dev development
 - [bug] fixed PR status in log message
 - [bug] only starting / stopping PR watch thread when PR is really updated
 - [bug] fixed PRT-122 (disappeared PRs)
 - [feature] removed deprecated login controller actions

### Version 0.1.0-SNAPSHOT

 - [bug] fair ranking algorithm even fairer
 - [feature] added francais as allowed language
 - [bug] fallback assign date when saving a pullrequest with assignee but no such date
 - [bug] fetching pull request events with updated repo url after repo renaming
 - [feature] implemented GitHub OAuth login web flow
 - [feature] added Fränkisch to list of valid languages
 - [feature] fair ranking algorithm
 - [feature] add allowed languages
 - [feature] no more pr ordering in backend / two order fields for assigned and unassigned prs
 - [feature] add IT lang config
 - [bug] switch user language
 - [feature] fixed availablae languages endpoint
 - [feature] available languages endpoint returns map instead of list
 - [bug] fix bad request response. wrong service methods called.
 - [feature] no zero closed count rankings anymore
 - [feature] storing user's preferred language in user settings
 - [feature] PRT-74 update renamed repositories
 - [feature] PR-closed-notification: backend part (w/o WebSocket support)
 - [feature] session timeout 9 hrs
 - [feature] storing build uri in pullrequest when fetching pullrequest status from Github
 - [bug] number of comments in a pullrequest is now the sum of both normal and review comments
 - [bug] correct field for fetching number of review comments
 - [feature] number of comments and build status in pull request data
 - [feature] added user profile url to user
 - [feature] do not count closed pull request if the author and assignee are the same person.
 - [feature] grouped rankings
 - [feature] Added pull_request.md
 - [feature] Added User.fullName
 - [refactor] readme
 - [refactor] H2 mixed mode
 - [feature] unassign myself from pull request
 - [bug] Fixed bug when using repo watchlist feature
 - [refactor] better error propagation to the frontend
 - [feature] configurable repository watchlist
 - [bug] make repos inactive instead of deleting them
 - [bug] remove repos that have disappeared
 - [feature] pullrequests endpoint supports optional filtering by repos for filtered wallboard feature
 - [bug] fixed ranking calculation / close dates
 - [bug] fixed date time formatting for pullrequest dto
 - [bug] first attempt to solve fetch problems
 - [bug] pull requests w/o user session works again (with default sort order)
 - [bug] correct rate limit exceeded / reset handling
 - [feature] user settings for preferred sort order of pull requests
 - [feature] ranking calculation
 - [refactor] rolling logfiles per day and after 50MB
 - [feature] added assignedAt property to PullRequest Entity and Dto
 - [refactor] implemented simple database migration strategy
 - [bug] assignee for open PR was not fetched when assignment took place at GitHub web
 - [bug] save assignee when null from GitHub response
 - [bug] missing data in pullrequest
 - [refactor] Apache HTTP client for repo events requests, paying attention to ETag / 304 NOT MODIFIED
 - [feature] return error dto / json on forbidden (403) error
 - [feature] created extra checkstyle-test.xml to exclude string duplication check from tests
 - [bug] orgamembers sorted by username; pull requests sorted by creation date latest first
 - [feature] endpoint for self-assigning PRs; bug-fixes for fetching PR events from GitHub
 - [feature] user session upon login
 - [feature] periodically fetching users, repos and (PR-)events
 - [bug] fixed database config
 - [feature] github service / jcabi access setup
 - [feature] better test output for gradle
 - [feature] Added checkstyle
 - [feature] server port and log configuration
 - [feature] project setup
 - [initial] initial commit

-- Last change by: Daniel Walldorf <daniel.walldorf@devbliss.com> Mon Jun 15 12:21:24 CEST 2015
