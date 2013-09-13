
#TODO Updaye tp check if the table exists
# (  SELECT 1 FROM Information_schema.tables WHERE table_name = 'nzuser' AND table_schema = 'socnetzone' )   
CREATE TABLE `socnetzone`.`nzuser` (
  `user_id` varchar(255) NOT NULL,
  `About` varchar(255) DEFAULT NULL,
  `Company` varchar(255) DEFAULT NULL,
  `DisplayName` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `Facebookid` varchar(255) DEFAULT NULL,
  `homelocation` varchar(255) DEFAULT NULL,
  `Linkedinid` varchar(255) DEFAULT NULL,
  `Position` varchar(255) DEFAULT NULL,
  `sex` varchar(255) DEFAULT NULL,
  `Twitterid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
);



#TODO Updaye tp check if the table exists
# (  SELECT 1 FROM Information_schema.tables WHERE table_name = 'nzshareinfo' AND table_schema = 'socnetzone' )   

CREATE TABLE `socnetzone`.`nzshareinfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `friendid` varchar(255) DEFAULT NULL,
  `myuserid` varchar(255) DEFAULT NULL,
  `sharehash` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ;


commit;

INSERT INTO `socnetzone`.`nzuser` (`user_id`, `Company`,`DisplayName`)
VALUES ('netzone.societies.local', 'NetZone', 'NetZone Admin');


INSERT INTO `socnetzone`.`nzuser` (`user_id`, `Company`,`DisplayName`)
VALUES ('user1.societies.local', 'Intel', 'Ted Crilly');

INSERT INTO `socnetzone`.`nzuser` (`user_id`, `Company`,`DisplayName`)
VALUES ('user2.societies.local', 'Intel', 'Dougal McGuire');

INSERT INTO `socnetzone`.`nzuser` (`user_id`, `Company`,`DisplayName`)
VALUES ('user3.societies.local', 'Intel', 'Jack Hackett');

INSERT INTO `socnetzone`.`nzuser`
(`user_id`, `Company`,`DisplayName`)
VALUES ('user4.societies.local', 'IBM', 'Dick Bryne');

INSERT INTO `socnetzone`.`nzuser`
(`user_id`, `Company`,`DisplayName`)
VALUES ('user5.societies.local', 'CompanyX', 'Noel Furlong');

INSERT INTO `socnetzone`.`nzuser`
(`user_id`, `Company`,`DisplayName`)
VALUES ('user6.societies.local', 'CompanyX', 'Larry Duff');

INSERT INTO `socnetzone`.`nzuser`
(`user_id`, `Company`,`DisplayName`)
VALUES ('user7.societies.local', 'CompanyX', 'Paul Stone');

INSERT INTO `socnetzone`.`nzuser`
(`user_id`, `Company`,`DisplayName`)
VALUES ('user8.societies.local', 'CompanyX', 'Fintan Stack');

INSERT INTO `socnetzone`.`nzuser`
(`user_id`, `Company`,`DisplayName`)
VALUES ('user9.societies.local', 'CompanyX', 'Austin Purcell');
INSERT INTO `socnetzone`.`nzuser`
(`user_id`, `Company`,`DisplayName`)
VALUES ('user10.societies.local', 'CompanyX', 'Todd Uncitious');
INSERT INTO `socnetzone`.`nzuser`
(`user_id`, `Company`,`DisplayName`)
VALUES ('user11.societies.local', 'CompanyX', 'Joan Doyle');


INSERT INTO `socnetzone`.`nzshareinfo` (`friendid`, `myuserid`, `sharehash`)
VALUES ('0', 'user1.societies.local', 7);
INSERT INTO `socnetzone`.`nzshareinfo` (`friendid`, `myuserid`, `sharehash`)
VALUES ('0', 'user2.societies.local', 7);
INSERT INTO `socnetzone`.`nzshareinfo` (`friendid`, `myuserid`, `sharehash`)
VALUES ('0', 'user3.societies.local', 7);
INSERT INTO `socnetzone`.`nzshareinfo` (`friendid`, `myuserid`, `sharehash`)
VALUES ('0', 'user4.societies.local', 7);
INSERT INTO `socnetzone`.`nzshareinfo` (`friendid`, `myuserid`, `sharehash`)
VALUES ('0', 'user5.societies.local', 7);
INSERT INTO `socnetzone`.`nzshareinfo` (`friendid`, `myuserid`, `sharehash`)
VALUES ('0', 'user6.societies.local', 7);
INSERT INTO `socnetzone`.`nzshareinfo` (`friendid`, `myuserid`, `sharehash`)
VALUES ('0', 'user7.societies.local', 7);
INSERT INTO `socnetzone`.`nzshareinfo` (`friendid`, `myuserid`, `sharehash`)
VALUES ('0', 'user8.societies.local', 7);
INSERT INTO `socnetzone`.`nzshareinfo` (`friendid`, `myuserid`, `sharehash`)
VALUES ('0', 'user9.societies.local', 7);
INSERT INTO `socnetzone`.`nzshareinfo` (`friendid`, `myuserid`, `sharehash`)
VALUES ('0', 'user10.societies.local', 7);
INSERT INTO `socnetzone`.`nzshareinfo` (`friendid`, `myuserid`, `sharehash`)
VALUES ('0', 'user11.societies.local', 7);

commit;






# select * from `socnetzone`.`nzuser`;
# select * from `socnetzone`.`nzuser`;