/*TODO: Modificare le tabelle per raggiungere gli stati presenti nel documento di progetto*/

CREATE TABLE staff (
  id int NOT NULL AUTO_INCREMENT,
  idCreatore int NOT NULL,
  nome varchar(150) NOT NULL,
  hash varchar(255) NOT NULL,
  timestampCreazione timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (idCreatore) REFERENCES utente(id)
);

CREATE TABLE utente (
  id int NOT NULL AUTO_INCREMENT,
  /*Si potrebbe fare una tabella simile a membro*/
  tipologiaUtente ENUM('NORMALE', 'AMMINISTRATORE_SISTEMA') NOT NULL DEFAULT 'NORMALE',
  nome varchar(150) NOT NULL,
  cognome varchar(150) NOT NULL,
  telefono varchar(80) /* facciamo opzionale*/,
  username varchar(60) NOT NULL,
  hash varchar(255) NOT NULL,
  token varchar(255),
  scadenzaToken timestamp DEFAULT CURRENT_TIMESTAMP,
  timestampRegistrazione timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  tentativiLogin int NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT chkUsername UNIQUE (username),
  CONSTRAINT chkTokenEScadenza CHECK ((token IS NOT NULL AND scadenzaToken IS NOT NULL) OR token IS NULL)
);

/*
CREATE TABLE registro (
  seq int NOT NULL AUTO_INCREMENT,
  livello ENUM('INFO', 'WARNING', 'IMPORTANT') NOT NULL DEFAULT 'INFO',
  timestampInserimento timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  
);
*/

CREATE TABLE membro (
  idUtente int NOT NULL,
  idStaff int NOT NULL,
  timestampRegistrazione timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (idUtente, idStaff),
  FOREIGN KEY(idUtente) REFERENCES utente(id) ON DELETE CASCADE,
  FOREIGN KEY(idStaff) REFERENCES staff(id) ON DELETE CASCADE
);

CREATE TABLE pr (
  idUtente int NOT NULL,
  idStaff int NOT NULL,
  timestampRegistrazione timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (idUtente, idStaff),
  FOREIGN KEY(idUtente, idStaff) REFERENCES membro(idUtente, idStaff) ON DELETE CASCADE
);

CREATE TABLE cassiere (
  idUtente int NOT NULL,
  idStaff int NOT NULL,
  timestampRegistrazione timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (idUtente, idStaff),
  FOREIGN KEY(idUtente, idStaff) REFERENCES membro(idUtente, idStaff) ON DELETE CASCADE
);

CREATE TABLE amministratore (
  idUtente int NOT NULL,
  idStaff int NOT NULL,
  timestampRegistrazione timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (idUtente, idStaff),
  FOREIGN KEY(idUtente, idStaff) REFERENCES membro(idUtente, idStaff) ON DELETE CASCADE
);

/*
CREATE TABLE macchina (
	idUtente int NOT NULL,
	idStaff int NOT NULL,
	timestampRegistrazione timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(idUtente, idStaff),
	FOREIGN KEY(idUtente, idStaff) REFERENCES membro(idUtente, idStaff) ON DELETE CASCADE
);
*/

CREATE TABLE evento (
  id int NOT NULL AUTO_INCREMENT,
  idStaff int NOT NULL,
  idCreatore int NOT NULL, 
  nome varchar(150) NOT NULL,
  descrizione varchar(500),
  inizio timestamp NOT NULL,
  fine timestamp NOT NULL,
  indirizzo varchar(150) NOT NULL,
  stato ENUM('VALIDO', 'ANNULLATO') NOT NULL DEFAULT 'VALIDO',
  idModificatore int,
  timestampUltimaModifica timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (idStaff) REFERENCES staff(id),
  FOREIGN KEY (idCreatore) REFERENCES utente(id),
  FOREIGN KEY (idModificatore) REFERENCES utente(id),
  CONSTRAINT chkUnicitàEvento UNIQUE(idStaff, inizio, fine, nome, indirizzo)
  /*CONSTRAINT chkValiditàData CHECK (inizio < fine)*/
);

CREATE TABLE tipoPrevendita (
  id int NOT NULL AUTO_INCREMENT,
  idEvento int NOT NULL,
  nome varchar(150) NOT NULL,
  descrizione varchar(500) NOT NULL,
  prezzo float NOT NULL,
  aperturaPrevendite timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  chiusuraPrevendite timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  idModificatore int,
  timestampUltimaModifica timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (idEvento) REFERENCES evento(id),
  FOREIGN KEY (idModificatore) REFERENCES utente(id),
  CONSTRAINT chkUnicitàTipoPrevendita UNIQUE(idEvento, nome)
);

