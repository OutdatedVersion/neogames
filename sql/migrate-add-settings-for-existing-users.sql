USE neogames;

# remove old one
DROP PROCEDURE IF EXISTS insert_settings;
DELIMITER ;;


CREATE PROCEDURE insert_settings()
  BEGIN
    DECLARE current_id INT;
    DECLARE done BOOL DEFAULT FALSE;
    DECLARE iid_cursor CURSOR FOR SELECT iid FROM accounts;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done=TRUE;

    OPEN iid_cursor;

    reader: LOOP
      FETCH iid_cursor INTO  current_id;

      IF done THEN
        LEAVE reader;
      END IF;

      INSERT INTO settings(`account_id`) VALUES(current_id);
    END LOOP;

    CLOSE iid_cursor;
  END;
;;


DELIMITER ;

# Execute
CALL insert_settings();

# Cleanup
DROP PROCEDURE insert_settings;