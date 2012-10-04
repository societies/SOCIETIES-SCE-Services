-- phpMyAdmin SQL Dump
-- version 3.3.9
-- http://www.phpmyadmin.net
--
-- Serveur: localhost
-- Généré le : Lun 21 Novembre 2011 à 12:37
-- Version du serveur: 5.5.8
-- Version de PHP: 5.3.5

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données: `asocom`
--

-- --------------------------------------------------------

--
-- Structure de la table `main`
--

CREATE TABLE IF NOT EXISTS `main` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `json` varchar(50000) NOT NULL,
  `SSID` varchar(64) NOT NULL,
  `Time` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1146 ;

CREATE TABLE IF NOT EXISTS `lstatus` (
  `SSID` varchar(64) NOT NULL,
  `id` int(10) NOT NULL,
  PRIMARY KEY (`SSID`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `ustatus` (
  `uid` varchar(200) NOT NULL,
  `id` int(10) NOT NULL,
  `SSID` varchar(64) NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `ucheckin` (
  `uuid` int(20) NOT NULL AUTO_INCREMENT,
  `uid` varchar(200) NOT NULL,
  `SSID` varchar(64) NOT NULL,
  `Time` bigint NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1146 ;
--
-- Contenu de la table `main`
--