CREATE TABLE prevendita (
  id int NOT NULL AUTO_INCREMENT,
  idEvento int NOT NULL,
  idPR int NOT NULL,
  /* Inclusione della tabella cliente */
  nomeCliente varchar(150) NOT NULL,
  cognomeCliente varchar(150) NOT NULL,
  idTipoPrevendita int NOT NULL,
  timestampCreazione timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  codice varchar(10) NOT NULL,
  stato ENUM('VALIDA', 'ANNULLATA', 'ANNULLATA_NON_RIMBORSATA') NOT NULL DEFAULT 'VALIDA',
  timestampUltimaModifica timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (idEvento) REFERENCES evento(id),
  FOREIGN KEY (idPR) REFERENCES utente(id),
  FOREIGN KEY (idTipoPrevendita) REFERENCES tipoPrevendita(id)/*,*/
  /*CONSTRAINT chkUnicitàPrevendita UNIQUE(idEvento, idCliente), Da rimuovere perché l'unique considera il NULL... Dopo tutto non importa se il cliente compra più prevendite...*/
  /*CONSTRAINT chkUnicitàCodice UNIQUE(codice)*/
);

CREATE TABLE entrata (
  seq int NOT NULL AUTO_INCREMENT,
  idCassiere int NOT NULL,
  idPrevendita int NOT NULL,
  timestampEntrata timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (seq),
  FOREIGN KEY (idCassiere) REFERENCES utente(id),
  FOREIGN KEY (idPrevendita) REFERENCES prevendita(id),
  CONSTRAINT chkUnicitàEntrata UNIQUE(idPrevendita)
);

/*
	Views di supporto.
	Prima view per stabilire i ruoli di un utente nei vari staff.
*/

CREATE VIEW ruoliMembro AS 
SELECT utente.id AS idUtente, staff.id AS idStaff, COUNT(membro.idStaff) AS membro, COUNT(pr.idStaff) AS pr, COUNT(cassiere.idStaff) AS cassiere, COUNT(amministratore.idStaff) AS amministratore
FROM utente
INNER JOIN membro ON membro.idUtente = utente.id
INNER JOIN staff on membro.idStaff = staff.id
LEFT JOIN pr ON pr.idStaff = staff.id AND pr.idUtente = utente.id
LEFT JOIN cassiere ON cassiere.idStaff = staff.id AND cassiere.idUtente = utente.id
LEFT JOIN amministratore ON amministratore.idStaff = staff.id AND amministratore.idUtente = utente.id
GROUP BY utente.id, staff.id;

/*
            SELECT id AS idUtente, dirut.idStaff as idStaff, nome, cognome, telefono, dirut.pr AS pr, dirut.cassiere AS cassiere, dirut.amministratore AS amministratore 
            FROM utente 
            INNER JOIN (SELECT utente.id AS idUtente, staff.id AS idStaff, COUNT(membro.idStaff) AS membro, COUNT(pr.idStaff) AS pr, COUNT(cassiere.idStaff) AS cassiere, COUNT(amministratore.idStaff) AS amministratore
            FROM utente
            INNER JOIN membro ON membro.idUtente = utente.id
            INNER JOIN staff on membro.idStaff = staff.id
            LEFT JOIN pr ON pr.idStaff = staff.id AND pr.idUtente = utente.id
            LEFT JOIN cassiere ON cassiere.idStaff = staff.id AND cassiere.idUtente = utente.id
            LEFT JOIN amministratore ON amministratore.idStaff = staff.id AND amministratore.idUtente = utente.id
            GROUP BY utente.id, staff.id) AS dirut ON dirut.idUtente = utente.id 
            WHERE dirut.idStaff = :idStaff AND utente.id = :idUtente
*/

CREATE VIEW statistichePREvento AS
SELECT pr.idUtente as idUtente, pr.idStaff AS idStaff, prevendita.idEvento AS idEvento, prevendita.idTipoPrevendita AS idTipoPrevendita, tipoPrevendita.nome AS nomeTipoPrevendita, COUNT(prevendita.id) AS prevenditeVendute, SUM(tipoPrevendita.prezzo) AS ricavo
FROM pr
INNER JOIN evento ON evento.idStaff = pr.idStaff
INNER JOIN prevendita ON prevendita.idEvento = evento.id AND prevendita.idPR = pr.idUtente
INNER JOIN tipoPrevendita ON tipoPrevendita.idEvento = evento.id AND tipoPrevendita.id = prevendita.idTipoPrevendita
WHERE prevendita.stato = 'PAGATA'
GROUP BY pr.idUtente, pr.idStaff, prevendita.idEvento, prevendita.idTipoPrevendita;

