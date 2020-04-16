<?php

/*
 * PRApp  Copyright (C) 2019  Luca Bartolomei
 *
 * This file is part of PRApp.
 *
 *     PRApp is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     PRApp is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with PRApp.  If not, see <http://www.gnu.org/licenses/>.
 */

namespace com\model\db\table;

use PDO;
use com\model\Hash;
use com\model\db\enum\Ruolo;
use com\model\db\table\Table;
use com\model\db\wrapper\WUtente;
use com\model\db\wrapper\WPrevendita;
use com\model\db\table\Amministratore;
use com\model\db\wrapper\WRuoliMembro;
use com\model\db\wrapper\WTipoPrevendita;
use com\utils\DateTimeImmutableAdapterJSON;
use com\model\db\wrapper\WStatisticheEvento;
use com\model\db\wrapper\WStatistichePRStaff;
use com\model\db\wrapper\WStatistichePREvento;
use com\model\db\exception\InsertUpdateException;
use com\model\net\wrapper\update\UpdateNetWStaff;
use com\model\net\wrapper\insert\InsertNetWEvento;
use com\model\net\wrapper\update\UpdateNetWEvento;
use com\model\db\wrapper\WStatisticheCassiereStaff;
use com\model\db\wrapper\WStatisticheCassiereEvento;
use com\model\net\wrapper\update\UpdateNetWRuoliMembro;
use com\model\db\exception\NotAvailableOperationException;
use com\model\net\wrapper\insert\InsertNetWTipoPrevendita;
use com\model\net\wrapper\update\UpdateNetWTipoPrevendita;


class Amministratore extends Table
{
    //Rimosso rimuovi cliente

    /**
     * Aggiunge un evento dello staff.
     *
     * @param InsertNetWEvento $evento
     * @param int $idUtente
     * @param int $idStaff
     * @throws InsertUpdateException evento già presente
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return WEvento Wrapper completo con timestamp ultima modifica fasullo
     */
    public static function aggiungiEvento(InsertNetWEvento $evento, int $idUtente, int $idStaff): WEvento
    {
        // Devo essere sincronizzato con il database riguardo il fuso orario
        $conn = parent::getConnection(TRUE);

        $stmtInserimento = $conn->prepare("INSERT INTO evento (idCreatore, idStaff, nome, descrizione, inizio, fine, indirizzo, stato, idModificatore) VALUES (:idCreatore, :idStaff, :nome, :descrizione, :inizio, :fine, :indirizzo, :stato, :idModificatore)");
        $stmtInserimento->bindValue(":idCreatore", $idUtente, PDO::PARAM_INT);
        $stmtInserimento->bindValue(":idModificatore", $idUtente, PDO::PARAM_INT);
        $stmtInserimento->bindValue(":idStaff", $idStaff, PDO::PARAM_INT);
        $stmtInserimento->bindValue(":nome", $evento->getNome(), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":descrizione", $evento->getDescrizione(), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":inizio", $evento->getInizio()
            ->getDateTimeImmutable()
            ->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":fine", $evento->getFine()
            ->getDateTimeImmutable()
            ->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":indirizzo", $evento->getIndirizzo(), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":stato", $evento->getStato()
            ->toString(), PDO::PARAM_STR);

        try {
            $stmtInserimento->execute();
        } catch (PDOException $ex) {
            // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
            $conn = NULL;

            if ($ex->getCode() == Amministratore::UNIQUE_CODE || $ex->getCode() == Amministratore::INTEGRITY_CODE) // Codice di integrità.
                throw new InsertUpdateException("Evento già presente.");

            throw $ex;
        }

        $id = (int) $conn->lastInsertId();

        $conn = NULL;

        return $evento->getWEvento($id, $idUtente, $idUtente, new DateTimeImmutableAdapterJSON(new \DateTimeImmutable()));
    }

