-- *********************************************
-- * Standard SQL generation                   
-- *--------------------------------------------
-- * DB-MAIN version: 11.0.1              
-- * Generator date: Dec  4 2018              
-- * Generation date: Thu Jun  4 14:00:11 2020 
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

create table AMMINISTRATORE (
);

create table AMMINISTRATORE DI SISTEMA (
     id numeric(10) not null,
     nome char(50) not null,
     cognome char(50) not null,
     telefono char(50) not null,
     username char(50) not null,
     passwordSaltedHash char(256) not null,
     timestampCreazione date not null,
     constraint IDAMMINISTRATORE DI SISTEMA primary key (id));

create table CASSIERE (
);

create table EVENTO (
     id numeric(10) not null,
     nome char(50) not null,
     descrizione char(50) not null,
     inizio date not null,
     fine date not null,
     indirizzo char(50) not null,
     constraint IDEVENTO primary key (id));

create table EVENTO ANNULLATO (
);

create table EVENTO CONCLUSO (
);

create table MEMBRO (
     timestampIngressoStaff date not null,
     constraint IDMEMBRO primary key (, ));

create table PR (
);

create table PREVENDITA (
     id numeric(1) not null,
     nomeCliente char(50) not null,
     cognomeCliente char(50) not null,
     codice char(10) not null,
     timestampVendita date not null,
     constraint IDPREVENDITA primary key (id));

create table PREVENDITA ANULLATA (
);

create table PREVENDITA RIMBORSATA (
);

create table PREVENDITA TIMBRATA (
);

create table STAFF (
     id numeric(10) not null,
     nome char(50) not null,
     codiceAccessoSaltedHash char(256) not null,
     timestampCreazione date not null,
     constraint IDSTAFF primary key (id));

create table TIPOLOGIA PREVENDITA (
     id numeric(10) not null,
     nome char(50) not null,
     descrizione char(50) not null,
     prezzo numeric(10,2) not null,
     aperturaVendite date not null,
     chiusuraVendite date not null,
     constraint IDTIPOLOGIA PREVENDITA primary key (id));

create table UTENTE (
     id numeric(10) not null,
     nome char(50) not null,
     cognome char(50) not null,
     telefono char(50) not null,
     username char(50) not null,
     passwordSaltedHash char(256) not null,
     timestampCreazione date not null,
     tentativiLogin numeric(1) not null,
     constraint IDUTENTE primary key (id));

create table UTENTE BLOCCATO (
     timestampBlocco date not null);


-- Constraints Section
-- ___________________ 


-- Index Section
-- _____________ 