/*
        SELECT T.idUtente, T.idStaff, T.idEvento, T.idTipoPrevendita, T.nomeTipoPrevendita, T.prevenditeVendute, T.ricavo 
        FROM (SELECT pr.idUtente as idUtente, pr.idStaff AS idStaff, prevendita.idEvento AS idEvento, prevendita.idTipoPrevendita AS idTipoPrevendita, tipoPrevendita.nome AS nomeTipoPrevendita, COUNT(prevendita.id) AS prevenditeVendute, SUM(tipoPrevendita.prezzo) AS ricavo
            FROM pr
            INNER JOIN evento ON evento.idStaff = pr.idStaff
            INNER JOIN prevendita ON prevendita.idEvento = evento.id AND prevendita.idPR = pr.idUtente
            INNER JOIN tipoPrevendita ON tipoPrevendita.idEvento = evento.id AND tipoPrevendita.id = prevendita.idTipoPrevendita
            WHERE prevendita.stato = 'PAGATA'
            GROUP BY pr.idUtente, pr.idStaff, prevendita.idEvento, prevendita.idTipoPrevendita) AS T
        WHERE T.idUtente = :idUtente AND T.idEvento = :idEvento
*/

CREATE VIEW statistichePRStaff AS
SELECT idUtente, idStaff, SUM(prevenditeVendute) AS prevenditeVendute, SUM(ricavo) AS ricavo
FROM statistichePREvento
GROUP BY idUtente, idStaff;

/*
SELECT T.idUtente, T.idStaff, T.prevenditeVendute, T.ricavo 
FROM (SELECT T1.idUtente, T1.idStaff, SUM(T1.prevenditeVendute) AS prevenditeVendute, SUM(T1.ricavo) AS ricavo
	FROM (SELECT pr.idUtente as idUtente, pr.idStaff AS idStaff, prevendita.idEvento AS idEvento, prevendita.idTipoPrevendita AS idTipoPrevendita, COUNT(prevendita.id) AS prevenditeVendute, SUM(tipoPrevendita.prezzo) AS ricavo
		FROM pr
		INNER JOIN evento ON evento.idStaff = pr.idStaff
		INNER JOIN prevendita ON prevendita.idEvento = evento.id AND prevendita.idPR = pr.idUtente
		INNER JOIN tipoPrevendita ON tipoPrevendita.idEvento = evento.id AND tipoPrevendita.id = prevendita.idTipoPrevendita
		WHERE prevendita.stato = 'PAGATA'
		GROUP BY pr.idUtente, pr.idStaff, prevendita.idEvento, prevendita.idTipoPrevendita) AS T1
	GROUP BY idUtente, idStaff) AS T
WHERE T.idUtente = :idUtente AND T.idStaff = :idStaff
*/

CREATE VIEW statistichePRTotali AS
SELECT idUtente, SUM(prevenditeVendute) AS prevenditeVendute, SUM(ricavo) AS ricavo
FROM statistichePRStaff
GROUP BY idUtente;

/*
SELECT T.idUtente, T.prevenditeVendute, T.ricavo 
FROM (SELECT T1.idUtente, SUM(T1.prevenditeVendute) AS prevenditeVendute, SUM(T1.ricavo) AS ricavo
	FROM (SELECT T2.idUtente, T2.idStaff, SUM(T2.prevenditeVendute) AS prevenditeVendute, SUM(T2.ricavo) AS ricavo
		FROM (SELECT pr.idUtente as idUtente, pr.idStaff AS idStaff, prevendita.idEvento AS idEvento, prevendita.idTipoPrevendita AS idTipoPrevendita, COUNT(prevendita.id) AS prevenditeVendute, SUM(tipoPrevendita.prezzo) AS ricavo
			FROM pr
			INNER JOIN evento ON evento.idStaff = pr.idStaff
			INNER JOIN prevendita ON prevendita.idEvento = evento.id AND prevendita.idPR = pr.idUtente
			INNER JOIN tipoPrevendita ON tipoPrevendita.idEvento = evento.id AND tipoPrevendita.id = prevendita.idTipoPrevendita
			WHERE prevendita.stato = 'PAGATA'
			GROUP BY pr.idUtente, pr.idStaff, prevendita.idEvento, prevendita.idTipoPrevendita) AS T2
		GROUP BY T2.idUtente, T2.idStaff) AS T1
	GROUP BY T1.idUtente) AS T
WHERE T.idUtente = :idUtente
*/

