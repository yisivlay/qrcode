ALTER TABLE `qrcode-default`.`roles`
  CHANGE `is_for_sign_up_process` `is_for_sign_up_process` TINYINT(1) DEFAULT 0 NOT NULL;

ALTER TABLE `qrcode-default`.`users`
  CHANGE `firsttime_login_remaining` `firsttime_login_remaining` TINYINT(1) NOT NULL,
  CHANGE `nonexpired` `nonexpired` TINYINT(1) NOT NULL,
  CHANGE `nonlocked` `nonlocked` TINYINT(1) NOT NULL,
  CHANGE `nonexpired_credentials` `nonexpired_credentials` TINYINT(1) NOT NULL,
  CHANGE `enabled` `enabled` TINYINT(1) NOT NULL,
  CHANGE `is_self_service_user` `is_self_service_user` TINYINT(1) DEFAULT b'0' NOT NULL,
  CHANGE `is_term_condition` `is_term_condition` TINYINT(1) DEFAULT b'0' NULL,
  CHANGE `from_sign_up` `from_sign_up` TINYINT(1) DEFAULT b'0' NOT NULL;