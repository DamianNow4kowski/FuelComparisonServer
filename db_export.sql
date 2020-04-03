SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;


CREATE TABLE `comment`
(
    `comment_id` int(11) NOT NULL,
    `user_id`    int(11) NOT NULL,
    `station_id` int(11) NOT NULL,
    `rating`     int(11) NOT NULL DEFAULT '0',
    `content`    varchar(255)     DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8;

CREATE TABLE `favourite_gas_stations`
(
    `user_id`    int(11) NOT NULL,
    `station_id` int(11) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8;

CREATE TABLE `available_fuel`
(
    `available_fuel_id` int(11) NOT NULL,
    `fuel_kind_id`      int(11) NOT NULL,
    `station_id`        int(11) NOT NULL,
    `deleted`           bit(1)  NOT NULL DEFAULT b'0'
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8;

CREATE TABLE `fuel_kind`
(
    `fuel_kind_id` int(11)      NOT NULL,
    `name`         varchar(255) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8;

CREATE TABLE `fuel_price`
(
    `fuel_price_id`     int(11)       NOT NULL,
    `available_fuel_id` int(11)       NOT NULL,
    `price`             decimal(5, 2) NOT NULL DEFAULT '0.00',
    `added_on`          date          NOT NULL,
    `rating`            int(11)       NOT NULL DEFAULT '1'
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8;

CREATE TABLE `gas_station`
(
    `station_id`          int(11)        NOT NULL,
    `agent_id`            int(11)        NOT NULL,
    `name`                varchar(255)   NOT NULL,
    `address`             varchar(255)   NOT NULL,
    `description`         varchar(255),
    `added_on`            date                    DEFAULT NULL,
    `electric_charging`   bit(1)         NOT NULL DEFAULT b'0',
    `accepted`            bit(1)         NOT NULL DEFAULT b'0',
    `for_disabled_people` bit(1)         NOT NULL DEFAULT b'0',
    `lat`                 DECIMAL(10, 8) NOT NULL,
    `lng`                 DECIMAL(11, 8) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8;

CREATE TABLE `opening_hours`
(
    `station_id` int(11) NOT NULL,
    `start_time` varchar(5) DEFAULT NULL,
    `end_time`   varchar(5) DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8;

CREATE TABLE `payment`
(
    `method_id` int(11)      NOT NULL,
    `type`      varchar(255) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8;

CREATE TABLE `station_agent_app`
(
    `agent_app_id` int(11) NOT NULL,
    `user_id`      int(11) NOT NULL,
    `station_id`   int(11) NOT NULL,
    `received`     date             DEFAULT NULL,
    `accepted`     bit(1)  NOT NULL DEFAULT b'0'
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8;

CREATE TABLE `station_payment_methods`
(
    `method_id`  int(11) NOT NULL,
    `station_id` int(11) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8;

CREATE TABLE `user`
(
    `user_id`       int(11)      NOT NULL,
    `username`      varchar(100) NOT NULL,
    `password`      varchar(255) NOT NULL,
    `email`         varchar(255) NOT NULL,
    `token`         varchar(50)  NOT NULL,
    `joined`        date         NOT NULL,
    `station_agent` bit(1) DEFAULT b'0',
    `superuser`     bit(1) DEFAULT b'0',
    `active`        bit(1) DEFAULT b'1',
    UNIQUE (token)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8;

CREATE TABLE `activity`
(
    `activity_id`   int(11) NOT NULL,
    `user_id`       int(11) NOT NULL,
    `activity_type` int(11) NOT NULL,
    `added_on`      date    NOT NULL,
    `detail_id`     int(11) DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8;

CREATE TABLE `ban`
(
    `ban_id`      int(11) NOT NULL,
    `user_id`     int(11) NOT NULL,
    `description` varchar(255) DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8;

--
-- Indeksy dla zrzutów tabel
--

--
-- Indexes for table `comment`
--
ALTER TABLE `comment`
    ADD PRIMARY KEY (`comment_id`),
    ADD KEY `FKcomment938109` (`user_id`),
    ADD KEY `FKcomment344239` (`station_id`);

--
-- Indexes for table `favourite_gas_stations`
--
ALTER TABLE `favourite_gas_stations`
    ADD PRIMARY KEY (`user_id`, `station_id`),
    ADD KEY `FKfavourite_577444` (`station_id`);

--
-- Indexes for table `available_fuel`
--
ALTER TABLE `available_fuel`
    ADD PRIMARY KEY (`available_fuel_id`),
    ADD KEY `FKfuel99091` (`fuel_kind_id`),
    ADD KEY `FKfuel99092` (`station_id`);

--
-- Indexes for table `fuel_kind`
--
ALTER TABLE `fuel_kind`
    ADD PRIMARY KEY (`fuel_kind_id`),
    ADD KEY `FKfuel_kind537119` (`fuel_kind_id`);

--
-- Indexes for table `fuel_price`
--
ALTER TABLE `fuel_price`
    ADD PRIMARY KEY (`fuel_price_id`),
    ADD KEY `FKfuel_price537119` (`available_fuel_id`);

--
-- Indexes for table `gas_station`
--
ALTER TABLE `gas_station`
    ADD PRIMARY KEY (`station_id`);

--
-- Indexes for table `opening_hours`
--
ALTER TABLE `opening_hours`
    ADD PRIMARY KEY (`station_id`);

--
-- Indexes for table `payment`
--
ALTER TABLE `payment`
    ADD PRIMARY KEY (`method_id`);

--
-- Indexes for table `station_agent_app`
--
ALTER TABLE `station_agent_app`
    ADD PRIMARY KEY (`agent_app_id`),
    ADD KEY `FKstation_ag511059` (`user_id`),
    ADD KEY `FKstation_ag630068` (`station_id`);

--
-- Indexes for table `station_payment_methods`
--
ALTER TABLE `station_payment_methods`
    ADD PRIMARY KEY (`method_id`, `station_id`),
    ADD KEY `FKstation_pa186542` (`station_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
    ADD PRIMARY KEY (`user_id`),
    ADD UNIQUE KEY `username` (`username`),
    ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `activity`
--
ALTER TABLE `activity`
    ADD PRIMARY KEY (`activity_id`),
    ADD KEY `FKuser_activ357910` (`user_id`);

--
-- Indexes for table `ban`
--
ALTER TABLE `ban`
    ADD PRIMARY KEY (`ban_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT dla tabeli `comment`
--
ALTER TABLE `comment`
    MODIFY `comment_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT dla tabeli `available_fuel`
--
ALTER TABLE `available_fuel`
    MODIFY `available_fuel_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT dla tabeli `fuel_kind`
--
ALTER TABLE `fuel_kind`
    MODIFY `fuel_kind_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT dla tabeli `fuel_price`
--
ALTER TABLE `fuel_price`
    MODIFY `fuel_price_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT dla tabeli `gas_station`
--
ALTER TABLE `gas_station`
    MODIFY `station_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT dla tabeli `payment`
--
ALTER TABLE `payment`
    MODIFY `method_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT dla tabeli `station_agent_app`
--
ALTER TABLE `station_agent_app`
    MODIFY `agent_app_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT dla tabeli `user`
--
ALTER TABLE `user`
    MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT dla tabeli `activity`
--
ALTER TABLE `activity`
    MODIFY `activity_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- Ograniczenia dla zrzutów tabel
--

--
-- Ograniczenia dla tabeli `comment`
--
ALTER TABLE `comment`
    ADD CONSTRAINT `FKcomment344239` FOREIGN KEY (`station_id`) REFERENCES `gas_station` (`station_id`) ON DELETE CASCADE,
    ADD CONSTRAINT `FKcomment938109` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `favourite_gas_stations`
--
ALTER TABLE `favourite_gas_stations`
    ADD CONSTRAINT `FKfavourite_251199` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
    ADD CONSTRAINT `FKfavourite_577444` FOREIGN KEY (`station_id`) REFERENCES `gas_station` (`station_id`);

--
-- Ograniczenia dla tabeli `available_fuel`
--
ALTER TABLE `available_fuel`
    ADD CONSTRAINT `FKfuel99091` FOREIGN KEY (`fuel_kind_id`) REFERENCES `fuel_kind` (`fuel_kind_id`),
    ADD CONSTRAINT `FKfuel99092` FOREIGN KEY (`station_id`) REFERENCES `gas_station` (`station_id`);

--
-- Ograniczenia dla tabeli `fuel_price`
--
ALTER TABLE `fuel_price`
    ADD CONSTRAINT `FKfuel_price537119` FOREIGN KEY (`available_fuel_id`) REFERENCES `available_fuel` (`available_fuel_id`);

--
-- Ograniczenia dla tabeli `opening_hours`
--
ALTER TABLE `opening_hours`
    ADD CONSTRAINT `FKopening_ho489543` FOREIGN KEY (`station_id`) REFERENCES `gas_station` (`station_id`);

--
-- Ograniczenia dla tabeli `station_agent_app`
--
ALTER TABLE `station_agent_app`
    ADD CONSTRAINT `FKstation_ag511059` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
    ADD CONSTRAINT `FKstation_ag630068` FOREIGN KEY (`station_id`) REFERENCES `gas_station` (`station_id`);

--
-- Ograniczenia dla tabeli `station_payment_methods`
--
ALTER TABLE `station_payment_methods`
    ADD CONSTRAINT `FKstation_pa186542` FOREIGN KEY (`station_id`) REFERENCES `gas_station` (`station_id`),
    ADD CONSTRAINT `FKstation_pa34057` FOREIGN KEY (`method_id`) REFERENCES `payment` (`method_id`);

--
-- Ograniczenia dla tabeli `activity`
--
ALTER TABLE `activity`
    ADD CONSTRAINT `FKuser_activ357910` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `ban`
--
ALTER TABLE `ban`
    ADD CONSTRAINT `FKuser_activ357912` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;


-- Example data
INSERT INTO `user` (`user_id`, `username`, `password`, `email`, `token`, `joined`, `station_agent`,
                    `superuser`, `active`)
VALUES (NULL, 'Administrator', 'admin', 'admin@admin.pl', '067e6162-3b6f-4ae2-a171-2470b63dff00',
        '2018-04-03', 0, 1, 1);
INSERT INTO `user` (`user_id`, `username`, `password`, `email`, `token`, `joined`, `station_agent`,
                    `superuser`, `active`)
VALUES (NULL, 'Andrzej', 'and', 'and@and.pl', '7ec02aab364b47a293ffb847d23381b3', '2018-06-10', 0,
        0, 1);
INSERT INTO `user` (`user_id`, `username`, `password`, `email`, `token`, `joined`, `station_agent`,
                    `superuser`, `active`)
VALUES (NULL, 'Zenon', 'zen', 'zen@zen.pl', '8a7da2e8908749e88391420aa97464f2', '2018-06-10', 1, 0,
        1);

INSERT INTO `gas_station` (`station_id`, `agent_id`, `name`, `address`, `added_on`,
                           `electric_charging`, `accepted`, `for_disabled_people`, `lat`, `lng`)
VALUES (NULL, '0', 'Testowa #1', 'Montelupich 3', '2018-05-04', b'0', b'1', b'0', '50.07383385',
        '19.94258981');
INSERT INTO `gas_station` (`station_id`, `agent_id`, `name`, `address`, `added_on`,
                           `electric_charging`, `accepted`, `for_disabled_people`, `lat`, `lng`)
VALUES (NULL, '0', 'Testowa #2', 'Lubicz 31', '2018-05-03', b'1', b'0', b'0', '50.06574584',
        '19.95795552');
INSERT INTO `gas_station` (`station_id`, `agent_id`, `name`, `address`, `added_on`,
                           `electric_charging`, `accepted`, `for_disabled_people`, `lat`, `lng`)
VALUES (NULL, '0', 'Orlen', 'Stanisława Konarskiego 10, 33-332 Kraków', '2018-06-10', b'1', b'0',
        b'0', '50.06419690', '19.92061011');
INSERT INTO `gas_station` (`station_id`, `agent_id`, `name`, `address`, `added_on`,
                           `electric_charging`, `accepted`, `for_disabled_people`, `lat`, `lng`)
VALUES (NULL, '0', 'Statoil #2', 'Swoszowicka, 30-001 Kraków', '2018-06-10', b'1', b'1', b'0',
        '50.03293693', '19.94885109');
INSERT INTO `gas_station` (`station_id`, `agent_id`, `name`, `address`, `added_on`,
                           `electric_charging`, `accepted`, `for_disabled_people`, `lat`, `lng`)
VALUES (NULL, '0', 'Shell', 'Józefa Mackiewicza 18, Kraków', '2018-06-10', b'0', b'0', b'1',
        '50.09534941', '19.94571291');
INSERT INTO `gas_station` (`station_id`, `agent_id`, `name`, `address`, `added_on`,
                           `electric_charging`, `accepted`, `for_disabled_people`, `lat`, `lng`)
VALUES (NULL, '0', 'BP', 'Stanisława Lema, 30-001 Kraków', '2018-06-10', b'0', b'1', b'1',
        '50.06478058', '19.98838887');
INSERT INTO `gas_station` (`station_id`, `agent_id`, `name`, `address`, `added_on`,
                           `electric_charging`, `accepted`, `for_disabled_people`, `lat`, `lng`)
VALUES (NULL, '0', 'BP', 'Gaik 84, Kraków', '2018-06-10', b'0', b'0', b'0', '50.10918159',
        '19.87841126');
INSERT INTO `gas_station` (`station_id`, `agent_id`, `name`, `address`, `added_on`,
                           `electric_charging`, `accepted`, `for_disabled_people`, `lat`, `lng`)
VALUES (NULL, '0', 'BP', 'Zakopiańska 56, Kraków', '2018-06-10', b'1', b'0', b'0', '50.01955559',
        '19.93234243');

INSERT INTO `comment` (`comment_id`, `user_id`, `station_id`, `rating`, `content`)
VALUES (NULL, '1', '1', '5', 'Super'),
       (NULL, '2', '1', '1', 'Fuuuj'),
       (NULL, '3', '2', '3', 'Może być'),
       (NULL, '1', '2', '5', 'Bardzo ładna stacja'),
       (NULL, '2', '3', '2', 'Niemiła obsługa'),
       (NULL, '3', '3', '5', 'Wszystko ok'),
       (NULL, '1', '4', '4', 'Miła obsługa'),
       (NULL, '2', '4', '2', 'Nie wróce drugi raz'),
       (NULL, '3', '5', '1', 'Tragedia'),
       (NULL, '1', '6', '4', 'Niskie ceny'),
       (NULL, '2', '6', '1', 'Trudny wjazd'),
       (NULL, '3', '6', '3', 'Stacja ok, ale cieżko na nią wjechać'),
       (NULL, '1', '7', '4', 'Super'),
       (NULL, '2', '8', '5', 'Moja ulubiona stacja'),
       (NULL, '3', '8', '5', 'Swietny, profesjanalny personel'),
       (NULL, '1', '8', '4', 'Wszystko ok');

INSERT INTO `fuel_kind` (`name`)
VALUES ('Petrol 95'),
       ('Petrol 98'),
       ('Natural Gas'),
       ('Diesel');

INSERT INTO `available_fuel` (`fuel_kind_id`, `station_id`)
VALUES (1, 1),
       (2, 1),
       (3, 2),
       (4, 2),
       (1, 3),
       (2, 3),
       (3, 4),
       (4, 4),
       (1, 5),
       (2, 5),
       (3, 6),
       (4, 6),
       (1, 7),
       (2, 7),
       (3, 8),
       (4, 8);

INSERT INTO `payment` (`method_id`, `type`)
VALUES (NULL, 'Gotówka'),
       (NULL, 'Karta'),
       (NULL, 'BLIK');

INSERT INTO `station_payment_methods` (`method_id`, `station_id`)
VALUES (3, 1),
       (2, 1),
       (3, 2),
       (1, 2),
       (1, 3),
       (3, 3),
       (3, 4),
       (2, 4),
       (3, 5),
       (1, 5),
       (3, 6),
       (1, 6),
       (1, 7),
       (2, 7),
       (3, 8),
       (2, 8);

INSERT INTO `favourite_gas_stations` (`user_id`, `station_id`)
VALUES (1, 1),
       (2, 2),
       (2, 3),
       (3, 1),
       (3, 3);



COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
