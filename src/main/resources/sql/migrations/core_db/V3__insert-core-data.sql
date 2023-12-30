--Insert Office
INSERT INTO `offices` (`parent_id`, `hierarchy`, `external_id`, `name`, `opening_date`) VALUES(NULL,'.','1','Head Office','2023-12-30');

--Insert Role
INSERT INTO `roles` (`name`, `description`, `is_disabled`, `code`, `is_for_sign_up_process`) VALUES('Super User','This position gives permission for all users','0','role.code.super-user','0');

--Insert Permission
INSERT INTO `permissions` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES('special','ALL_FUNCTIONS',NULL,NULL,'0');
INSERT INTO `permissions` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES('special','ALL_FUNCTIONS_READ',NULL,NULL,'0');
INSERT INTO `permissions` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES('special','CHECKER_SUPER_USER',NULL,NULL,'0');
INSERT INTO `permissions` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES('special','REPORTING_SUPER_USER',NULL,NULL,'0');

--Mapping role permission
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES('1','1');

--Insert User
INSERT INTO users(`office_id`, `username`, `firstname`, `lastname`, `fullname`, `password`, `email`, `gender`, `address`, `firsttime_login_remaining`, `nonexpired`, `nonlocked`, `nonexpired_credentials`, `enabled`, `last_time_password_updated`, `password_never_expires`, `is_self_service_user`, `attempts`, `lang`, `is_term_condition`, `from_sign_up`, `is_deleted`) VALUES(1, 'system', 'Super', 'User', 'Super User', '$2a$10$ujZdKtitPoMkd2icuOQCa.C0zJq8ciXa52/pNSikJ5CwkW8tR4y.S', 'info@caminfoservices.com', 1, 'Phnom Penh', 0, 1, 1, 1, 1, CURRENT_TIMESTAMP, 1, 0, 0, 'en-US', 0, 0, 0);

--Mapping User Role
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES('1','1');