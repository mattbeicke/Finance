# Matt's Finance App

## Introduction

Matt's Finance App is a JavaFX desktop application for managing personal finances built on the Spring Boot framework.
It allows users to track bank accounts, transactions, categorize spending, define financial goals, and monitor net worth
using a lightweight SQLite database.

---

## Table of Contents

* [Features](#features)
* [Installation](#installation)
* [Definitions](#definitions)
* [Usage](#usage)
* [UI Overview](#ui-overview)
* [Database Schema](#database-schema)
* [Architecture](#architecture)
* [Dependencies](#dependencies)
* [Configuration](#configuration)
* [Examples](#examples)
* [Troubleshooting](#troubleshooting)
* [Contributors](#contributors)
* [License](#license)
* [Future Improvements](#potential-future-improvements)

---

## Features

* Account management (create, edit, hide)
* Transaction tracking (internal transfers & external spending)
    * Category tagging (multiple per transaction)
* Track saving Goal progress toward a target amount for a specified account
* Net worth calculation
* Hide/unhide accounts & transactions
* Currency formatting
* SQLite-backed persistence
* Centralized error handling (`FinanceError`, `FinanceException`)
* Dark mode

---

## Installation

### Prerequisites

* Java 26+
* Maven

### Build

```bash
mvn clean install
```

### Run

```bash
mvn spring-boot:run
```

---

## Definitions

These represent things that you may see in the app that may not be immediately intuitive on what they mean

| Term     | Definition                                                                                                             |
|----------|------------------------------------------------------------------------------------------------------------------------|
| External | Represents any transfer of money that is not between two of your own accounts. (i.e. you buying something from a shop) |

---

## Usage

### Navigation

The app uses a sidebar (from `main.fxml`) with the following tabs:

* Dashboard
* Transactions
* Accounts
* Settings (bottom left)

The Account and Transaction tabs have pages for their tables<br>
Use the provided navigation buttons to go between pages<br>
Set page size in settings

---

## UI Overview

### Dashboard

* Displays total net worth (excluding hidden accounts)
* Goals shown and managed here

---

### Accounts Tab

* Add new accounts
* Add account types
* Edit existing accounts
* Hide/unhide accounts
* View hidden accounts

**Table Columns:**

* Name
* Type
* Balance

---

### Transactions Tab

* Add/edit transactions
* Assign categories (comma-separated)
* Optional memo
* Optional date (will use current date if none entered)
* Automatically update account balances (if wanted)

**Table Columns:**

* Date
* From Account
* To Account
* Category
* Amount
* Memo

---

### Settings Tab

* Set Account table page size
* Set Transaction table page size
* Set Dark Mode

---

### Modals

Required fields are indicated

* Add/Edit Account
* Add/Edit Transaction
* Add/Edit Goal
* Add Account Type
* Update Balances Confirmation

---

## Database Schema

### Overview

Database tables and initial data are automatically set up and populated at startup via schema.sql.
The application uses SQLite with normalized relational tables.
---

### Tables

#### `account`

```sqlite
CREATE TABLE account
(
    acc_id   INTEGER        NOT NULL
        CONSTRAINT account_pk
            PRIMARY KEY AUTOINCREMENT,
    acc_type INTEGER        NOT NULL
        CONSTRAINT account_type_fk
            REFERENCES account_type (type_id),
    balance  DECIMAL(15, 2) NOT NULL,
    name     VARCHAR(50)    NOT NULL
        CONSTRAINT name_unique
            UNIQUE
);
```

---

#### `account_type`

```sqlite
CREATE TABLE account_type
(
    type_id INTEGER      NOT NULL
        CONSTRAINT account_type_pk
            PRIMARY KEY AUTOINCREMENT,
    type    VARCHAR(100) NOT NULL
        CONSTRAINT type_unique
            UNIQUE
);
```

---

#### `category`

```sqlite
CREATE TABLE category
(
    cat_id   INTEGER     NOT NULL
        CONSTRAINT category_pk
            PRIMARY KEY AUTOINCREMENT,
    cat_name VARCHAR(50) NOT NULL
        CONSTRAINT cat_name_unique
            UNIQUE
);
```

---

#### `goal`

```sqlite
CREATE TABLE goal
(
    goal_id INTEGER        NOT NULL
        CONSTRAINT goal_pk
            PRIMARY KEY AUTOINCREMENT,
    acc_id  INTEGER        NOT NULL
        CONSTRAINT acc_id_fk
            REFERENCES account (acc_id),
    target  DECIMAL(15, 2) NOT NULL,
    initial DECIMAL(15, 2) NOT NULL,
    name    VARCHAR(50)    NOT NULL
);
```

---

#### `hidden_accounts`

```sqlite
CREATE TABLE hidden_accounts
(
    hidden_acc_id INTEGER NOT NULL
        CONSTRAINT hidden_accounts_pk
            PRIMARY KEY AUTOINCREMENT,
    acc_id        INTEGER NOT NULL
        CONSTRAINT acc_id_fk
            REFERENCES account (acc_id),
    CONSTRAINT acc_id_unique
        UNIQUE (acc_id)
);
```

---

#### `hidden_transactions`

```sqlite
CREATE TABLE hidden_transactions
(
    hidden_t_id INTEGER NOT NULL
        CONSTRAINT hidden_transactions_pk
            PRIMARY KEY AUTOINCREMENT,
    t_id        INTEGER NOT NULL
        CONSTRAINT t_id_fk
            REFERENCES "transaction" (t_id),
    CONSTRAINT t_id_unique
        UNIQUE (t_id)
);
```

---

#### `tcat` (Transaction <-> Category)

```sqlite
CREATE TABLE tcat
(
    trans INTEGER NOT NULL
        CONSTRAINT t_id_fk
            REFERENCES "transaction" (t_id),
    cat   INTEGER NOT NULL
        CONSTRAINT cat_id_fk
            REFERENCES category (cat_id),
    CONSTRAINT tcat_pk
        PRIMARY KEY (trans, cat)
);
```

---

#### `transaction`

```sqlite
CREATE TABLE "transaction"
(
    t_id     INTEGER        NOT NULL
        CONSTRAINT transaction_pk
            PRIMARY KEY AUTOINCREMENT,
    date     INTEGER        NOT NULL,
    from_acc INTEGER        NOT NULL
        CONSTRAINT from_acc_fk
            REFERENCES account (acc_id),
    to_acc   INTEGER        NOT NULL
        CONSTRAINT to_acc_fk
            REFERENCES account (acc_id),
    amount   DECIMAL(15, 2) NOT NULL,
    memo     VARCHAR(100)
);
```

---

### Relationships

* `account -> account_type` (many-to-one)
* `transaction -> account` (from/to)
* `transaction <-> category` (many-to-many via `tcat`)
* The 'hidden_...' tables act as filters

---

## Architecture

### Structure

```
src/
└── main/
    ├── java/
    │   └── mattb/
    │       ├── ConfigService.java       # Connection point to Preferences API
    │       ├── FinanceApp.java          # Spring Boot entry point
    │       ├── FinanceError.java        # Centralized error messages
    │       ├── FinanceException.java    # Custom runtime exception
    │       ├── JavaFXApp.java           # JavaFX application entry
    │       ├── UIUtilities.java         # Commonly used functions
    │       │
    │       ├── controllers/             # UI logic (JavaFX controllers)
    │       │   ├── AccountController.java
    │       │   ├── AddAccountController.java
    │       │   ├── AddGoalController.java
    │       │   ├── AddTransactionController.java
    │       │   ├── AddTypeController.java
    │       │   ├── DashboardController.java
    │       │   ├── GoalListCellController.java
    │       │   ├── MainController.java
    │       │   ├── TransactionController.java
    │       │   ├── UpdateBalancesController.java
    │       │   └── ViewGoalDetailsController.java
    │       │
    │       ├── dao/                     # Database interactions
    │       │   ├── AccountDAO.java
    │       │   ├── AccountDAOImpl.java
    │       │   ├── GoalDAO.java
    │       │   ├── GoalDAOImpl.java
    │       │   ├── TransactionDAO.java
    │       │   └── TransactionDAOImpl.java
    │       │
    │       ├── model/                   # Data models (records)
    │       │   ├── Account.java
    │       │   ├── AccountResponse.java
    │       │   ├── Goal.java
    │       │   ├── GoalResponse.java
    │       │   ├── Transaction.java
    │       │   └── TransactionResponse.java
    │       │
    │       └── service/                   # Business logic handling
    │           ├── AccountService.java
    │           ├── AccountServiceImpl.java
    │           ├── GoalService.java
    │           ├── GoalServiceImpl.java
    │           ├── TransactionService.java
    │           └── TransactionServiceImpl.java
    │
    └── resources/
        ├── application.properties       # Contains information for Spring Boot (primarily for how to do SQL database)
        ├── data.sql                     # SQL file containing the initial seed data
        ├── schema.sql                   # SQL file containing the table setup code
        └── mattb/
            ├── dark-theme.css           # CSS containing dark theme
            └── controllers/             # FXML UI layouts
                ├── accounts.fxml
                ├── add_account.fxml
                ├── add_goal.fxml
                ├── add_transaction.fxml
                ├── add_type.fxml
                ├── dashboard.fxml
                ├── goal_cell.fxml
                ├── main.fxml
                ├── transactions.fxml
                ├── update_balance.fxml
                └── view_goal_details.fxml
```

---

### Architecture Tiers

This project follows an N-Tiered MVC pattern

#### View Tier

* The ```FXML``` files (found in resources folder)
* Defines how the UI works

#### Controller Tier

* Marked with ```@Component```
* Handle user interaction with the JavaFX GUI
* Delegate business logic to service layer

---

#### Service Tier

* Marked with ```@Service```
* Uses ```@Transactional``` to ensure data integrity during multistep database operations (like moving money between
  accounts)
* Handles all business logic
* Delegates persistence to DAO layer

---

#### DAO Tier

* Marked with ```@Repository```
* Encapsulates all SQL logic
* Uses Spring's ```JdbcTemplate``` to remove the boilerplate nonsense
* Isolates database concerns from UI and business logic

---

#### Model Tier

* Represent core domain objects (Accounts, Transactions, and Goals)
* Implemented as record classes

---

### Design Patterns

* MVC (Model-View-Controller)
* DAO (Data Access Object)
* Service Classes
* Centralized error handling via enums + exceptions

---

## Dependencies

From `pom.xml`:

* Spring Boot Starter JDBC
    * For database connectivity and JdbcTemplate
* Spring Boot Starter Data JPA
    * For advanced persistence and transaction management
* SQLite JDBC
    * The database engine
* JavaFX (Controls & FXML)
    * UI framework
* ControlsFX
    * Enhanced UI components

---

## Configuration

**All application settings including database connection strings, initialization scripts,
and logging levels are managed in src/main/resources/application.properties.**

### Database

* File: `finance.db`
* Auto-created/used in working directory

### Currency Formatting

```java
NumberFormat.getCurrencyInstance(Locale.US)
```

---

## Examples

### Add a Transaction

1. Navigate to Transactions tab
2. Click "Add Transaction"
3. Fill in:
    * To and from Accounts (selecting External if it is a purchase or deposit)
    * Amount
    * Categories
    * Date
    * Memo
4. Save

---

## Troubleshooting

| Issue           | Solution                                         |
|-----------------|--------------------------------------------------|
| App won't start | Ensure Java 26+ is installed                     |
| Database errors | Delete `finance.db` and restart                  |
| UI not loading  | Check FXML paths                                 |
| Data Missing    | Check if items are hidden (toggle “View Hidden”) |
| Other           | Report ASAP to developer                         |

Installing Java 26 and Maven, running ```mvn clean install``` should install all required dependencies

Spring boot provides detailed debug logs. If you encounter database issues, check the console output for JdbcTemplate
and HikariPool messages to identify what exactly failed.

---

## Contributors

* Matthew Beicke

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## Potential Future Improvements

* Add something that celebrates you hitting a goal
* Make the User Interface pretty
* Improved goal tracking (link to accounts/categories)
* Data visualization (charts/graphs)
* Ability to mass import data (via CSV) and export data
* Proper testing suite