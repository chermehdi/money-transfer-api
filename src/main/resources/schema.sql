DROP TABLE IF EXISTS user
;
DROP TABLE IF EXISTS transaction
;
DROP TABLE IF EXISTS account
;
DROP TABLE IF EXISTS transfer
;

CREATE TABLE account
(
    id         INT          NOT NULL AUTO_INCREMENT,
    identifier varchar(255) NOT NULL,
    currency   VARCHAR(10)  NOT NULL,
    amount     decimal(19, 4),

    CONSTRAINT pk_t_account PRIMARY KEY (id),
)
;

CREATE TABLE user
(
    id         INT         NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    account_id INT,

    CONSTRAINT pk_t_user PRIMARY KEY (ID),
    CONSTRAINT fk_t_user_account_id FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE
)
;

CREATE TABLE transaction
(
    id          long           NOT NULL AUTO_INCREMENT,
    account_id  INT            NOT NULL,
    identifier  VARCHAR(255)   NOT NULL,
    amount      DECIMAL(19, 4) NOT NULL,
    currency    VARCHAR(10)    NOT NULL,
    performedAt timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_t_transaction PRIMARY KEY (id),
    CONSTRAINT fk_t_transaction_account_id FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE
)
;

CREATE TABLE transfer
(
    id                      long           NOT NULL AUTO_INCREMENT,
    from_account_identifier VARCHAR(255)   NOT NULL,
    to_account_identifier   VARCHAR(255)   NOT NULL,
    amount                  DECIMAL(19, 4) NOT NULL,
    currency                VARCHAR(10)    NOT NULL,
    status                  VARCHAR(50)    NOT NULL,
    performedAt             timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_t_transfer PRIMARY KEY (id),
)
;

INSERT INTO account (identifier, currency, amount)
VALUES ('d42f0d8a-2e87-4ca1-a55e-e283d4ae7f57', 'EUR', 80)
;

INSERT INTO account (identifier, currency, amount)
VALUES ('d42f56ea-2e87-4sa1-a55e-e2drd4ae7ed7', 'EUR', 200)
;

INSERT INTO account (identifier, currency, amount)
VALUES ('1f442fda-b0c4-40a0-b2f8-89dca5e0b2d8', 'MAD', 320)
;

INSERT INTO user (first_name, last_name, account_id)
VALUES ('mehdi', 'cheracher', 1)
;

INSERT INTO user (first_name, last_name, account_id)
VALUES ('foo', 'bar', 2)
;

INSERT INTO user (first_name, last_name, account_id)
VALUES ('john', 'doe', 3)
;

INSERT INTO transaction (account_id, identifier, amount, currency)
VALUES (1, 'd42f0d8a-2e87-4ca1-a55e-e283d4ae7f57', 100, 'EUR')
;

INSERT INTO transaction (account_id, identifier, amount, currency)
VALUES (1, 'd42f56ea-2e87-4sa1-a55e-e2drd4ae7ed7', 200, 'EUR')
;

INSERT INTO transaction (account_id, identifier, amount, currency)
VALUES (1, '1f442fda-b0c4-40a0-b2f8-89dca5e0b2d8', 300, 'MAD')
;

INSERT INTO transfer (from_account_identifier, to_account_identifier, amount, currency, status)
VALUES ('d42f0d8a-2e87-4ca1-a55e-e283d4ae7f57', '1f442fda-b0c4-40a0-b2f8-89dca5e0b2d8', 20, 'EUR',
        'SUCCESS')
