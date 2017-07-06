# Select correct database
USE neogames;


# remove old one
DROP USER 'dev_testing'@'localhost';

# create
CREATE USER 'dev_testing'@'localhost' IDENTIFIED BY 'super_secure';

# add privileges
GRANT ALL PRIVILEGES ON `neogames` . * TO 'dev_testing'@'localhost';

# reload perms
FLUSH PRIVILEGES;
