# COMP438 QA — Assignment 2 — HebronMart

**Course:** COMP438 — Software Quality Assurance

**University:** Birzeit University

**AUT:** [https://hebronmart.com](https://hebronmart.com)

## Purpose

Automated GUI testing of HebronMart using Katalon Studio. This project continues the manual testing work from Assignment 1.

## Team

- Ahmad Daghra
- Anas Shalabi
- Abduallah Jagh
- Shareef Jalamneh
- Majd Abojamial

## Assignment requirements

Each member implements:

1. A basic UI verification or user-flow test
2. A data-driven test
3. An advanced Katalon interaction test

The project is expected to contain **15 automated test cases** in total.

## Project architecture

```text
Test Cases/<Member>/{Basic,DataDriven,Advanced}
Object Repository/<Feature>
Data Files/<Member>
Test Suites/{Members,Regression}
```

- Test cases are grouped by team member and test category.
- Object Repository entries are organized by feature rather than team member.
- Test data is grouped by team member.
- Member suites and the regression suite are kept under `Test Suites`.
- Reusable Keywords may be added later when genuine duplication appears.

## Naming conventions

### Test cases

```text
ATC-<MEMBER>-01_<Description>
ATC-<MEMBER>-02_<Description>
ATC-<MEMBER>-03_<Description>
```

Examples:

```text
ATC-AHM-01_Search_Product_Basic
ATC-AHM-02_Search_DataDriven
ATC-AHM-03_Cart_Advanced
```

### Test objects

Use a prefix that identifies the element type:

```text
btn_  txt_  inp_  lbl_  lnk_  ddl_  chk_  card_
```

### Test data

```text
TD_<Member>_<Purpose>
```

### Test suites

```text
TS_<Member>
TS_GroupRegression
```

## Git workflow

- `main` is the stable, integrated project.
- Each member works on their own feature branch.
- Do not perform normal development directly on `main`.
- Test changes locally before merging.
- Use meaningful commit messages.
- Pull and update the local branch before starting work.

Recommended branches:

```text
feature/ahmad-tests
feature/anas-tests
feature/abduallah-tests
feature/shareef-tests
feature/majd-tests
```

## Security

Secrets, API keys, tokens, passwords, credentials, private keys, and local environment files must never be committed to this repository.
