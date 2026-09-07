# Contributing to COMP438 QA — Assignment 2 — HebronMart

This repository is a shared Katalon Studio project for COMP438 — Software Quality Assurance at Birzeit University.

The purpose of these guidelines is to make it possible for all group members to work independently without overwriting each other's Katalon artifacts or creating unnecessary Git conflicts.

## Assignment scope

Each group member is responsible for their own three automated test cases:

1. Basic UI verification / user-flow test
2. Data-driven test
3. Advanced Katalon interaction test

The shared repository structure is provided only for organization and collaboration. Each member remains responsible for designing and implementing their own work.

## Repository structure

Each member has a dedicated Test Case area:

Test Cases/
- Ahmad/
  - Basic/
  - DataDriven/
  - Advanced/
- Anas/
  - Basic/
  - DataDriven/
  - Advanced/
- Abduallah/
  - Basic/
  - DataDriven/
  - Advanced/
- Shareef/
  - Basic/
  - DataDriven/
  - Advanced/
- Majd/
  - Basic/
  - DataDriven/
  - Advanced/

Member-specific datasets belong under:

Data Files/<Member>/

The Object Repository is shared and organized by feature, not by member:

Object Repository/
- Common/
  - Header/
  - Footer/
  - Navigation/
- Registration/
- Login/
- Search/
- Product/
- Cart/
- Checkout/
- Wishlist/
- Contact/
- Blog/
- Merchant/

Before creating a new Test Object, check whether an equivalent reusable object already exists.

## Branches

main is the shared integrated project.

Each member should work on their own branch:

- feature/ahmad-tests
- feature/anas-tests
- feature/abduallah-tests
- feature/shareef-tests
- feature/majd-tests

Normal development should not be done directly on main.

Because branch protection is not available for this private repository on the current GitHub plan, this rule is enforced by team workflow rather than GitHub settings.

## First-time setup

Clone the repository:

git clone https://github.com/Ahmaddaghra/COMP438-QA-Assignment2-HebronMart.git

Then enter the folder:

cd COMP438-QA-Assignment2-HebronMart

Switch to your own branch, for example:

git checkout feature/anas-tests

Then open:

COMP438_Assignment2_HebronMart.prj

in Katalon Studio.

Using Git through Katalon Studio, GitHub Desktop, or another Git client is also acceptable. Command-line Git is not required.

## Normal work session

A typical work cycle is:

checkout your branch
→ update your branch if shared main changed
→ implement your work
→ execute and verify your tests in Katalon
→ commit
→ push
→ open a Pull Request to main when ready

If using command-line Git:

git checkout feature/<your-name>-tests
git pull

After implementing and testing:

git status
git add <intentional files>
git commit -m "test: add <short description>"
git push

Avoid blindly staging generated files. Check git status before committing.

## Pull Requests

When your work is ready for integration:

1. Push your member branch.
2. Open a Pull Request from your branch into main.
3. Briefly describe what was added or changed.
4. Mention shared Test Objects or other common files that were modified.
5. Resolve conflicts before merging.

A Pull Request is used primarily to avoid accidental overwrites and to make shared changes visible to the rest of the group.

## Shared Object Repository rules

The Object Repository is shared by everyone, so changes here deserve extra care.

- Reuse existing Test Objects when they represent the same UI element.
- Do not create duplicate objects only because another member originally created them.
- Prefer stable selectors such as IDs, names, data-* attributes, and short semantic CSS selectors.
- Avoid absolute XPath such as /html/body/...
- Avoid fragile positional selectors such as div[3] when a semantic locator is available.
- If changing an existing shared Test Object may affect another test, mention the change to the group before integration.

## Test design and reliability

Each member chooses their own test scenarios, but automated tests should preferably be:

- independent — not dependent on another test running first;
- repeatable — safe to execute more than once;
- deterministic — Pass/Fail should come from assertions, not visual inspection;
- diagnosable — failures should clearly indicate what behavior failed;
- synchronized using meaningful waits rather than unnecessary fixed delays.

Do not change expected behavior merely to make an automation pass. If the Application Under Test behaves incorrectly, keep the valid expectation and report the observed behavior.

## Naming conventions

Recommended Test Case pattern:

ATC-<MEMBER>-01_<Description>
ATC-<MEMBER>-02_<Description>
ATC-<MEMBER>-03_<Description>

Example:

ATC-AHM-01_Search_Product_Basic

Recommended Test Object prefixes:

btn_   button
inp_   input field
lbl_   label/text
lnk_   link
ddl_   dropdown
chk_   checkbox
card_  card/container

Recommended Test Data pattern:

TD_<Member>_<Purpose>

Recommended Test Suite patterns:

TS_<Member>
TS_GroupRegression

## Commit messages

Use short, meaningful commit messages.

Examples:

test: add Ahmad basic product search flow
test: add login data-driven coverage
fix: stabilize product price selector
refactor: reuse common header search object
docs: update collaboration workflow

Avoid messages such as:

final
final2
done
new
last version

## Generated files and secrets

Do not commit:

- Katalon execution reports
- build/cache/output folders
- temporary files
- local IDE metadata
- local Katalon account/integration settings
- API keys
- access tokens
- passwords
- certificates or private keys
- .env files containing secrets

The repository .gitignore already excludes the main generated and sensitive local files, but always inspect git status before committing.

## Conflict avoidance

Most Git conflicts can be avoided by following three rules:

1. Work on your own member branch.
2. Keep member-specific Test Cases and Data Files inside your own folders.
3. Coordinate before modifying shared objects or other shared framework files already used by another member.

If a conflict occurs, do not delete another member's changes merely to make Git accept the merge. Review both versions and preserve both intended behaviors where necessary.

## Help

If the repository does not clone correctly, Katalon cannot open the .prj file, a branch is missing, or Git reports a conflict involving shared project files, share the exact error with the group before making destructive changes.
