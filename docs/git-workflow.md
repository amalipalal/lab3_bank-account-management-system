# Git Workflow

## Branch Types

Use these branch patterns when working on the project:

* **feature/<name>** — new features and refactors
* **fix/<name>** — bug fixes
* **docs/<name>** — documentation improvements and Javadocs

Example:

```
feature/account-transfer
fix/null-pointer-bug
docs/update-readme
```

---

## Workflow for Adding a New Feature

1. Create a branch from `main`:

   ```
   git checkout -b feature/<name>
   ```

2. Make your changes and commit as needed.
   *Commit messages do not require prefixes.*

3. Push your branch:

   ```
   git push -u origin feature/<name>
   ```

4. Open a Pull Request into `main` on GitHub.

5. Ensure:

    * New functionality includes unit tests.
    * All tests pass before requesting review.

6. PRs must be merged using **Squash & Merge**.

---

## Rebasing Rules

### Keeping Your Branch Updated

Before opening a PR (or anytime your feature branch drifts behind `main`), update it using **rebase**, not merge:

```
git checkout feature/<name>
git pull --rebase origin main
```

This keeps the history clean and avoids adding extra merge commits.

### If There Are Conflicts

Resolve them normally, then continue:

```
git add .
git rebase --continue
```

### After a Rebase, Push With Force

A rebase rewrites commit history, so you must push with force:

```
git push --force-with-lease
```

(*This is safe — it only overwrites your branch, not anyone else’s.*)

---

## Bug Fix Workflow

1. Create a fix branch:

   ```
   git checkout -b fix/<name>
   ```

2. Implement the fix and commit normally.

3. Push and open a PR into `main`.

4. All tests must pass before merging.

5. Merge via **Squash & Merge**.

---

## Documentation Workflow

1. Create a docs branch:

   ```
   git checkout -b docs/<name>
   ```

2. Add or update documentation/Javadocs.

3. Push and open a PR into `main`.

4. Merge using **Squash & Merge**.

---

## Cherry-Picking Workflow

Use cherry-picking when a specific commit from one branch needs to be applied onto another (e.g., porting a fix to main).

1. Identify the commit hash:

   ```
   git log
   ```

2. Switch to the branch that should receive the commit:

   ```
   git checkout main
   ```

3. Cherry-pick the commit:

   ```
   git cherry-pick <commit-hash>
   ```

4. Resolve conflicts if any.

5. Run tests to ensure nothing breaks.

6. Commit the resolved cherry-pick (if needed) and push.

---

## Testing Requirements

* Every new feature must ship with unit tests.
* All existing and new tests must pass before merging.
* A PR cannot be merged unless tests are green.
