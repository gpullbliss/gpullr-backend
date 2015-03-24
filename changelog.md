
### Version 0.1.0-SNAPSHOT


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

-- Last change by: Daniel Walldorf <daniel.walldorf@devbliss.com> Tue Mar 24 10:24:30 CET 2015