CREATE VIEW statisticheCassiereEvento AS
SELECT cassiere.idUtente AS idUtente, cassiere.idStaff AS idStaff, evento.id AS idEvento, COUNT(entrata.seq) AS entrate
FROM cassiere
INNER JOIN entrata ON entrata.idCassiere = cassiere.idUtente
INNER JOIN prevendita ON prevendita.id = entrata.idPrevendita
INNER JOIN evento ON evento.idStaff = cassiere.idStaff AND prevendita.idEvento = evento.id
GROUP BY cassiere.idUtente, cassiere.idStaff, evento.id;

/*
SELECT T.idUtente, T.idStaff, T.idEvento, T.entrate 
FROM (SELECT cassiere.idUtente AS idUtente, cassiere.idStaff AS idStaff, evento.id AS idEvento, COUNT(entrata.seq) AS entrate
	FROM cassiere
	INNER JOIN entrata ON entrata.idCassiere = cassiere.idUtente
	INNER JOIN prevendita ON prevendita.id = entrata.idPrevendita
	INNER JOIN evento ON evento.idStaff = cassiere.idStaff AND prevendita.idEvento = evento.id
	GROUP BY cassiere.idUtente, cassiere.idStaff, evento.id)  AS T
WHERE T.idUtente = :idUtente AND T.idEvento = :idEvento
*/

CREATE VIEW statisticheCassiereStaff AS
SELECT idUtente, idStaff, SUM(entrate) AS entrate
FROM statisticheCassiereEvento
GROUP BY idUtente, idStaff;

/*
SELECT T.idUtente, T.idStaff, T.entrate 
FROM (SELECT T1.idUtente, T1.idStaff, SUM(T1.entrate) AS entrate
	FROM (SELECT cassiere.idUtente AS idUtente, cassiere.idStaff AS idStaff, evento.id AS idEvento, COUNT(entrata.seq) AS entrate
		FROM cassiere
		INNER JOIN entrata ON entrata.idCassiere = cassiere.idUtente
		INNER JOIN prevendita ON prevendita.id = entrata.idPrevendita
		INNER JOIN evento ON evento.idStaff = cassiere.idStaff AND prevendita.idEvento = evento.id
		GROUP BY cassiere.idUtente, cassiere.idStaff, evento.id) AS T1
	GROUP BY T1.idUtente, T1.idStaff) AS T
WHERE T.idUtente = :idUtente AND T.idStaff = :idStaff
*/

CREATE VIEW statisticheCassiereTotali AS
SELECT idUtente, SUM(entrate) AS entrate
FROM statisticheCassiereStaff
GROUP BY idUtente;

/*
SELECT T.idUtente, T.entrate 
FROM (SELECT T1.idUtente, SUM(T1.entrate) AS entrate
	FROM (SELECT T2.idUtente, T2.idStaff, SUM(T2.entrate) AS entrate
		FROM (SELECT cassiere.idUtente AS idUtente, cassiere.idStaff AS idStaff, evento.id AS idEvento, COUNT(entrata.seq) AS entrate
			FROM cassiere
			INNER JOIN entrata ON entrata.idCassiere = cassiere.idUtente
			INNER JOIN prevendita ON prevendita.id = entrata.idPrevendita
			INNER JOIN evento ON evento.idStaff = cassiere.idStaff AND prevendita.idEvento = evento.id
			GROUP BY cassiere.idUtente, cassiere.idStaff, evento.id) AS T2
		GROUP BY T2.idUtente, T2.idStaff) AS T1
	GROUP BY T1.idUtente) AS T
WHERE T.idUtente = :idUtente

*/

CREATE VIEW statisticheEvento AS
SELECT evento.id AS idEvento, prevendita.idTipoPrevendita AS idTipoPrevendita, COUNT(prevendita.id) AS prevenditeVendute, SUM(tipoPrevendita.prezzo) AS ricavo 
FROM evento 
INNER JOIN prevendita ON prevendita.idEvento = evento.id 
INNER JOIN tipoPrevendita ON tipoPrevendita.idEvento = evento.id AND tipoPrevendita.id = prevendita.idTipoPrevendita 
GROUP BY evento.id, prevendita.idTipoPrevendita;

