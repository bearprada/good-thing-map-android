# Project plan

## Current scope

Establish [agent guidelines](AGENTS.md) and [project memory](PROJECT_MEMORY.md). This documentation rollout does not complete the application backlog.

## Work queue

Reconcile remote issues/PRs and existing feature worktrees. Record each approved item's ID, priority, owner, dependency IDs, branch/worktree, acceptance test cases, verification command/results, status and next action. Keep pending verification/review visible and continue independent work.

## Rollout exit criteria

Validate guidance coverage, local links and `git diff --check`; commit only rollout documents and publish to `origin/master`. Confirm the containing commit SHA with `git ls-remote origin refs/heads/master`.
