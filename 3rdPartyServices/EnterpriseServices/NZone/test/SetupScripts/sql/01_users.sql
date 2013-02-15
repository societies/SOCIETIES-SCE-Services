CREATE TABLE `socnetzone`.`cssadvertisementrecordentry` (
  `ID` varchar(255) NOT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `Uri` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
); 

commit;




INSERT INTO `socnetzone`.`cssadvertisementrecordentry`
(`ID`,
`Name`,
`Uri`)
VALUES
(
'netzone.societies.local',	'Netzone Admin', ''
);

INSERT INTO `socnetzone`.`cssadvertisementrecordentry`
(`ID`,
`Name`,
`Uri`)
VALUES
(
'john.societies.local',	'John Murphy', ''
);


# Tempoary users below that don't exist ( i.e. no container but user data needed for tests

INSERT INTO `socnetzone`.`cssadvertisementrecordentry`
(`ID`,
`Name`,
`Uri`)
VALUES
(
'user1.societies.local',	'Ted Crilly', ''
);

INSERT INTO `socnetzone`.`cssadvertisementrecordentry`
(`ID`,
`Name`,
`Uri`)
VALUES
(
'user2.societies.local',	'Dougal McGuire', ''
);


INSERT INTO `socnetzone`.`cssadvertisementrecordentry`
(`ID`,
`Name`,
`Uri`)
VALUES
(
'user3.societies.local',	'Jack Hackett', ''
);


INSERT INTO `socnetzone`.`cssadvertisementrecordentry`
(`ID`,
`Name`,
`Uri`)
VALUES
(
'user4.societies.local',	'Dick Bryne', ''
);


INSERT INTO `socnetzone`.`cssadvertisementrecordentry`
(`ID`,
`Name`,
`Uri`)
VALUES
(
'user5.societies.local',	'Noel Furlong', ''
);


INSERT INTO `socnetzone`.`cssadvertisementrecordentry`
(`ID`,
`Name`,
`Uri`)
VALUES
(
'user6.societies.local',	'Larry Duff', ''
);



INSERT INTO `socnetzone`.`cssadvertisementrecordentry`
(`ID`,
`Name`,
`Uri`)
VALUES
(
'user7.societies.local',	'Paul Stone', ''
);

INSERT INTO `socnetzone`.`cssadvertisementrecordentry`
(`ID`,
`Name`,
`Uri`)
VALUES
(
'user8.societies.local',	'Fintan Stack', ''
);

INSERT INTO `socnetzone`.`cssadvertisementrecordentry`
(`ID`,
`Name`,
`Uri`)
VALUES
(
'user9.societies.local',	'Austin Purcell', ''
);


INSERT INTO `socnetzone`.`cssadvertisementrecordentry`
(`ID`,
`Name`,
`Uri`)
VALUES
(
'user10.societies.local',	'Todd Uncitious', ''
);

INSERT INTO `socnetzone`.`cssadvertisementrecordentry`
(`ID`,
`Name`,
`Uri`)
VALUES
(
'user11.societies.local',	'Joan Doyle', ''
);


commit;