/*
SELECT T.idEvento, T.idTipoPrevendita, T.prevenditeVendute, T.ricavo 
FROM (SELECT evento.id AS idEvento, prevendita.idTipoPrevendita AS idTipoPrevendita, COUNT(prevendita.id) AS prevenditeVendute, SUM(tipoPrevendita.prezzo) AS ricavo 
	FROM evento 
	INNER JOIN prevendita ON prevendita.idEvento = evento.id 
	INNER JOIN tipoPrevendita ON tipoPrevendita.idEvento = evento.id AND tipoPrevendita.id = prevendita.idTipoPrevendita 
	GROUP BY evento.id, prevendita.idTipoPrevendita) AS T
WHERE T.idEvento = :idEvento
*/

/*
	Triggers
*/

/*
Verifica che il token sia scaduto: Altrimenti non posso aggiornare il token. 
*/

DELIMITER $$

CREATE TRIGGER verificheToken
BEFORE UPDATE ON utente
FOR EACH ROW BEGIN
	IF(OLD.token IS NOT NULL AND OLD.scadenzaToken IS NOT NULL) THEN
		IF(OLD.token <> NEW.TOKEN AND OLD.scadenzaToken > CURRENT_TIMESTAMP) THEN
			SIGNAL SQLSTATE '70003'
			SET MESSAGE_TEXT = 'Token non ancora scaduto';
		END IF;
	ELSEIF ((NEW.token IS NULL AND NEW.scadenzaToken IS NOT NULL) OR (NEW.token IS NOT NULL AND NEW.scadenzaToken IS NULL)) THEN
		SIGNAL SQLSTATE '70003'
		SET MESSAGE_TEXT = 'Inserimento token non valido';
	ELSEIF (NEW.scadenzaToken < CURRENT_TIMESTAMP AND OLD.token <> NEW.token) THEN
		SIGNAL SQLSTATE '70000'
		SET MESSAGE_TEXT = 'La scadenza del token deve avvenire nel futuro.';	
	END IF;
END$$

DELIMITER ;

/*
Verifica l'inserimento di un evento:
  1)La data di apertura e chiusura deve formare un intervallo temporale.
  2)Questo intervallo deve essere in una data presente o futura.
  3)L'evento deve essere VALIDO.
*/

DELIMITER $$

CREATE TRIGGER verificaEvento
BEFORE INSERT ON evento
FOR EACH ROW BEGIN
	IF (NEW.inizio > NEW.fine) THEN
		SIGNAL SQLSTATE '70000'
        SET MESSAGE_TEXT = 'Data non valida: inizio dopo la fine!';
	ELSEIF (NEW.fine < NEW.inizio) THEN
		SIGNAL SQLSTATE '70000'
        SET MESSAGE_TEXT = "Data non valida: fine prima dell\'inizio!";
	ELSEIF (NEW.inizio < CURRENT_TIMESTAMP) THEN
		SIGNAL SQLSTATE '70000'
        SET MESSAGE_TEXT = 'Data non valida: inizio in una data passata!';
	ELSEIF (NEW.stato <> 'VALIDO') THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = "Stato non valido: l\'evento può essere solo VALIDO!";
    END IF;
END$$

DELIMITER ;

/*
Verifica l'aggiornamento di un evento:
  0)ID non modificabile
  1)La data di apertura e chiusura deve formare un intervallo temporale.
  2)Questo intervallo deve essere in una data presente o futura.
  3)Lo stato deve seguire il relativo grafo (stati.png).
*/

DELIMITER $$

CREATE TRIGGER verificaAggiornamentoEvento
BEFORE UPDATE ON evento
FOR EACH ROW BEGIN
	IF (NEW.inizio > NEW.fine) THEN
		SIGNAL SQLSTATE '70000'
        SET MESSAGE_TEXT = 'Data non valida: inizio dopo la fine!';
	ELSEIF (NEW.fine < NEW.inizio) THEN
		SIGNAL SQLSTATE '70000'
        SET MESSAGE_TEXT = "Data non valida: fine prima dell\'inizio!";
	ELSEIF (NEW.inizio < CURRENT_TIMESTAMP AND OLD.inizio <> NEW.inizio) THEN
		SIGNAL SQLSTATE '70000'
        SET MESSAGE_TEXT = 'Data non valida: inizio in una data passata!';
	ELSEIF (NEW.stato = 'VALIDO' AND (OLD.stato <> 'VALIDO')) THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = "Stato non valido: un evento NON VALIDO rimane tale";
    ELSEIF (OLD.id <> NEW.id) THEN
		SIGNAL SQLSTATE '70003'
        SET MESSAGE_TEXT = "ID non modificabile, ricrea evento";
    END IF;
