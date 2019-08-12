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

use com\model\Context;
use com\model\db\exception\NotAvailableOperationException;
use com\model\db\exception\AuthorizationException;
use com\model\db\wrapper\WUtente;
use com\model\db\wrapper\WCliente;
use com\model\db\wrapper\WEvento;
use com\model\db\wrapper\WTipoPrevendita;
use InvalidArgumentException;
use PDO;
use PDOException;
use com\model\db\wrapper\WDirittiUtente;
use com\model\net\wrapper\NetWId;

class Membro extends Table
{

    /**
     * Verifica se l'utente fa parte dello staff.
     *
     * @param int $idStaff
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws \Exception
     * @return boolean
     */
    private static function _isMembro(int $idStaff): bool
    {
        $conn = parent::getConnection();

        $stmt = $conn->prepare("SELECT COUNT(*) AS conto FROM membro WHERE idUtente = :idUtente AND idStaff = :idStaff");
        $stmt->bindValue(":idUtente", Context::getContext()->getUtente()->getId(), PDO::PARAM_INT);
        $stmt->bindValue(":idStaff", $idStaff, PDO::PARAM_INT);
        $stmt->execute();

        $count = (int) $stmt->fetch(PDO::FETCH_ASSOC)["conto"];

        $conn = NULL;

        return $count === 1;
    }

    /**
     * Verifica se l'utente fa parte dello staff.
     *
     * @param int $idEvento
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws \Exception
     * @return boolean
     */
    private static function _isMembroByEvento(int $idEvento): bool
    {
        $conn = parent::getConnection();

        $stmt = $conn->prepare("SELECT COUNT(*) AS conto FROM membro INNER JOIN evento ON membro.idStaff = evento.idStaff WHERE evento.id = :idEvento AND membro.idUtente = :idUtente");
        $stmt->bindValue(":idUtente", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
        $stmt->bindValue(":idEvento", $idEvento, PDO::PARAM_INT);
        $stmt->execute();

        $count = (int) $stmt->fetch(PDO::FETCH_ASSOC)["conto"];

        $conn = NULL;

        return $count === 1;
    }

    // /**
    // * Verifica se l'utente è membro dello staff.
    // *
    // * @param WStaff $staff
    // * @throws InvalidArgumentException parametri nulli o non validi
    // * @throws NotAvailableOperationException non si è loggati nel sistema
    // * @throws PDOException problemi del database (errore di connessione, errore nel database)
    // * @return boolean
    // */
    // public static function isMembro(WStaff $staff): bool
    // {
    // if (is_null($staff))
    // throw new InvalidArgumentException("Parametri nulli.");

    // if (! ($staff instanceof WStaff))
    // throw new InvalidArgumentException("Parametri non validi.");

    // // Verifico che si è loggati nel sistema.
    // if (!Context::getContext()->isValid())
    // throw new NotAvailableOperationException("Utente non loggato.");

    // return self::_isMembro($staff->getId());
    // }

    /**
     * Restituisce la lista dei membri dello staff.
     *
     * @param NetWId $staff
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws AuthorizationException non si fa parte dello staff
     * @return \com\model\db\wrapper\WUtente[] Lista dei membri dello staff
     */
    public static function getListaUtenti(NetWId $staff): array
    {
        // Verifico i parametri
        if (is_null($staff))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($staff instanceof NetWId))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isMembro($staff->getId()))
            throw new AuthorizationException("L'utente non fa parte dello staff.");

        // ---------------------------------------------------------------------------

        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT id, nome, cognome, telefono FROM utente INNER JOIN membro ON membro.idUtente = utente.id WHERE membro.idStaff = :idStaff");
        $stmtSelezione->bindValue(":idStaff", $staff->getId(), PDO::PARAM_INT);
        $stmtSelezione->execute();

        $lista = array();

        while (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $lista[] = WUtente::of($riga);
        }

        $conn = NULL;

