CREATE TABLE IF NOT EXISTS account_type
(
    type_id INTEGER      NOT NULL
        CONSTRAINT account_type_pk
            PRIMARY KEY AUTOINCREMENT,
    type    VARCHAR(100) NOT NULL
        CONSTRAINT type_unique
            UNIQUE
);

CREATE TABLE IF NOT EXISTS account
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

CREATE TABLE IF NOT EXISTS "transaction"
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
    memo     VARCHAR(100),
    CONSTRAINT transaction_unique
        UNIQUE (date, from_acc, to_acc, amount, memo)
);

CREATE TABLE IF NOT EXISTS category
(
    cat_id   INTEGER     NOT NULL
        CONSTRAINT category_pk
            PRIMARY KEY AUTOINCREMENT,
    cat_name VARCHAR(50) NOT NULL
        CONSTRAINT cat_name_unique
            UNIQUE
);

CREATE TABLE IF NOT EXISTS tcat
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

CREATE TABLE IF NOT EXISTS hidden_accounts
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

CREATE TABLE IF NOT EXISTS hidden_transactions
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

CREATE TABLE IF NOT EXISTS goal
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