END$$

DELIMITER ;

/*
Verifica l'inserimento di un tipo prevendita:
  1)La data di apertura e chiusura deve formare un intervallo temporale.
  2)Questo intervallo deve essere in una data presente o futura.
  3)Questo intervallo NON deve superare la data dell'evento.
  4)Prezzo >= 0
*/

DELIMITER $$

CREATE TRIGGER verificaTipoPrevendita
BEFORE INSERT ON tipoPrevendita
FOR EACH ROW BEGIN
	DECLARE inizioEvento timestamp;
	DECLARE statoEvento varchar(20);

	SELECT inizio INTO inizioEvento FROM evento WHERE id = NEW.idEvento;
	SELECT stato INTO statoEvento FROM evento WHERE id = NEW.idEvento;

	IF (NEW.aperturaPrevendite > NEW.chiusuraPrevendite) THEN
		SIGNAL SQLSTATE '70000'
        SET MESSAGE_TEXT = 'Data non valida: inizio dopo la fine!';
	ELSEIF (NEW.chiusuraPrevendite < NEW.aperturaPrevendite) THEN
		SIGNAL SQLSTATE '70000'
        SET MESSAGE_TEXT = "Data non valida: fine prima dell\'inizio!";
	ELSEIF (NEW.aperturaPrevendite < CURRENT_TIMESTAMP) THEN
		SIGNAL SQLSTATE '70000'
        SET MESSAGE_TEXT = 'Data non valida: inizio in una data passata!';
	ELSEIF (NEW.chiusuraPrevendite > inizioEvento) THEN
		SIGNAL SQLSTATE '70000'
        SET MESSAGE_TEXT = 'Data non valida: chiusura prevendite dopo inizio evento!';
	ELSEIF (statoEvento <> 'VALIDO') THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = 'L\'evento non è valido!';	
    ELSEIF (NEW.prezzo < 0) THEN
		SIGNAL SQLSTATE '70003'
        SET MESSAGE_TEXT = 'Prezzo negativo';	
    END IF;
END$$

DELIMITER ;

/*
Verifica l'aggiornamento di un tipo prevendita:
  0)id non modificabile
  1)La data di apertura e chiusura deve formare un intervallo temporale.
  2)Questo intervallo deve essere in una data presente o futura.
  3)Questo intervallo NON deve superare la data dell'evento.
  4)Se sono state vendute prevendite allora non posso modificarne il prezzo.
*/

DELIMITER $$

CREATE TRIGGER verificaAggiornamentoTipoPrevendita
BEFORE UPDATE ON tipoPrevendita
FOR EACH ROW BEGIN
	DECLARE inizioEvento timestamp;
	DECLARE statoEvento varchar(20);
	DECLARE conteggio int;

	SELECT inizio INTO inizioEvento FROM evento WHERE id = OLD.idEvento;
	SELECT stato INTO statoEvento FROM evento WHERE id = OLD.idEvento;
	SELECT COUNT(*) INTO conteggio FROM prevendita WHERE idTipoPrevendita = OLD.id;

	IF (NEW.aperturaPrevendite > NEW.chiusuraPrevendite) THEN
		SIGNAL SQLSTATE '70000'
        SET MESSAGE_TEXT = 'Data non valida: inizio dopo la fine!';
	ELSEIF (NEW.chiusuraPrevendite < NEW.aperturaPrevendite) THEN
		SIGNAL SQLSTATE '70000'
        SET MESSAGE_TEXT = "Data non valida: fine prima dell\'inizio!";
	ELSEIF (NEW.aperturaPrevendite < CURRENT_TIMESTAMP AND OLD.aperturaPrevendite <> NEW.aperturaPrevendite) THEN
		SIGNAL SQLSTATE '70000'
        SET MESSAGE_TEXT = 'Data non valida: inizio in una data passata!';
	ELSEIF (NEW.chiusuraPrevendite > inizioEvento) THEN
		SIGNAL SQLSTATE '70000'
        SET MESSAGE_TEXT = 'Data non valida: chiusura prevendite dopo inizio evento!';
	ELSEIF (statoEvento <> 'VALIDO') THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = 'L\'evento non è valido!';
	ELSEIF (conteggio > 0 AND OLD.prezzo <> NEW.prezzo) THEN
		SIGNAL SQLSTATE '70003'
        SET MESSAGE_TEXT = 'Non puoi modificare il prezzo se hai venduto prevendite';
    ELSEIF (OLD.id <> NEW.id) THEN
		SIGNAL SQLSTATE '70003'
        SET MESSAGE_TEXT = 'ID non modificabile, ricrea tipo prevendita';
    END IF;
