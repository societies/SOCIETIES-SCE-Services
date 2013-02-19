#Networking Zone tables
CREATE TABLE socnetzone.nzzones (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mainzone` int(11) DEFAULT 0,
  `zonelocation` varchar(255) DEFAULT NULL,
  `zonelocdisplay` varchar(255) DEFAULT NULL,
  `zonename` varchar(255) DEFAULT NULL,
  `zonetopics` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

INSERT INTO socnetzone.nzzones 
(`id`,`mainzone`,`zonelocation`,`zonename`,`zonetopics`,`zonelocdisplay`) 
VALUES (1,1,null,'NZone Main Zone',null,null);

INSERT INTO socnetzone.nzzones 
(`id`,`mainzone`,`zonelocation`,`zonename`,`zonetopics`,`zonelocdisplay`) 
VALUES (2,0,'zoneA','Networking Zone A','Cloud Computing','Room 1');

INSERT INTO socnetzone.nzzones 
(`id`,`mainzone`,`zonelocation`,`zonename`,`zonetopics`,`zonelocdisplay`) 
VALUES (3,0,'zoneB','Networking Zone B','Internet Of Things','Room 2');

INSERT INTO socnetzone.nzzones 
(`id`,`mainzone`,`zonelocation`,`zonename`,`zonetopics`,`zonelocdisplay`) 
VALUES (4,0,'zoneC','Networking Zone C','The Future of The Internet','Room 3');

commit;

# select * from  socnetzone.nzzones