    /**
     * Modifica un evento dello staff.
     *
     * @param UpdateNetWEvento $evento
     * @param int $idUtente
     * @param int $idStaff
     * @param int $idEvento
     * @throws InsertUpdateException evento coincide con un altro
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return WEvento Wrapper completo con timestamp ultima modifica fasullo
     */
    public static function modificaEvento(UpdateNetWEvento $evento, int $idUtente, int $idStaff, int $idEvento): WEvento
    {
        // Devo essere sincronizzato con il database riguardo il fuso orario
        $conn = parent::getConnection(TRUE);

        $stmtModifica = $conn->prepare("UPDATE evento SET idModificatore = :idModificatore, descrizione = :descrizione, inizio = :inizio, fine = :fine, indirizzo = :indirizzo, stato = :stato, timestampUltimaModifica = CURRENT_TIMESTAMP WHERE id = :idEvento");
        $stmtModifica->bindValue(":idEvento", $idEvento, PDO::PARAM_INT);
        $stmtModifica->bindValue(":idModificatore", $idUtente, PDO::PARAM_INT);
        $stmtModifica->bindValue(":descrizione", $evento->getDescrizione(), PDO::PARAM_STR);
        $stmtModifica->bindValue(":inizio", $evento->getInizio()
            ->getDateTimeImmutable()
            ->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP), PDO::PARAM_STR);
        $stmtModifica->bindValue(":fine", $evento->getFine()
            ->getDateTimeImmutable()
            ->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP), PDO::PARAM_STR);
        $stmtModifica->bindValue(":indirizzo", $evento->getIndirizzo(), PDO::PARAM_STR);
        $stmtModifica->bindValue(":stato", $evento->getStato()
            ->toString(), PDO::PARAM_STR);

        try {
            $stmtModifica->execute();
        } catch (PDOException $ex) {
            // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
            $conn = NULL;

            if ($ex->getCode() == Amministratore::UNIQUE_CODE || $ex->getCode() == Amministratore::INTEGRITY_CODE) // Codice di integrità.
                throw new InsertUpdateException("evento coincide con un altro.");

            throw $ex;
        }

        // Recupero i dati aggiuntivi per restituire un WEvento.
        // Mi serve il nome, etc

        $stmtRecuperoDati = $conn->prepare("SELECT nome, idCreatore FROM evento WHERE id = :id");
        $stmtRecuperoDati->bindValue(":id", $evento->getId());
        $stmtRecuperoDati->execute();

        $fetch = $stmtRecuperoDati->fetch(PDO::FETCH_ASSOC);

        if (! $fetch) {
            throw new NotAvailableOperationException("Impossibile recuperare l'evento");
        }

        $nome = $fetch["nome"];
        $idCreatore = (int) $fetch["idCreatore"];

        $conn = NULL;

        return $evento->getWEvento($idEvento, $idStaff, $idCreatore, $nome, $idUtente, new DateTimeImmutableAdapterJSON(new \DateTimeImmutable()));
    }

    /**
     * Aggiunge un nuovo tipo di prevendita per un evento.
     *
     * @param InsertNetWTipoPrevendita $tipoPrevendita
     * @param int $idUtente
     * @param int $idEvento
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException l'utente non è amministratore per lo staff
     * @return WTipoPrevendita
     */
    public static function aggiungiTipoPrevendita(InsertNetWTipoPrevendita $tipoPrevendita, int $idUtente, int $idEvento): WTipoPrevendita
    {
        // Devo essere sincronizzato con il database riguardo il fuso orario: aperturaVendite e chiusuraVendite sono influenzate dal fuso orario.
        $conn = parent::getConnection(TRUE);

        $stmtInserimento = $conn->prepare("INSERT INTO tipoPrevendita (idEvento, nome, descrizione, prezzo, aperturaPrevendite, chiusuraPrevendite, idModificatore) VALUES (:idEvento, :nome, :descrizione, :prezzo, :aperturaPrevendite, :chiusuraPrevendite, :idModificatore)");
        $stmtInserimento->bindValue(":idEvento", $idEvento, PDO::PARAM_INT);
        $stmtInserimento->bindValue(":nome", $tipoPrevendita->getNome(), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":descrizione", $tipoPrevendita->getDescrizione(), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":prezzo", strval($tipoPrevendita->getPrezzo()), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":aperturaPrevendite", $tipoPrevendita->getAperturaVendite()
            ->getDateTimeImmutable()
            ->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":chiusuraPrevendite", $tipoPrevendita->getChiusuraVendite()
            ->getDateTimeImmutable()
            ->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":idModificatore", $idUtente);

        try {
            $stmtInserimento->execute();
        } catch (PDOException $ex) {
            // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
            $conn = NULL;

            if ($ex->getCode() == Amministratore::UNIQUE_CODE || $ex->getCode() == Amministratore::INTEGRITY_CODE) // Codice di integrità.
                throw new InsertUpdateException("tipo di prevenidita già presente.");

            if ($ex->getCode() == Amministratore::DATA_NON_VALIDA_CODE)
                throw new InsertUpdateException("La data d'inizio è nel passato. (".$tipoPrevendita->getAperturaVendite()->getDateTimeImmutable()->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP).")");

            throw $ex;
        }

        $id = (int) $conn->lastInsertId();

        $conn = NULL;

        return $tipoPrevendita->getWTipoPrevendita($id, $idEvento, $idUtente, new DateTimeImmutableAdapterJSON(new \DateTimeImmutable()));
    }

    /**
     * Modifica il tipo prevendita secondo le neccesità.
     *
     * @param UpdateNetWTipoPrevendita $tipoPrevendita
     * @throws InsertUpdateException tipo di prevendita già presente
     * @throws NotAvailableOperationException tipo prevendita non dell'evento selezionato
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     *        
     * @return WTipoPrevendita prevendita modificata
     */
    public static function modificaTipoPrevendita(UpdateNetWTipoPrevendita $tipoPrevendita, int $idUtente, int $idEvento): WTipoPrevendita
    {
        // Devo essere sincronizzato con il database riguardo il fuso orario: aperturaVendite e chiusuraVendite sono influenzate dal fuso orario.
        $conn = parent::getConnection(TRUE);

        //Devo fare un check ulteriore SQL sull'idEvento del tipo prevendita
        $stmtVerifica = $conn->prepare("SELECT idEvento FROM tipoPrevendita WHERE id = :idTipoPrevendita");
        $stmtVerifica->bindValue(":idTipoPrevendita", $prevendita->getId(), PDO::PARAM_INT);
        $stmtVerifica->execute();

        if ($stmtVerifica->rowCount() > 0) {
            $idEventoDB = $stmtVerifica->fetch(PDO::FETCH_ASSOC)["idEvento"];

            if($idEvento != $idEventoDB){
                throw new NotAvailableOperationException("Non puoi modificare un tipo prevendita di un evento non selezionato.");
            }
        }else{
            throw new NotAvailableOperationException("Non puoi modificare un tipo prevendita di un evento non selezionato.");
        }

        $stmtModifica = $conn->prepare("UPDATE tipoPrevendita SET nome = :nome, descrizione = :descrizione, prezzo = :prezzo, aperturaPrevendite = :aperturaPrevendite, chiusuraPrevendite = :chiusuraPrevendite, idModificatore = :idModificatore, timestampUltimaModifica = CURRENT_TIMESTAMP WHERE id = :id");
        $stmtModifica->bindValue(":id", $tipoPrevendita->getId(), PDO::PARAM_INT);
        $stmtModifica->bindValue(":nome", $tipoPrevendita->getNome(), PDO::PARAM_STR);
        $stmtModifica->bindValue(":descrizione", $tipoPrevendita->getDescrizione(), PDO::PARAM_STR);
        $stmtModifica->bindValue(":prezzo", strval($tipoPrevendita->getPrezzo()), PDO::PARAM_STR);
        $stmtModifica->bindValue(":aperturaPrevendite", $tipoPrevendita->getAperturaPrevendite()
            ->getDateTimeImmutable()
            ->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP), PDO::PARAM_STR);
        $stmtModifica->bindValue(":chiusuraPrevendite", $tipoPrevendita->getChiusuraPrevendite()
            ->getDateTimeImmutable()
            ->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP), PDO::PARAM_STR);
        $stmtModifica->bindValue(":idModificatore", $idUtente);

        try {
            $stmtModifica->execute();
        } catch (PDOException $ex) {
            // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
            $conn = NULL;

            if ($ex->getCode() == Amministratore::DATA_NON_VALIDA_CODE) // Codice di integrità.
                throw new InsertUpdateException("tipo di prevenidita non aggiornambile (data non valida).");

            throw $ex;
        }

        // Recupero i dati aggiuntivi per restituire un WTipoPrevenidta.
        // Mi serve il nome, etc

        $stmtRecuperoDati = $conn->prepare("SELECT idEvento, timestampUltimaModifica FROM tipoPrevendita WHERE id = :id");
        $stmtRecuperoDati->bindValue(":id", $tipoPrevendita->getId());

        $stmtRecuperoDati->execute();

        $fetch = $stmtRecuperoDati->fetch(PDO::FETCH_ASSOC);

        if (! $fetch) {
            throw new NotAvailableOperationException("Impossibile recuperare la prevendita");
        }

        $idEvento = (int) $fetch["idEvento"];
        $timestampUltimaModifica = $fetch["timestampUltimaModifica"];

        $conn = NULL;

        return $tipoPrevendita->getWTipoPrevendita($idEvento, $idUtente, $timestampUltimaModifica);
    }

    /**
     * Elimina un tipo di prevendita, solo se non sono state vendute prevendite di questo tipo.
     *
     * @param int $idTipoPrevendita
     * @param int $idEvento
     * @throws NotAvailableOperationException  tipo prevendita non dell'evento selezionato
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws InsertUpdateException non posso eliminare il tipo prevenidita
     * 
     * @return WTipoPrevendita tipo prevendita eliminata
     */
    public static function eliminaTipoPrevendita(int $idTipoPrevendita, int $idEvento): WTipoPrevendita
    {
        $conn = parent::getConnection();

        //Devo fare un check ulteriore SQL sull'idEvento del tipo prevendita
        $stmtVerifica = $conn->prepare("SELECT idEvento FROM tipoPrevendita WHERE id = :idTipoPrevendita");
        $stmtVerifica->bindValue(":idTipoPrevendita", $prevendita->getId(), PDO::PARAM_INT);
        $stmtVerifica->execute();

        if ($stmtVerifica->rowCount() > 0) {
            $idEventoDB = $stmtVerifica->fetch(PDO::FETCH_ASSOC)["idEvento"];

            if($idEvento != $idEventoDB){
                throw new NotAvailableOperationException("Non puoi eliminare un tipo prevendita di un evento non selezionato.");
            }
        }else{
            throw new NotAvailableOperationException("Non puoi eliminare un tipo prevendita di un evento non selezionato.");
        }

        // Devo verificare prima che non ci siano prevendite per questo tipo di prevendita.
        // Fatto un trigger per evitare

        //Ricavo i dati del tipo prevendita.
        $stmtSelezione = $conn->prepare("SELECT id, idEvento, nome, descrizione, prezzo, aperturaPrevendite, chiusuraPrevendite, idModificatore, timestampUltimaModifica FROM tipoPrevendita WHERE id = :idTipoPrevendita");
        $stmtSelezione->bindValue(":idTipoPrevendita", $idTipoPrevendita, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WTipoPrevendita::of($riga);
        }

        // Posso procedere: elimino il tipo di prevendita.

        $stmtElimina = $conn->prepare("DELETE FROM tipoPrevendita WHERE id = :idTipoPrevendita");
        $stmtElimina->bindValue(":idTipoPrevendita", $idTipoPrevendita, PDO::PARAM_INT);

        try {
            $stmtElimina->execute();
        } catch (PDOException $ex) {
            // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
            $conn = NULL;

            if ($ex->getCode() == Amministratore::VINCOLO_CODE){ // Codice di integrità.
                throw new InsertUpdateException("Esistono prevendite già vendute.");
            }

            throw $ex;
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Modifica i ruoli di un utente.
     *
     * @param UpdateNetWRuoliMembro $ruoliMembro
     * @param int $idStaff
     * @throws NotAvailableOperationException qualche condizione che ha dato errore
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws InsertUpdateException non è possibile modificare ruoli di un non-membro
     * @return WRuoliMembro restituisce i ruoli modificati
     */
    public static function modificaRuoliMembro(UpdateNetWRuoliMembro $ruoliMembro, int $idStaff): WRuoliMembro
    {
        $conn = parent::getConnection();

        // Dato che non mi fido dei dati, controllo che l'utente faccia parte dello staff.
        $stmtVerifica = $conn->prepare("SELECT COUNT(*) AS conto FROM membro WHERE idUtente = :idUtente AND idStaff = :idStaff");
        $stmtVerifica->bindValue(":idUtente", $ruoliMembro->getIdUtente(), PDO::PARAM_INT);
        $stmtVerifica->bindValue(":idStaff", $idStaff, PDO::PARAM_INT);
        $stmtVerifica->execute();

        $count = (int) $stmtVerifica->fetch(PDO::FETCH_ASSOC)["conto"];

        if ($count === 0) {
            $conn = NULL;

            throw new NotAvailableOperationException("Non è possibile modificare i ruoli di un non-membro.");
        }

        $queryInserimento = "INSERT INTO :tabella: (idUtente, idStaff) VALUES (:idUtente, :idStaff)";
        $queryRimozione = "DELETE FROM :tabella: WHERE idUtente = :idUtente AND idStaff = :idStaff";

        // Procedo con la modifica.
        // La modifica risulta un po' particolare...
        // Inoltre deve essere svolta in modalità atomica.

        try {
            $conn->beginTransaction();

            // Posso scrivere i ruoli che sono presenti.
            foreach ($ruoliMembro->getRuoli() as $ruolo) {
                $stmtInserimento = $conn->prepare(str_replace(":tabella:", strtolower($ruolo->toString()), $queryInserimento));
                $stmtInserimento->bindValue(":idStaff", $idStaff, PDO::PARAM_INT);
                $stmtInserimento->bindValue(":idUtente", $ruoliMembro->getIdUtente(), PDO::PARAM_INT);

                try {
                    $stmtInserimento->execute();
                } catch (PDOException $ex) {
                    // Se restituisce UNIQUE exception vuol dire che il ruolo non è stato modificato.
                    // Teoricamente non serve il rollback...
                    if ($ex->getCode() != Amministratore::UNIQUE_CODE && $ex->getCode() != Amministratore::INTEGRITY_CODE){ // Codice di integrità.
                        throw new InsertUpdateException("Impossibile aggiornare i ruoli dell'utente");
                    }

                    throw $ex;
                }
            }

            // Passo all'eliminazione dei ruoli non presenti.
            foreach (Ruolo::complement($ruoliMembro->getRuoli()) as $ruolo) {
                $stmtRimozione = $conn->prepare(str_replace(":tabella:", strtolower($ruolo->toString()), $queryRimozione));
                $stmtRimozione->bindValue(":idStaff", $$idStaff, PDO::PARAM_INT);
                $stmtRimozione->bindValue(":idUtente", $ruoliMembro->getIdUtente(), PDO::PARAM_INT);
                $stmtRimozione->execute();
            }
        }
        catch (InsertUpdateException $ex){
            // Annullo le modifiche
            $conn->rollBack();
            throw $ex;
        }
        catch (\PDOException $ex) {
            // Annullo le modifiche
            $conn->rollBack();
            throw $ex;
        }

        $conn->commit();
        $conn = NULL;

        return $ruoliMembro->getWRuoliMembro($idStaff);
    }

    /**
     * Restituisce le statistiche di un PR dello staff selezionato.
     *
     * @param int $idPR
     * @param int $idStaff
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return ?WStatistichePRStaff
     */
    public static function getStatistichePR(int $idPR, int $idStaff): ?WStatistichePRStaff
    {
        $conn = parent::getConnection();

        //Query XAMPP:
        //$query = "SELECT idUtente, idStaff, prevenditeVendute, ricavo FROM statistichePRStaff WHERE idUtente = :idUtente AND idStaff = :idStaff";

        //Query ALTERVISTA:
        $query = <<<EOT
        SELECT T.idUtente, T.idStaff, T.prevenditeVendute, T.ricavo 
        FROM (SELECT T1.idUtente, T1.idStaff, SUM(T1.prevenditeVendute) AS prevenditeVendute, SUM(T1.ricavo) AS ricavo
            FROM (SELECT pr.idUtente as idUtente, pr.idStaff AS idStaff, prevendita.idEvento AS idEvento, prevendita.idTipoPrevendita AS idTipoPrevendita, COUNT(prevendita.id) AS prevenditeVendute, SUM(tipoPrevendita.prezzo) AS ricavo
                FROM pr
                INNER JOIN evento ON evento.idStaff = pr.idStaff
                INNER JOIN prevendita ON prevendita.idEvento = evento.id AND prevendita.idPR = pr.idUtente
                INNER JOIN tipoPrevendita ON tipoPrevendita.idEvento = evento.id AND tipoPrevendita.id = prevendita.idTipoPrevendita
                WHERE prevendita.stato = 'VALIDA'
                GROUP BY pr.idUtente, pr.idStaff, prevendita.idEvento, prevendita.idTipoPrevendita) AS T1
            GROUP BY idUtente, idStaff) AS T
        WHERE T.idUtente = :idUtente AND T.idStaff = :idStaff
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idUtente", $idPR, PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idStaff", $idStaff, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WStatistichePRStaff::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Restituisce le statistiche di un cassiere dello staff selezionato.
     *
     * @param int $idCassiere
     * @param int $idStaff
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return NULL|WStatisticheCassiereStaff Restituisce le statistiche
     */
    public static function getStatisticheCassiere(int $idCassiere, int $idStaff): ?WStatisticheCassiereStaff
    {
        $conn = parent::getConnection();

        //Query XAMPP:
        //$query = "SELECT idUtente, idStaff, entrate FROM statisticheCassiereStaff WHERE idUtente = :idUtente AND idStaff = :idStaff";

        //Query ALTERVISTA:
        $query = <<<EOT
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
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idUtente", $idCassiere, PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idStaff", $idStaff, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WStatisticheCassiereStaff::of($riga);
        }
        
        $conn = NULL;

        return $result;
    }

    /**
     * Restituisce le statistiche di un evento.
     *
     * @param int $idEvento
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException l'utente non è amministratore per lo staff
     * @return WStatisticheEvento[] Restituisce un array con le statistiche
     */
    public static function getStatisticheEvento(int $idEvento): array
    {
        $conn = parent::getConnection();

        //Query XAMPP:
        //$query = "SELECT idEvento, idTipoPrevendita, prevenditeVendute, ricavo FROM statisticheEvento WHERE idEvento = :idEvento";

        //Query ALTERVISTA:
        $query = <<<EOT
        SELECT T.idEvento, T.idTipoPrevendita, T.nomeTipoPrevendita, T.prevenditeVendute, T.ricavo, T.prevenditeEntrate, T.prevenditeNonEntrate
        FROM (SELECT evento.id AS idEvento, prevendita.idTipoPrevendita AS idTipoPrevendita, tipoPrevendita.nome AS nomeTipoPrevendita, COUNT(prevendita.id) AS prevenditeVendute, SUM(tipoPrevendita.prezzo) AS ricavo, COUNT(entrata.seq) AS prevenditeEntrate, COUNT(prevendita.id)-COUNT(entrata.seq) AS prevenditeNonEntrate
            FROM evento 
            INNER JOIN prevendita ON prevendita.idEvento = evento.id 
            INNER JOIN tipoPrevendita ON tipoPrevendita.idEvento = evento.id AND tipoPrevendita.id = prevendita.idTipoPrevendita 
            LEFT JOIN entrata ON entrata.idPrevendita = prevendita.id
            GROUP BY evento.id, prevendita.idTipoPrevendita) AS T
        WHERE T.idEvento = :idEvento
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idEvento", $idEvento, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = array();

        while (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result[] = WStatisticheEvento::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Restituisce le prevendite di un evento.
     *
     * @param int $idEvento
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return array
     */
    public static function getPrevendite(int $idEvento): array
    {
        $conn = parent::getConnection();

        // Posso direttamente selezionare le prevendite dell'utente.
        $stmtSelezione = $conn->prepare("SELECT id, idEvento, idPR, nomeCliente, cognomeCliente, idTipoPrevendita, codice, stato, timestampUltimaModifica FROM prevendita WHERE idEvento = :idEvento");
        $stmtSelezione->bindValue(":idEvento", $idEvento, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = array();

        while (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result[] = WPrevendita::of($riga);
        }

        $conn = NULL;
        
        return $result;
    }

    /**
     * Rimuove un utente membro.
     *
     * @param int $idUtente membro da rimuovere
     * @param int $idStaff staff di cui fa parte il membro
     * @throws NotAvailableOperationException condizione sopraggiunta
     * @throws InsertUpdateException Non è stato possibile eliminare il membro
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * 
     * @return WUtente utente eliminato.
     */
    public static function rimuoviMembro(int $idUtente, int $idStaff) : WUtente
    {
        $conn = parent::getConnection();

        // Verifico che il membro sia effettivamente un membro.

        $stmtSelezione = $conn->prepare("SELECT * FROM membro WHERE idUtente = :idUtente AND idStaff = :idStaff");
        $stmtSelezione->bindValue(":idUtente", $idUtente, PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idStaff", $idStaff, PDO::PARAM_INT);
        $stmtSelezione->execute();

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {

            // Procedo con l'eliminazione.

            $stmtDelete = $conn->prepare("DELETE FROM membro WHERE idUtente = :idUtente AND idStaff = :idStaff");
            $stmtDelete->bindValue(":idUtente", $idUtente, PDO::PARAM_INT);
            $stmtDelete->bindValue(":idStaff", $idStaff, PDO::PARAM_INT);
            
            try {
                $stmtDelete->execute();
            } catch (PDOException $ex) {
                // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
                $conn = NULL;
    
                if ($ex->getCode() == Amministratore::VINCOLO_CODE){ // Codice di integrità.
                    throw new InsertUpdateException("Il membro non può essere rimosso: ultimo amministratore.");
                }
    
                throw $ex;
            }

            //Ricavo i dati dell'utente eliminato.
            $stmtSelezione = $conn->prepare("SELECT id, nome, cognome, telefono FROM utente WHERE id = :idUtente");
            $stmtSelezione->bindValue(":idUtente", $idUtente, PDO::PARAM_INT);
            $stmtSelezione->execute();
    
            $result = NULL;
    
            if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
                $result = WUtente::of($riga);
            }

            $conn = NULL;

            return $result;
        }

        $conn = NULL;

        throw new NotAvailableOperationException("Il membro non fa parte dello staff.");
    }

    /**
     * Cambia il codice di accesso per lo staff
     *
     * @param UpdateNetWStaff $staff Staff a cui cambiare il codice
     * @param int $idStaff
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     */
    public static function modificaCodiceAccesso(UpdateNetWStaff $staff, int $idStaff)
    {
        // Preparo l'hash da inserire...
        $hash = Hash::getSingleton()->hashPassword($staff->getCodiceAccesso());
        $staff->clear();

        $conn = parent::getConnection();

        // Cambio il codice....

        $stmtUpdate = $conn->prepare("UPDATE staff SET hash = :hash WHERE id = :idStaff");
        $stmtUpdate->bindValue(":idStaff", $idStaff, PDO::PARAM_INT);
        $stmtUpdate->bindValue(":hash", $hash, PDO::PARAM_STR);
        $stmtUpdate->execute();

        $conn = NULL;   
    }

    /**
     * Restituisce le statistiche del PR in un evento.
     *
     * @param int $idEvento
     * @param int $idPR
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return WStatistichePREvento[] Restituisce un array con le statistiche
     */
    public static function getStatistichePREvento(int $idEvento, int $idPR): array
    {
        $conn = parent::getConnection();

        //Query XAMPP:
        //$query = "SELECT idUtente, idStaff, idEvento, idTipoPrevendita, prevenditeVendute, ricavo FROM statistichePREvento WHERE idUtente = :idUtente AND idEvento = :idEvento";

        //Query ALTERVISTA:
        $query = <<<EOT
        SELECT T.idUtente, T.idStaff, T.idEvento, T.idTipoPrevendita, T.nomeTipoPrevendita, T.prevenditeVendute, T.ricavo 
        FROM (SELECT pr.idUtente as idUtente, pr.idStaff AS idStaff, prevendita.idEvento AS idEvento, prevendita.idTipoPrevendita AS idTipoPrevendita, tipoPrevendita.nome AS nomeTipoPrevendita, COUNT(prevendita.id) AS prevenditeVendute, SUM(tipoPrevendita.prezzo) AS ricavo
            FROM pr
            INNER JOIN evento ON evento.idStaff = pr.idStaff
            INNER JOIN prevendita ON prevendita.idEvento = evento.id AND prevendita.idPR = pr.idUtente
            INNER JOIN tipoPrevendita ON tipoPrevendita.idEvento = evento.id AND tipoPrevendita.id = prevendita.idTipoPrevendita
            WHERE prevendita.stato = 'VALIDA'
            GROUP BY pr.idUtente, pr.idStaff, prevendita.idEvento, prevendita.idTipoPrevendita) AS T
        WHERE T.idUtente = :idUtente AND T.idEvento = :idEvento
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idUtente", $idPR, PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idEvento", $idEvento, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = array();

        while (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result[] = WStatistichePREvento::of($riga);
        }

        $conn = NULL;

        return $result;
    }

     /**
     * Restituisce le statistiche del cassiere in un evento.
     *
     * @param int $idEvento
     * @param int $idCassiere 
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return ?WStatisticheCassiereEvento Restituisce il wrapper. Se non sono disponibili statistiche restituisce NULL.
     */
    public static function getStatisticheCassiereEvento(int $idEvento, int $idCassiere): ?WStatisticheCassiereEvento
    {
        $conn = parent::getConnection();

        //Query XAMPP:
        //$query = "SELECT idUtente, idStaff, idEvento, entrate FROM statisticheCassiereEvento  WHERE idUtente = :idUtente AND idEvento = :idEvento";

        //Query ALTERVISTA:
        $query = <<<EOT
        SELECT T.idUtente, T.idStaff, T.idEvento, T.entrate 
        FROM (SELECT cassiere.idUtente AS idUtente, cassiere.idStaff AS idStaff, evento.id AS idEvento, COUNT(entrata.seq) AS entrate
            FROM cassiere
            INNER JOIN entrata ON entrata.idCassiere = cassiere.idUtente
            INNER JOIN prevendita ON prevendita.id = entrata.idPrevendita
            INNER JOIN evento ON evento.idStaff = cassiere.idStaff AND prevendita.idEvento = evento.id
            GROUP BY cassiere.idUtente, cassiere.idStaff, evento.id)  AS T
        WHERE T.idUtente = :idUtente AND T.idEvento = :idEvento
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idUtente", $idCassiere, PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idEvento", $idEvento, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WStatisticheCassiereEvento::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    // Utilizzo sto metodo per i due metodi pubblici qua sotto.

    /**
     * Restituisce i ruoli di un membro.
     * 
     * @param int $idUtente
     * @param int $idStaff
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws NotAvailableOperationException quando sopraggiunge una condizione.
     */
    public static function getRuoli(int $idUtente, int $idStaff): ?WRuoliMembro
    {
        $conn = parent::getConnection();

        //Check che l'utente faccia parte dello staff
        $stmtVerifica = $conn->prepare("SELECT idStaff FROM membro WHERE idUtente = :idUtente");
        $stmtVerifica->bindValue(":idUtente", $idUtente, PDO::PARAM_INT);
        $stmtVerifica->execute();

        if ($stmtVerifica->rowCount() > 0) {
            $idStaffDB = $stmtVerifica->fetch(PDO::FETCH_ASSOC)["idStaff"];

            if($idStaff != $idStaffDB){
                throw new NotAvailableOperationException("Non puoi selezionare i ruoli di un membro che non sia dello staff selezionato");
            }
        }else{
            throw new NotAvailableOperationException("Non puoi selezionare i ruoli di un membro che non sia dello staff selezionato");
        }

        //Query per XAMPP:
        //$query = "SELECT id AS idUtente, dirut.idStaff as idStaff, nome, cognome, telefono, dirut.pr AS pr, dirut.cassiere AS cassiere, dirut.amministratore AS amministratore FROM utente INNER JOIN ruoliutente AS dirut ON dirut.idUtente = utente.id WHERE dirut.idStaff = :idStaff AND utente.id = :idUtente";

        //Query per ALTERVISTA:
        $query = <<<EOT
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
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idUtente", $idUtente, PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idStaff", $idStaff, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $wrapper = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $wrapper = WRuoliMembro::of($riga);
        }

        $conn = NULL;

        return $wrapper;
    }

    private function __construct()
    {}
}

