# Finance App

## Introduction

Finance App is a JavaFX desktop application for managing personal finances. It allows users to create accounts, track transactions, categorize spending, and monitor net worth using a lightweight SQLite database.

---

## Table of Contents

* [Features](#features)
* [Installation](#installation)
* [Usage](#usage)
* [UI Overview](#ui-overview)
* [Database Schema](#database-schema)
* [Architecture](#architecture)
* [Dependencies](#dependencies)
* [Configuration](#configuration)
* [Troubleshooting](#troubleshooting)
* [Contributors](#contributors)
* [License](#license)

---

## Features

* Account management (create, edit, hide)
* Transaction tracking between accounts
* Category tagging (multiple per transaction)
* Net worth calculation
* Hide/unhide accounts & transactions
* Currency formatting
* SQLite-backed persistence
* Centralized error handling (`FinanceError`, `FinanceException`)

---

## Installation

### Prerequisites

* Java 26+
* Maven

### Build

```bash
mvn clean package
```

### Run

```bash
javaw -jar target/Finance-1.0.0.jar
```

---

## Usage

### Navigation

The app uses a sidebar (from `main.fxml`) with:

* Dashboard
* Transactions
* Accounts

---

## UI Overview

### Dashboard

* Displays:

    * Total net worth (excluding hidden accounts)

---

### 💼 Accounts Tab

* Add new accounts
* Add account types
* Edit existing accounts
* Hide/unhide accounts

**Table Columns:**

* Name
* Type
* Balance

---

### Transactions Tab

* Add/edit transactions
* Transfer money between accounts
* Assign categories (comma-separated)
* Optional memo + date

**Table Columns:**

* Date
* From Account
* To Account
* Category
* Amount
* Memo

---

### Modals

* Add Account
* Add Transaction
* Add Account Type
* Update Balances confirmation

---

## Database Schema

### Overview

The application uses SQLite with normalized relational tables.

---

### Tables

#### `account`

```sql
CREATE TABLE account (
    acc_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    acc_type INTEGER NOT NULL,
    balance  DECIMAL(15,2) NOT NULL,
    name     VARCHAR(50) NOT NULL,
    FOREIGN KEY (acc_type) REFERENCES account_type
);
```

---

#### `account_type`

```sql
CREATE TABLE account_type (
    type_id INTEGER PRIMARY KEY AUTOINCREMENT,
    type    VARCHAR(100) NOT NULL
);
```

---

#### `transaction`

```sql
CREATE TABLE "transaction" (
    t_id     INTEGER PRIMARY KEY AUTOINCREMENT,
    date     INTEGER NOT NULL,
    from_acc INTEGER NOT NULL,
    to_acc   INTEGER NOT NULL,
    amount   DECIMAL(15,2) NOT NULL,
    memo     VARCHAR(100),
    FOREIGN KEY (from_acc) REFERENCES account,
    FOREIGN KEY (to_acc) REFERENCES account
);
```

---

#### `category`

```sql
CREATE TABLE category (
    cat_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    cat_name VARCHAR(50) NOT NULL
);
```

---

#### `tcat` (Transaction ↔ Category)

```sql
CREATE TABLE tcat (
    trans INTEGER NOT NULL,
    cat   INTEGER NOT NULL,
    PRIMARY KEY (trans, cat),
    FOREIGN KEY (trans) REFERENCES "transaction",
    FOREIGN KEY (cat) REFERENCES category
);
```

---

#### `hidden_accounts`

```sql
CREATE TABLE hidden_accounts (
    hidden_acc_id INTEGER PRIMARY KEY AUTOINCREMENT,
    acc_id        INTEGER NOT NULL,
    FOREIGN KEY (acc_id) REFERENCES account
);
```

---

#### `hidden_transactions`

```sql
CREATE TABLE hidden_transactions (
    hidden_t_id INTEGER PRIMARY KEY AUTOINCREMENT,
    t_id        INTEGER NOT NULL,
    FOREIGN KEY (t_id) REFERENCES "transaction"
);
```

---

### Relationships

* `account → account_type` (many-to-one)
* `transaction → account` (from/to)
* `transaction ↔ category` (many-to-many via `tcat`)
* hidden tables act as filters

---

## Architecture

### Structure

```
src/
└── main/
    ├── java/
    │   └── mattb/
    │       ├── FinanceError.java        # Centralized error messages
    │       ├── FinanceException.java    # Custom runtime exception
    │       ├── Launcher.java            # JAR entry point
    │       ├── Main.java                # JavaFX application entry
    │       │
    │       ├── controllers/             # UI logic (JavaFX controllers)
    │       │   ├── AccountController.java
    │       │   ├── AddAccountController.java
    │       │   ├── AddTransactionController.java
    │       │   ├── AddTypeController.java
    │       │   ├── DashboardController.java
    │       │   ├── MainController.java
    │       │   └── TransactionController.java
    │       │
    │       └── model/                   # Data models (records)
    │           ├── Account.java
    │           └── Transaction.java
    │
    └── resources/
        └── mattb/
            └── controllers/             # FXML UI layouts
                ├── accounts.fxml
                ├── add_account.fxml
                ├── add_transaction.fxml
                ├── add_type.fxml
                ├── dashboard.fxml
                ├── main.fxml
                ├── transactions.fxml
                └── update_balance.fxml
```

---

### Design Patterns

* MVC (Model-View-Controller)
* DAO-like direct SQL usage via controllers
* Centralized error handling via enum

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

## Troubleshooting

### Database connection fails

* Ensure `finance.db` exists
* Check working directory

---

### JavaFX runtime issues

Make sure JavaFX is available or bundled properly.

---

### Data missing

* Check if items are hidden (toggle “View Hidden”)

---

## Contributors

* Matthew Beicke

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## Possible Improvements

* Add schema migration/versioning
* Separate DAO layer from controllers
* Add unit tests
* Export/import data
* UI styling (CSS)