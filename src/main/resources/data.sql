INSERT OR IGNORE INTO account_type (type_id, type)
VALUES (0, 'External'),
       (1, 'Savings'),
       (2, 'Checking'),
       (3, 'Business');

INSERT OR IGNORE INTO account (acc_id, acc_type, balance, name)
VALUES (0, 0, 0.0, 'External');