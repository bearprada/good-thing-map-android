# Project memory

## Reviewed baseline (2026-10-25)

- Android Good Thing Map application; [README](README.md), Gradle tasks, CI workflows, unit tests and instrumentation tests are verification entry points.
- Delivery base: updated `origin/master` at `0ac77bf`. Existing feature branches/worktrees retain ongoing work; reconcile them with remote issues before selecting another item.
- Preserve unrelated `.DS_Store` and `.serena/` files. Current rollout is documentation-only; application tests have not been run.

## Handoff contract

Rollout verification: policy-coverage and local-link checks passed across all 13 workspace repositories; `git diff --check` passed. Application testing remains outside this documentation-only change.

After each item record task/owner, branch/worktree, base SHA, changed files, commit/PR, acceptance cases, exact verification commands/results/artifacts, pending external acceptance and next action. Store status in [project plan](PROJECT_PLAN.md); reconcile with Git and remote state on resume.
