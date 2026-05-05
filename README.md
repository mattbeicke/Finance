# Matt's Finance App

## Introduction

Matt's Finance App is a JavaFX desktop application for managing personal finances.
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
javaw -jar target/Finance-1.0.0.jar
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

The application uses SQLite with normalized relational tables

---

### Tables

#### `account`

```sqlite
CREATE TABLE account
(
    acc_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    acc_type INTEGER        NOT NULL,
    balance  DECIMAL(15, 2) NOT NULL,
    name     VARCHAR(50)    NOT NULL,
    FOREIGN KEY (acc_type) REFERENCES account_type
);
```

---

#### `account_type`

```sqlite
CREATE TABLE account_type
(
    type_id INTEGER PRIMARY KEY AUTOINCREMENT,
    type    VARCHAR(100) NOT NULL
);
```

---

#### `transaction`

```sqlite
CREATE TABLE "transaction"
(
    t_id     INTEGER PRIMARY KEY AUTOINCREMENT,
    date     INTEGER        NOT NULL,
    from_acc INTEGER        NOT NULL,
    to_acc   INTEGER        NOT NULL,
    amount   DECIMAL(15, 2) NOT NULL,
    memo     VARCHAR(100),
    FOREIGN KEY (from_acc) REFERENCES account,
    FOREIGN KEY (to_acc) REFERENCES account
);
```

---

#### `category`

```sqlite
CREATE TABLE category
(
    cat_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    cat_name VARCHAR(50) NOT NULL
);
```

---

#### `tcat` (Transaction <-> Category)

```sqlite
CREATE TABLE tcat
(
    trans INTEGER NOT NULL,
    cat   INTEGER NOT NULL,
    PRIMARY KEY (trans, cat),
    FOREIGN KEY (trans) REFERENCES "transaction",
    FOREIGN KEY (cat) REFERENCES category
);
```

---

#### `hidden_accounts`

```sqlite
CREATE TABLE hidden_accounts
(
    hidden_acc_id INTEGER PRIMARY KEY AUTOINCREMENT,
    acc_id        INTEGER NOT NULL,
    FOREIGN KEY (acc_id) REFERENCES account
);
```

---

#### `hidden_transactions`

```sqlite
CREATE TABLE hidden_transactions
(
    hidden_t_id INTEGER PRIMARY KEY AUTOINCREMENT,
    t_id        INTEGER NOT NULL,
    FOREIGN KEY (t_id) REFERENCES "transaction"
);
```

#### `goal`

```sqlite
CREATE TABLE goal
(
    goal_id INTEGER PRIMARY KEY AUTOINCREMENT,
    acc_id  INTEGER        NOT NULL,
    target  DECIMAL(15, 2) NOT NULL,
    initial DECIMAL(15, 2) NOT NULL,
    name    VARCHAR(50)    NOT NULL,
    FOREIGN KEY (acc_id) REFERENCES account
)
```

---

### Relationships

* `account -> account_type` (many-to-one)
* `transaction -> account` (from/to)
* `transaction <-> category` (many-to-many via `tcat`)
* hidden tables act as filters

---

## Architecture

### Structure

```
src/
└── main/
    ├── java/
    │   └── mattb/
    │       ├── Config.java              # Connection point to Preferences API
    │       ├── FinanceError.java        # Centralized error messages
    │       ├── FinanceException.java    # Custom runtime exception
    │       ├── Launcher.java            # JAR entry point
    │       ├── Main.java                # JavaFX application entry
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
    │       │   ├── Goal.java
    │       │   ├── Transaction.java
    │       │   ├── TransactionResponse.java
    │       │   └── TransactionRequest.java
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
        └── mattb/
            ├── dark-theme.css           # CSS containing dark theme
            ├── schema.sql               # SQL file containing the neccessary table setup and initial population
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

### Layers

#### Controllers (UI Layer)

* Handle user interaction
* Bind UI to data
* Delegate business logic to service layer

---

#### Service Layer

* Delegate persistence to DAO layer

---

#### DAO Layer

The DAO (Data Access Object) layer is responsible for:

* Encapsulating all SQL logic
* Providing clean methods for CRUD operations
* Isolating database concerns from UI logic

---

#### Model Layer

* Represent core domain objects (Account, Transaction, Goal)
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

* JavaFX Controls
* JavaFX FXML
* SQLite JDBC
* Maven Shade Plugin (for fat JAR)

---

## Configuration

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

---

## Contributors

* Matthew Beicke

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## Potential Future Improvements

* Add something that celebrates you hitting a goal
* Add a section to actually delete data (instead of just hiding it)
* Make the User Interface pretty
* Improved goal tracking (link to accounts/categories)
* Data visualization (charts/graphs)
* Ability to mass import data (via CSV) and export data
* Proper testing suite
* Alert messages saying things worked, missing fields, etc