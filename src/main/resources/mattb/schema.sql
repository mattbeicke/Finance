CREATE TABLE IF NOT EXISTS account_type
(
    type_id INTEGER PRIMARY KEY AUTOINCREMENT,
    type    VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS account
(
    acc_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    acc_type INTEGER        NOT NULL,
    balance  DECIMAL(15, 2) NOT NULL,
    name     VARCHAR(50)    NOT NULL,
    FOREIGN KEY (acc_type) REFERENCES account_type (type_id)
);

CREATE TABLE IF NOT EXISTS "transaction"
(
    t_id     INTEGER PRIMARY KEY AUTOINCREMENT,
    date     INTEGER        NOT NULL,
    from_acc INTEGER        NOT NULL,
    to_acc   INTEGER        NOT NULL,
    amount   DECIMAL(15, 2) NOT NULL,
    memo     VARCHAR(100),
    FOREIGN KEY (from_acc) REFERENCES account (acc_id),
    FOREIGN KEY (to_acc) REFERENCES account (acc_id)
);

CREATE TABLE IF NOT EXISTS category
(
    cat_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    cat_name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS tcat
(
    trans INTEGER NOT NULL,
    cat   INTEGER NOT NULL,
    PRIMARY KEY (trans, cat),
    FOREIGN KEY (trans) REFERENCES "transaction" (t_id),
    FOREIGN KEY (cat) REFERENCES category (cat_id)
);

CREATE TABLE IF NOT EXISTS hidden_accounts
(
    hidden_acc_id INTEGER PRIMARY KEY AUTOINCREMENT,
    acc_id        INTEGER NOT NULL,
    FOREIGN KEY (acc_id) REFERENCES account (acc_id)
);

CREATE TABLE IF NOT EXISTS hidden_transactions
(
    hidden_t_id INTEGER PRIMARY KEY AUTOINCREMENT,
    t_id        INTEGER NOT NULL,
    FOREIGN KEY (t_id) REFERENCES "transaction" (t_id)
);

CREATE TABLE IF NOT EXISTS goal
(
    goal_id INTEGER PRIMARY KEY AUTOINCREMENT,
    acc_id  INTEGER        NOT NULL,
    target  DECIMAL(15, 2) NOT NULL,
    initial DECIMAL(15, 2) NOT NULL,
    name    VARCHAR(50)    NOT NULL,
    FOREIGN KEY (acc_id) REFERENCES account (acc_id)
);

INSERT OR IGNORE INTO account_type (type_id, type)
VALUES (0, 'External'),
       (1, 'Savings'),
       (2, 'Checking'),
       (3, 'Business');

INSERT OR IGNORE INTO account (acc_id, acc_type, balance, name)
VALUES (0, 0, 0.0, 'External');