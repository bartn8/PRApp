-- *********************************************
-- * Standard SQL generation                   
-- *--------------------------------------------
-- * DB-MAIN version: 11.0.1              
-- * Generator date: Dec  4 2018              
-- * Generation date: Fri May  8 11:22:35 2020 
-- * LUN file: C:\Users\bartn\Documents\Progetti\PRApp\Ingegneria del software T\Progettazione\ER\Progetto_Ing_Software_ER.lun 
-- * Schema: SCHEMA/1 
-- ********************************************* 


-- Database Section
-- ________________ 

create database SCHEMA;


-- DBSpace Section
-- _______________


-- Tables Section
-- _____________ 

create table CASSIERE (
);

create table MEMBRO (
);

create table EVENTO (
     id numeric(10) not null,
     nome char(50) not null,
     descrizione char(50) not null,
     inizio date not null,
     fine date not null,
     indirizzo char(50) not null,
     stato char(1) not null);

create table AMMINISTRATORE (
);

create table PR (
);

create table UTENTE (
     id numeric(10) not null,
     nome char(50) not null,
     cognome char(50) not null,
     telefono char(50) not null,
     username char(50) not null,
     passwordSaltedHash char(256) not null,
     timestampCreazione date not null,
     tentativiLogin numeric(1) not null);

create table AMMINISTRATORE DI SISTEMA (
     id numeric(10) not null,
     nome char(50) not null,
     cognome char(50) not null,
     telefono char(50) not null,
     username char(50) not null,
     passwordSaltedHash char(256) not null,
     timestampCreazione date not null);

create table TIPOLOGIA PREVENDITA (
     id numeric(10) not null,
     nome char(50) not null,
     descrizione char(50) not null,
     prezzo numeric(10,2) not null,
     aperturaVendite date not null,
     chiusuraVendite date not null);

create table PREVENDITA (
     id numeric(1) not null,
     nomeCliente char(50) not null,
     cognomeCliente char(50) not null,
     codice char(10) not null);

create table UTENTE BLOCCATO (
     timestampBlocco date not null);

create table PREVENDITA ANULLATA (
);

create table PREVENDITA TIMBRATA (
);

create table PREVENDITA RIMBORSATA (
);

create table EVENTO CONCLUSO (
);

create table EVENTO ANNULLATO (
);

create table STAFF (
     id numeric(10) not null,
     nome char(50) not null,
     codiceAccessoSaltedHash char(256) not null,
     timestampCreazione date not null);


-- Constraints Section
-- ___________________ 


-- Index Section
-- _____________ 

