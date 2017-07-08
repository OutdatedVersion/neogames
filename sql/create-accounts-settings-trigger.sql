USE neogames;

DELIMITER ;;

CREATE TRIGGER accounts_add_settings_row AFTER INSERT ON accounts FOR EACH ROW BEGIN
  INSERT INTO settings(account_id) VALUES (NEW.iid);
END ;;


# Cleanup
DELIMITER ;