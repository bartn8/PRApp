-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versione server:              10.1.19-MariaDB - mariadb.org binary distribution
-- S.O. server:                  Win32
-- HeidiSQL Versione:            9.2.0.4974
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dump della struttura del database prapp
CREATE DATABASE IF NOT EXISTS `prapp` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */;
USE `prapp`;


-- Dump della struttura di vista prapp.andamento_evento
-- Creazione di una tabella temporanea per risolvere gli errori di dipendenza della vista
CREATE TABLE `andamento_evento` (
	`nome_evento` VARCHAR(100) NOT NULL COLLATE 'utf8_unicode_ci',
	`data_evento` TIMESTAMP NOT NULL,
	`chiusura_prevendite` TIMESTAMP NOT NULL,
	`prezzo_prevendita` FLOAT NOT NULL,
	`prevendite_vendute` BIGINT(21) NOT NULL,
	`guadagno` DOUBLE NULL
) ENGINE=MyISAM;


-- Dump della struttura di tabella prapp.entrata
CREATE TABLE IF NOT EXISTS `entrata` (
  `seq` int(11) NOT NULL AUTO_INCREMENT,
  `id_evento` int(11) NOT NULL,
  `id_prevendita` int(11) NOT NULL,
  `data_entrata` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`),
  UNIQUE KEY `chk_unique` (`id_evento`,`id_prevendita`),
  KEY `id_prevendita` (`id_prevendita`),
  CONSTRAINT `entrata_ibfk_1` FOREIGN KEY (`id_evento`) REFERENCES `evento` (`id`),
  CONSTRAINT `entrata_ibfk_2` FOREIGN KEY (`id_prevendita`) REFERENCES `prevendita` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Dump dei dati della tabella prapp.entrata: ~0 rows (circa)
/*!40000 ALTER TABLE `entrata` DISABLE KEYS */;
/*!40000 ALTER TABLE `entrata` ENABLE KEYS */;


-- Dump della struttura di vista prapp.entrata_full
-- Creazione di una tabella temporanea per risolvere gli errori di dipendenza della vista
CREATE TABLE `entrata_full` (
	`data_entrata` TIMESTAMP NOT NULL,
	`nome_evento` VARCHAR(100) NOT NULL COLLATE 'utf8_unicode_ci',
	`nome_pr` VARCHAR(100) NOT NULL COLLATE 'utf8_unicode_ci',
	`cognome_pr` VARCHAR(100) NOT NULL COLLATE 'utf8_unicode_ci',
	`tel_pr` VARCHAR(50) NOT NULL COLLATE 'utf8_unicode_ci',
	`nome_cliente` VARCHAR(100) NOT NULL COLLATE 'utf8_unicode_ci',
	`cognome_cliente` VARCHAR(100) NOT NULL COLLATE 'utf8_unicode_ci',
	`tel_cliente` VARCHAR(50) NOT NULL COLLATE 'utf8_unicode_ci',
	`codice_prevendita` VARCHAR(64) NOT NULL COLLATE 'utf8_unicode_ci',
	`prezzo` FLOAT NOT NULL
) ENGINE=MyISAM;


-- Dump della struttura di tabella prapp.evento
CREATE TABLE IF NOT EXISTS `evento` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `data` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `chiusura_prevendite` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `prezzo` float NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Dump dei dati della tabella prapp.evento: ~0 rows (circa)
/*!40000 ALTER TABLE `evento` DISABLE KEYS */;
INSERT INTO `evento` (`id`, `nome`, `data`, `chiusura_prevendite`, `prezzo`) VALUES
	(1, 'ev1', '2017-10-27 16:44:16', '2017-10-25 16:44:16', 15);
/*!40000 ALTER TABLE `evento` ENABLE KEYS */;


-- Dump della struttura di tabella prapp.pr
CREATE TABLE IF NOT EXISTS `pr` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `livello` char(1) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'n',
  `nome` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `cognome` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `telefono` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `username` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `salt` varchar(16) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Dump dei dati della tabella prapp.pr: ~0 rows (circa)
/*!40000 ALTER TABLE `pr` DISABLE KEYS */;
INSERT INTO `pr` (`id`, `livello`, `nome`, `cognome`, `telefono`, `username`, `password`, `salt`) VALUES
	(1, 'n', 'matteo', 'mattei', '+390668785', 'test', '9f7e9dda9154f3d31f17e9ff9564c438af5f47bb788c9b564c1574b69428528a', 'abcdeabcdeabcdea');
/*!40000 ALTER TABLE `pr` ENABLE KEYS */;


-- Dump della struttura di tabella prapp.prevendita
CREATE TABLE IF NOT EXISTS `prevendita` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_evento` int(11) NOT NULL,
  `id_pr` int(11) NOT NULL,
  `data_vendita` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `nome_cliente` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `cognome_cliente` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `telefono_cliente` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `data_nascita_cliente` date NOT NULL,
  `codice_fiscale_cliente` varchar(16) COLLATE utf8_unicode_ci DEFAULT NULL,
  `codice_vendita` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `prezzo` float NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id_evento` (`id_evento`),
  KEY `id_pr` (`id_pr`),
  CONSTRAINT `prevendita_ibfk_1` FOREIGN KEY (`id_evento`) REFERENCES `evento` (`id`),
  CONSTRAINT `prevendita_ibfk_2` FOREIGN KEY (`id_pr`) REFERENCES `pr` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Dump dei dati della tabella prapp.prevendita: ~0 rows (circa)
/*!40000 ALTER TABLE `prevendita` DISABLE KEYS */;
INSERT INTO `prevendita` (`id`, `id_evento`, `id_pr`, `data_vendita`, `nome_cliente`, `cognome_cliente`, `telefono_cliente`, `data_nascita_cliente`, `codice_fiscale_cliente`, `codice_vendita`, `prezzo`) VALUES
	(1, 1, 1, '2017-10-17 16:46:32', 'qwe', 'rtt', '124412412', '2017-10-17', NULL, 'regeht', 15),
	(2, 1, 1, '2017-10-17 16:47:00', 'htth', 'thte', '45435', '2017-10-17', NULL, 'jyjyy', 15);
/*!40000 ALTER TABLE `prevendita` ENABLE KEYS */;


-- Dump della struttura di vista prapp.andamento_evento
-- Rimozione temporanea di tabella e creazione della struttura finale della vista
DROP TABLE IF EXISTS `andamento_evento`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` VIEW `andamento_evento` AS SELECT evento.nome AS nome_evento, evento.data AS data_evento, evento.chiusura_prevendite AS chiusura_prevendite, evento.prezzo AS prezzo_prevendita, COUNT(prevendita.id) AS prevendite_vendute, SUM(prevendita.prezzo) AS guadagno
FROM evento
INNER JOIN prevendita ON prevendita.id_evento = evento.id
GROUP BY evento.id
ORDER BY evento.nome ASC ;


-- Dump della struttura di vista prapp.entrata_full
-- Rimozione temporanea di tabella e creazione della struttura finale della vista
DROP TABLE IF EXISTS `entrata_full`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` VIEW `entrata_full` AS SELECT entrata.data_entrata AS data_entrata, evento.nome AS nome_evento, pr.nome AS nome_pr, pr.cognome AS cognome_pr, pr.telefono AS tel_pr, prevendita.nome_cliente AS nome_cliente, prevendita.cognome_cliente AS cognome_cliente, prevendita.telefono_cliente AS tel_cliente, prevendita.codice_vendita AS codice_prevendita, prevendita.prezzo AS prezzo
FROM entrata 
INNER JOIN evento ON evento.id = entrata.id_evento
INNER JOIN prevendita ON prevendita.id = entrata.id_prevendita
INNER JOIN pr ON pr.id = prevendita.id_pr
ORDER BY entrata.data_entrata DESC ;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
