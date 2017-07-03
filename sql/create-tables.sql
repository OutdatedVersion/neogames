# Select correct database
USE neogames;


# Create accounts table
CREATE TABLE IF NOT EXISTS accounts (
  `iid` INT NOT NULL AUTO_INCREMENT,
  `uuid` VARCHAR(40) NOT NULL,
  `name` VARCHAR(20) NOT NULL,
  `role` VARCHAR(16) NOT NULL DEFAULT 'DEFAULT',
  `address` VARCHAR(42) NOT NULL,
  `first_login` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_login` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`iid`),
  UNIQUE KEY (`uuid`)
);


# Permission node storage
CREATE TABLE IF NOT EXISTS assigned_permissions (
  `possessor` VARCHAR(16),
  `node` VARCHAR(64),
  PRIMARY KEY (`possessor`)
);


# Punishment storage
CREATE TABLE IF NOT EXISTS punishments (
  `id` INT NOT NULL AUTO_INCREMENT,
  `target` VARCHAR(40) NOT NULL,
  `issued_by` VARCHAR(40) NOT NULL,
  `type` VARCHAR(10) NOT NULL,
  `reason` VARCHAR(260) NOT NULL,
  `revoked` BOOL NOT NULL DEFAULT FALSE,
  `revoked_by` VARCHAR(40) DEFAULT NULL,
  `expires_at` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
);


# Lobby news bar table
CREATE TABLE IF NOT EXISTS news (
  `id` INT NOT NULL AUTO_INCREMENT,
  `val` VARCHAR(280) NOT NULL,
  `updated_by` INT NOT NULL,
  `last_updated_at` TIMESTAMP NOT NULL,
  FOREIGN KEY (`updated_by`) REFERENCES accounts(`iid`),
  PRIMARY KEY (`id`)
);


# Create index on punishments
# CREATE INDEX full_uuid ON punishments ()