END$$

DELIMITER ;

/*
Verifica eliminazione tipo prevendita:
  1)Non devono essere state vendute prevendite.
  2)L'evento deve essere valido
*/

DELIMITER $$

CREATE TRIGGER verificaEliminazioneTipoPrevendita
BEFORE DELETE ON tipoPrevendita
FOR EACH ROW BEGIN

	DECLARE conteggio int;
	DECLARE statoEvento varchar(20);

	SELECT COUNT(*) INTO conteggio FROM prevendita WHERE idTipoPrevendita = OLD.id;
	SELECT stato INTO statoEvento FROM evento WHERE id = OLD.idEvento;
	
	IF(conteggio > 0) THEN
		SIGNAL SQLSTATE '70003'
		SET MESSAGE_TEXT = 'Impossibile eliminare tipo prevendita: ci sono prevendite vendute';
	ELSEIF (statoEvento <> 'VALIDO') THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = 'L\'evento non è valido!';
	END IF;
END$$

DELIMITER ;

/*
Verifica che la prevendita appena inserita sia effettivamente vendibile, cioè:
  1)I dati devono essere congruenti: evento e tipo prevendita compatibile.
  2)La data della vendita deve rientrare nel periodo di vendita.
  3)L'evento deve essere VALIDO.
  4)La prevendita deve essere VALIDA.
*/

DELIMITER $$

CREATE TRIGGER verificaPrevendita
BEFORE INSERT ON prevendita
FOR EACH ROW BEGIN
	DECLARE apertura timestamp;
    DECLARE chiusura timestamp;
    DECLARE statoEvento varchar(20);
	DECLARE verificaConteggio1 int;
	DECLARE verificaConteggio2 int;
    DECLARE ora timestamp;
        
	SELECT COUNT(t.id) INTO verificaConteggio1 FROM tipoPrevendita t WHERE t.idEvento = NEW.idEvento AND t.id = NEW.idTipoPrevendita;
	SELECT COUNT(e.id) INTO verificaConteggio2 FROM evento e WHERE e.id = NEW.idEvento;
	
  	SELECT aperturaPrevendite, chiusuraPrevendite INTO apertura, chiusura FROM tipoPrevendita WHERE id = NEW.idTipoPrevendita;
    SELECT stato INTO statoEvento FROM evento WHERE id = NEW.idEvento;
    
    SET ora := CURRENT_TIMESTAMP;
    
	IF (verificaConteggio1 <> 1 OR verificaConteggio2 <> 1) THEN
		SIGNAL SQLSTATE '70001'
        SET MESSAGE_TEXT = 'Dati non congruenti!';
	ELSEIF (ora < apertura OR ora > chiusura) THEN
		SIGNAL SQLSTATE '70000'
        SET MESSAGE_TEXT = 'Data non valida: non puoi vendere in questo momento!';
    ELSEIF (statoEvento <> 'VALIDO') THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = 'Evento non valido!';    
	ELSEIF (NEW.stato <> 'VALIDA') THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = 'La prevendita deve essere valida!';    
    END IF;
END$$

DELIMITER ;

/*
Verifica che la prevendita è annullabile (Pattern Immutable):
  0)Non ho modificato l'id
  1)Non ho modificato dataCreazione, tipoPrevendita
  2)La prevendita non è timbrata
  3)Lo stato deve seguire il relativo grafo (stati.png).
*/

DELIMITER $$

