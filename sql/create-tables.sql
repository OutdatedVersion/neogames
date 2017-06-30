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
  `target_id` INT NOT NULL,
  `issued_by` VARCHAR(40) NOT NULL,
  `type` VARCHAR(10) NOT NULL,
  `reason` VARCHAR(260) NOT NULL,
  `expires_at` TIMESTAMP DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`target_id`) REFERENCES accounts(`iid`)
);