        return $lista;
    }

    // Utilizzo sto metodo per i due metodi pubblici qua sotto.
    private static function getDiritti(int $idUtente, int $idStaff): ?WDirittiUtente
    {
        // Verifico i parametri
        if (is_null($idUtente) || is_null($idStaff))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! is_int($idUtente) || ! is_int($idStaff))
            throw new InvalidArgumentException("Parametri non del tipo giusto.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isMembro($idStaff))
            throw new AuthorizationException("L'utente non fa parte dello staff.");

        // ---------------------------------------------------------------------------

        $conn = parent::getConnection();

        //Query per XAMPP:
        //$query = "SELECT id AS idUtente, dirut.idStaff as idStaff, nome, cognome, telefono, dirut.pr AS pr, dirut.cassiere AS cassiere, dirut.amministratore AS amministratore FROM utente INNER JOIN dirittiutente AS dirut ON dirut.idUtente = utente.id WHERE dirut.idStaff = :idStaff AND utente.id = :idUtente";

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
            $wrapper = WDirittiUtente::of($riga);
        }

        $conn = NULL;

        return $wrapper;
    }

    /**
     * Restituisce i diritti dell'utente.
     *
     * @param NetWId $staff
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws AuthorizationException non si fa parte dello staff
     * @return ?WDirittiUtente wrapper dei diritti dell'utente
     */
    public static function getDirittiPersonali(NetWId $staff): ?WDirittiUtente
    {
        // Devo per forza verificare che l'utente sia loggato.
        // Altrimenti non riesco a restituire il context.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico anche il parametro.
        if (! ($staff instanceof NetWId))
            throw new InvalidArgumentException("Parametro non valido (INSTANCE).");

        return self::getDiritti(Context::getContext()->getUtente()->getId(), $staff->getId());
    }

    /**
     * Restituisce i diritti di un utente.
     *
     * @param NetWId $utente
     * @param NetWId $staff
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws AuthorizationException non si fa parte dello staff
     * @return ?WDirittiUtente wrapper dei diritti
     */
    public static function getDirittiUtente(NetWId $utente, NetWId $staff): ?WDirittiUtente
    {
        // Verifico i parametri
        if (is_null($utente) || is_null($staff))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($utente instanceof NetWId) || ! ($staff instanceof NetWId))
            throw new InvalidArgumentException("Parametri non validi.");

        return self::getDiritti($utente->getId(), $staff->getId());
    }

    /**
     * Restituisce gli eventi prodotti dallo staff.
     *
     * @param NetWId $staff
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws AuthorizationException non si fa parte dello staff
     * @return WEvento[] Lista degli eventi
     */
    public static function getListaEventi(NetWId $staff): array
    {
        // Verifico i parametri
        if (is_null($staff))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($staff instanceof NetWId))
            throw new InvalidArgumentException("Parametro non valido (INSTANCE).");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isMembro($staff->getId()))
            throw new AuthorizationException("L'utente non fa parte dello staff.");

        // ---------------------------------------------------------------------------

        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT id, idStaff, idCreatore, nome, inizio, descrizione, fine, indirizzo, stato, timestampUltimaModifica, idModificatore FROM evento WHERE idStaff = :idStaff");
        $stmtSelezione->bindValue(":idStaff", $staff->getId(), PDO::PARAM_INT);
        $stmtSelezione->execute();

        $lista = array();

        while (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $lista[] = WEvento::of($riga);
        }

        $conn = NULL;

        return $lista;
    }

    /**
     * Restituisce i tipi di prevendita disponibili per un evento
     *
     * @param NetWId $evento
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws AuthorizationException non si fa parte dello staff
     * @return \com\model\db\wrapper\WTipoPrevendita[] Lista dei tipi di prevendita per l'evento
     */
    public static function getTipiPrevendita(NetWId $evento): array
    {
        // Verifico i parametri
        if (is_null($evento))
            throw new InvalidArgumentException("Parametro nullo.");

        if (! ($evento instanceof NetWId))
            throw new InvalidArgumentException("Parametro non valido.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isMembroByEvento($evento->getId()))
            throw new AuthorizationException("L'utente non fa parte dello staff.");

        // ---------------------------------------------------------------------------

        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT id, idEvento, nome, descrizione, prezzo, aperturaPrevendite, chiusuraPrevendite, idModificatore, timestampUltimaModifica FROM tipoPrevendita WHERE idEvento = :idEvento");
        $stmtSelezione->bindValue(":idEvento", $evento->getId(), PDO::PARAM_INT);
        $stmtSelezione->execute();

        $lista = array();

        while (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $lista[] = WTipoPrevendita::of($riga);
        }

        $conn = NULL;

        return $lista;
    }

    /**
     * Restituisce la lista dei clienti di uno staff.
     *
     * @param NetWId $staff
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws AuthorizationException non si fa parte dello staff
     * @return \com\model\db\wrapper\WCliente[] Lista dei clienti di uno staff.
     */
    public static function getListaClienti(NetWId $staff): array
    {
        // Verifico i parametri
        if (is_null($staff))
            throw new InvalidArgumentException("Parametro nullo.");

            if (! ($staff instanceof NetWId))
            throw new InvalidArgumentException("Parametro non valido.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isMembro($staff->getId()))
            throw new AuthorizationException("L'utente non fa parte dello staff.");

        // ---------------------------------------------------------------------------

        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT id, idStaff, nome, cognome, telefono, dataDiNascita, codiceFiscale, timestampInserimento FROM cliente WHERE idStaff = :idStaff");
        $stmtSelezione->bindValue(":idStaff", $staff->getId(), PDO::PARAM_INT);
        $stmtSelezione->execute();

        $lista = array();

        while (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $lista[] = WCliente::of($riga);
        }

        $conn = NULL;

        return $lista;
    }

    private function __construct()
    {}
}