CREATE TRIGGER verificaAggiornamentoPrevendita
BEFORE UPDATE ON prevendita
FOR EACH ROW BEGIN
	DECLARE statoEvento varchar(20);
	DECLARE verificaEntrata int;
	
    SELECT stato INTO statoEvento FROM evento WHERE id = NEW.idEvento;
	SELECT COUNT(e.seq) INTO verificaEntrata FROM entrata e WHERE e.idPrevendita = OLD.id;
	
	IF (OLD.idTipoPrevendita <> NEW.idTipoPrevendita) THEN
		SIGNAL SQLSTATE '70003'
        SET MESSAGE_TEXT = 'Tipo prevendita non modificabile: ricrea la prevendita';
	ELSEIF (OLD.id <> NEW.id) THEN
		SIGNAL SQLSTATE '70003'
        SET MESSAGE_TEXT = 'ID non modificabile: ricrea la prevendita';
    ELSEIF (OLD.timestampCreazione <> NEW.timestampCreazione) THEN
		SIGNAL SQLSTATE '70000'
        SET MESSAGE_TEXT = 'Data non modificabile: ricrea la prevendita';
    ELSEIF (statoEvento <> 'VALIDO' AND NEW.stato <> OLD.stato AND NEW.stato = 'VALIDA')  THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = 'L\'Evento è scaduto: la prevendita non può essere valida';
	ELSEIF (NEW.stato = 'VALIDA' AND OLD.stato <> 'VALIDA')  THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = 'Ormai hai annullato la prevendita: fanne un\'altra';	
	ELSEIF (verificaEntrata > 0) THEN
		SIGNAL SQLSTATE '70003'
        SET MESSAGE_TEXT = 'Non puoi modificare la prevendita, è timbrata';	
		
    END IF;
END$$

DELIMITER ;

/* Creo un trigger per l'inserimento di un'entrata */

/*
Verifica che la prevendita appena inserita sia effettivamente valida, cioè:
  1)L'entrata deve essere effettuata nel tempo dell'evento.
  2)L'evento deve essere VALIDO.
  3)La prevendita deve essere consegnata o pagata.
*/

DELIMITER $$

CREATE TRIGGER verificaEntrata
BEFORE INSERT ON entrata
FOR EACH ROW BEGIN
	
	DECLARE ora timestamp;
	DECLARE inizioEvento timestamp;
    DECLARE fineEvento timestamp;
    DECLARE statoEvento varchar(20);
	DECLARE statoPrevendita varchar(20);
    
	SET ora := CURRENT_TIMESTAMP;
    
	SELECT evento.inizio, evento.fine, evento.stato INTO inizioEvento, fineEvento, statoEvento FROM evento INNER JOIN prevendita ON prevendita.idEvento = evento.id WHERE prevendita.id = NEW.idPrevendita;
  	SELECT stato INTO statoPrevendita FROM prevendita WHERE id = NEW.idPrevendita;
        
    
	IF (ora < inizioEvento OR ora > fineEvento) THEN
		SIGNAL SQLSTATE '70000'
        SET MESSAGE_TEXT = 'Data non valida: non puoi timbrare in questo momento!';
    ELSEIF (statoEvento <> 'VALIDO') THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = 'Evento non valido!'; 
	ELSEIF (statoPrevendita <> 'VALIDA') THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = 'Prevendita annullata!'; 
    END IF;
END$$

DELIMITER ;


/* Elimino un membro amministratore solo se c'è rimasto un amministratore */

DELIMITER $$

CREATE TRIGGER eliminaMembroAmministratore
BEFORE DELETE ON membro
FOR EACH ROW BEGIN
	
	DECLARE conteggioAmministratori int;
	DECLARE isAmministratore int;
    
	SELECT COUNT(idUtente) INTO conteggioAmministratori FROM amministratore WHERE idStaff = OLD.idStaff;
	SELECT COUNT(idUtente) INTO isAmministratore FROM amministratore WHERE idStaff = OLD.idStaff AND idUtente = OLD.idUtente;
    
	/* Se i conteggi sono uguali allora l'unico amministratore rimasto è quello da eliminare */
	
	IF (conteggioAmministratori = isAmministratore) THEN
		SIGNAL SQLSTATE '70003'
		SET MESSAGE_TEXT = 'Impossibile eliminare membro: rimasto solo lui amministratore';
	END IF;
END$$

DELIMITER ;

/* Un utente può creare al massimo uno staff */

DELIMITER $$

CREATE TRIGGER aggiungiStaff
BEFORE INSERT ON staff
FOR EACH ROW BEGIN
	
	DECLARE conteggioStaff int;
	
	SELECT COUNT(id) INTO conteggioStaff FROM staff WHERE idCreatore = NEW.idCreatore;
    
	IF (conteggioStaff > 0) THEN
		SIGNAL SQLSTATE '70003'
		SET MESSAGE_TEXT = 'Impossibile aggiungere staff: limite raggiunto';
	END IF;
END$$

DELIMITER ;