CREATE TABLE staff (
  id int NOT NULL AUTO_INCREMENT,
  nome varchar(150) NOT NULL,
  timestampCreazione timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  hash varchar(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE utente (
  id int NOT NULL AUTO_INCREMENT,
  nome varchar(150) NOT NULL,
  cognome varchar(150) NOT NULL,
  telefono varchar(80) NOT NULL,
  username varchar(60) NOT NULL,
  hash varchar(255) NOT NULL,
  token varchar(255),
  scadenzaToken timestamp DEFAULT CURRENT_TIMESTAMP,
  timestampRegistrazione timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT chkUsername UNIQUE (username),
  CONSTRAINT chkTokenEScadenza CHECK ((token IS NOT NULL AND scadenzaToken IS NOT NULL) OR token IS NULL)
  /*CONSTRAINT chkToken UNIQUE(token) meglio un trigger per evitare unique null*/
);

CREATE TABLE membro (
  idUtente int NOT NULL,
  idStaff int NOT NULL,
  PRIMARY KEY (idUtente, idStaff),
  FOREIGN KEY(idUtente) REFERENCES utente(id) ON DELETE CASCADE,
  FOREIGN KEY(idStaff) REFERENCES staff(id) ON DELETE CASCADE
);

CREATE TABLE pr (
  idUtente int NOT NULL,
  idStaff int NOT NULL,
  PRIMARY KEY (idUtente, idStaff),
  FOREIGN KEY(idUtente, idStaff) REFERENCES membro(idUtente, idStaff) ON DELETE CASCADE
);

CREATE TABLE cassiere (
  idUtente int NOT NULL,
  idStaff int NOT NULL,
  PRIMARY KEY (idUtente, idStaff),
  FOREIGN KEY(idUtente, idStaff) REFERENCES membro(idUtente, idStaff) ON DELETE CASCADE
);

CREATE TABLE amministratore (
  idUtente int NOT NULL,
  idStaff int NOT NULL,
  PRIMARY KEY (idUtente, idStaff),
  FOREIGN KEY(idUtente, idStaff) REFERENCES membro(idUtente, idStaff) ON DELETE CASCADE
);

CREATE TABLE cliente (
  id int NOT NULL AUTO_INCREMENT,
  idStaff int NOT NULL,
  nome varchar(150) NOT NULL,
  cognome varchar(150) NOT NULL,
  telefono varchar(80),
  dataDiNascita date NOT NULL,
  codiceFiscale varchar(16),
  timestampInserimento timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY(idStaff) REFERENCES staff(id)
  /*CONSTRAINT chkCodiceFiscale UNIQUE(idStaff, codiceFiscale)*/
);

CREATE TABLE evento (
  id int NOT NULL AUTO_INCREMENT,
  idStaff int NOT NULL,
  idCreatore int NOT NULL, 
  nome varchar(150) NOT NULL,
  descrizione varchar(500),
  inizio timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fine timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  indirizzo varchar(150) NOT NULL,
  stato ENUM('VALIDO', 'ANNULLATO', 'RIMBORSATO', 'PAGATO') NOT NULL DEFAULT 'VALIDO',
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
  idCliente int,
  idTipoPrevendita int NOT NULL,
  timestampCreazione timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  codice varchar(10) NOT NULL,
  stato ENUM('CONSEGNATA', 'PAGATA', 'ANNULLATA', 'RIMBORSATA') NOT NULL DEFAULT 'PAGATA',
  timestampUltimaModifica timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (idEvento) REFERENCES evento(id),
  FOREIGN KEY (idPR) REFERENCES utente(id),
  FOREIGN KEY (idCliente) REFERENCES cliente(id) ON DELETE SET NULL,
  FOREIGN KEY (idTipoPrevendita) REFERENCES tipoPrevendita(id)/*,*/
  /*CONSTRAINT chkUnicitàPrevendita UNIQUE(idEvento, idCliente), Da rimuovere perchè l'unique considera il NULL... Dopo tutto non importa se il cliente compra più prevendite...*/
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

CREATE VIEW dirittiUtente AS 
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
Verifica che il token sia univoco:
	1)Non viene selezionato il null: non fa conteggio.
	2)Deve essere univoco per garantire un accesso senza username.

*/

DELIMITER $$

CREATE TRIGGER uniqueToken
BEFORE UPDATE ON utente
FOR EACH ROW BEGIN
	DECLARE conteggioToken int;
	SELECT COUNT(token) INTO conteggioToken FROM utente WHERE token = NEW.token;
	
	IF(conteggioToken > 0) THEN
		SIGNAL SQLSTATE '70003'
		SET MESSAGE_TEXT = 'Token presente';
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
	ELSEIF (NEW.stato = 'PAGATO' AND OLD.stato <> 'VALIDO') THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = "Stato non valido: l\'evento può essere solo VALIDO!";
	ELSEIF (NEW.stato = 'PAGATO' AND OLD.stato <> 'VALIDO') THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = "Stato non valido: un evento è PAGATO solo se prima era VALIDO!";
	ELSEIF (NEW.stato = 'ANNULLATO' AND OLD.stato <> 'VALIDO') THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = "Stato non valido: un evento è ANNULLATO solo se prima era VALIDO!";
	ELSEIF (NEW.stato <> 'RIMBORSATO' AND OLD.stato = 'ANNULLATO') THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = "Stato non valido: un evento ANNULLATO può essere solo RIMBORSATO!";
    END IF;
END$$

DELIMITER ;

/*
Verifica l'inserimento di un tipo prevendita:
  1)La data di apertura e chiusura deve formare un intervallo temporale.
  2)Questo intervallo deve essere in una data presente o futura.
  3)Questo intervallo NON deve superare la data dell'evento.
*/

DELIMITER $$

CREATE TRIGGER verificaTipoPrevendita
BEFORE INSERT ON tipoPrevendita
FOR EACH ROW BEGIN
	DECLARE inizioEvento timestamp;

	SELECT inizio INTO inizioEvento FROM evento WHERE id = NEW.idEvento;

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
    END IF;
END$$

DELIMITER ;

/*
Verifica l'aggiornamento di un tipo prevendita:
  1)La data di apertura e chiusura deve formare un intervallo temporale.
  2)Questo intervallo deve essere in una data presente o futura.
  3)Questo intervallo NON deve superare la data dell'evento.
*/

DELIMITER $$

CREATE TRIGGER verificaAggiornamentoTipoPrevendita
BEFORE UPDATE ON tipoPrevendita
FOR EACH ROW BEGIN
	DECLARE inizioEvento timestamp;

	SELECT inizio INTO inizioEvento FROM evento WHERE id = OLD.idEvento;

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
    END IF;
END$$

DELIMITER ;

/*
Verifica eliminazione tipo prevendita:
  1)Non devono essere state vendute prevendite.
*/

DELIMITER $$

CREATE TRIGGER verificaEliminazioneTipoPrevendita
BEFORE DELETE ON tipoPrevendita
FOR EACH ROW BEGIN

	DECLARE conteggio int;

	SELECT COUNT(*) INTO conteggio FROM prevendita WHERE idTipoPrevendita = OLD.id;
	
	IF(conteggio > 0) THEN
		SIGNAL SQLSTATE '70003'
		SET MESSAGE_TEXT = 'Impossibile eliminare tipo prevendita: ci sono prevendite vendute';
	END IF;
END$$

DELIMITER ;

/*
Verifica che la prevendita appena inserita sia effettivamente vendibile, cioè:
  1)I dati devono essere congruenti: evento e tipo prevendita compatibile. Cliente dello giusto staff.
  2)La data della vendita deve rientrare nel periodo di vendita.
  3)L'evento deve essere VALIDO.
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
	SELECT COUNT(e.id) INTO verificaConteggio2 FROM cliente c, evento e WHERE e.idStaff = c.idStaff AND e.id = NEW.idEvento AND c.id = NEW.idCliente;
	
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
    END IF;
END$$

DELIMITER ;

/*
Verifica che la prevendita è aggiornabile:
  1)Il nuovo tipo prevendita è compatibile.
  2)Il nuovo tipo prevendita si può modificare solo se rispetta il periodo di vendita
  3)Lo stato deve seguire il relativo grafo (stati.png).
*/

DELIMITER $$

CREATE TRIGGER verificaAggiornamentoPrevendita
BEFORE UPDATE ON prevendita
FOR EACH ROW BEGIN
	DECLARE apertura timestamp;
    DECLARE chiusura timestamp;
	DECLARE statoEvento varchar(20);
	DECLARE verificaConteggio1 int;
	
	SELECT COUNT(t.id) INTO verificaConteggio1 FROM tipoPrevendita t, prevendita p WHERE p.idEvento = t.idEvento AND p.id = OLD.id AND t.id = NEW.idTipoPrevendita;
  	SELECT aperturaPrevendite, chiusuraPrevendite INTO apertura, chiusura FROM tipoPrevendita WHERE id = NEW.idTipoPrevendita;
    SELECT stato INTO statoEvento FROM evento WHERE id = NEW.idEvento;
	
	IF (verificaConteggio1 <> 1) THEN
		SIGNAL SQLSTATE '70001'
        SET MESSAGE_TEXT = 'Dati non congruenti!';
	ELSEIF ((NEW.timestampCreazione < apertura OR NEW.timestampCreazione > chiusura) AND OLD.timestampCreazione <> NEW.timestampCreazione) THEN
		SIGNAL SQLSTATE '70000'
        SET MESSAGE_TEXT = 'Data non valida: non puoi vendere in questo momento!';
    ELSEIF (statoEvento <> 'VALIDO' AND (NEW.stato = 'CONSEGNATA' OR NEW.stato = 'PAGATA'))  THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = "Stato non valido: la prevendita deve essere CONSEGNATA o PAGATA quando l\'evento è VALIDO!";
	ELSEIF (statoEvento <> 'ANNULLATO' AND (NEW.stato = 'ANNULLATA' OR NEW.stato = 'RIMBORSATA'))  THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = "Stato non valido: la prevendita deve essere ANNULLATA o RIMBORSATA quando l\'evento è ANNULLATO!";
	ELSEIF (OLD.stato = 'PAGATA' AND NEW.stato <> 'RIMBORSATA') THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = 'Stato non valido: una prevendita PAGATA può essere solo RIMBORSATA!';
	ELSEIF (NEW.stato = 'ANNULLATA' AND OLD.stato <> 'CONSEGNATA') THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = 'Stato non valido: una prevendita si può annullare solo se nello stato CONSEGNATA!';
	ELSEIF (NEW.stato = 'PAGATA' AND OLD.stato <> 'CONSEGNATA') THEN
		SIGNAL SQLSTATE '70002'
        SET MESSAGE_TEXT = 'Stato non valido: impossibile annullare una prevendita che non sia nello stato CONSEGNATA!';
    END IF;
END$$

DELIMITER ;

/*
Se un evento è annullato, lo porto automaticamente in rimborsato
  se tutte le prevendite sono state rimborsate o annullate.
*/

DELIMITER $$

CREATE TRIGGER verificaStatoEvento
AFTER UPDATE ON prevendita
FOR EACH ROW BEGIN
	DECLARE conteggioTotale int;
    DECLARE conteggioRimborsate int;
	DECLARE conteggioAnnullate int;
	DECLARE conteggioPagate int;
	DECLARE conteggioConsegnate int;
	DECLARE statoEvento varchar(20);
	
	SELECT COUNT(id) INTO conteggioTotale FROM prevendita WHERE idEvento = NEW.idEvento;
	SELECT COUNT(id) INTO conteggioRimborsate FROM prevendita WHERE idEvento = NEW.idEvento AND stato = 'RIMBORSATA';
	SELECT COUNT(id) INTO conteggioAnnullate FROM prevendita WHERE idEvento = NEW.idEvento AND stato = 'ANNULLATA';
	SELECT COUNT(id) INTO conteggioConsegnate FROM prevendita WHERE idEvento = NEW.idEvento AND stato = 'CONSEGNATA';
	SELECT COUNT(id) INTO conteggioPagate FROM prevendita WHERE idEvento = NEW.idEvento AND stato = 'PAGATA';
    SELECT stato INTO statoEvento FROM evento WHERE id = NEW.idEvento;
	
	IF (statoEvento = 'ANNULLATO') THEN
		UPDATE evento SET stato = 'RIMBORSATO' WHERE id = NEW.idEvento AND conteggioTotale = (conteggioAnnullate + conteggioRimborsate);
/*
	ELSEIF (statoEvento = 'RIMBORSATO') THEN
		UPDATE evento SET stato = 'ANNULLATO' WHERE id = NEW.idEvento AND conteggioTotale <> (conteggioAnnullate + conteggioRimborsate);
*/
	ELSEIF (statoEvento = 'VALIDO') THEN
		UPDATE evento SET stato = 'PAGATO' WHERE id = NEW.idEvento AND conteggioTotale = (conteggioPagate + conteggioAnnullate + conteggioRimborsate) AND inizio < CURRENT_TIMESTAMP;	
    END IF;
END$$

DELIMITER ;