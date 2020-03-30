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
use PDOException;
use com\model\Context;
use com\model\db\table\Table;
use InvalidArgumentException;
use com\model\db\wrapper\WStaff;
use com\model\db\wrapper\WEvento;
use com\model\db\wrapper\WUtente;
use com\model\net\wrapper\NetWId;
use com\model\db\wrapper\WCliente;
use com\model\db\wrapper\WDirittiUtente;
use com\model\db\wrapper\WTipoPrevendita;
use com\model\db\exception\AuthorizationException;
use com\model\db\exception\NotAvailableOperationException;

class Membro extends Table
{
    /**
     * Restituisce la lista dei membri dello staff.
     *
     * @param WStaff $staff
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return \com\model\db\wrapper\WUtente[] Lista dei membri dello staff
     */
    public static function getListaUtenti(WStaff $staff) : array
    {
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

    public static function getDiritti(int $idUtente, int $idStaff): ?WDirittiUtente
    {
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
     * Restituisce gli eventi prodotti dallo staff.
     *
     * @param int $staff
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return WEvento[] Lista degli eventi
     */
    public static function getListaEventi(int $staff): array
    {
        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT id, idStaff, idCreatore, nome, inizio, descrizione, fine, indirizzo, stato, timestampUltimaModifica, idModificatore FROM evento WHERE idStaff = :idStaff");
        $stmtSelezione->bindValue(":idStaff", $staff, PDO::PARAM_INT);
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
     * @param int $evento
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return \com\model\db\wrapper\WTipoPrevendita[] Lista dei tipi di prevendita per l'evento
     */
    public static function getTipiPrevendita(int $evento) : array
    {
        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT id, idEvento, nome, descrizione, prezzo, aperturaPrevendite, chiusuraPrevendite, idModificatore, timestampUltimaModifica FROM tipoPrevendita WHERE idEvento = :idEvento");
        $stmtSelezione->bindValue(":idEvento", $evento, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $lista = array();

        while (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $lista[] = WTipoPrevendita::of($riga);
        }

        $conn = NULL;

        return $lista;
    }

    //Lista clienti rimossa

    /**
     * Restituisce l'evento dall'id
     * Restitusice solo se l'utente fa parte dello staff associato.
     * Restitusice solo se l'evento fa parte dello staff associato.
     * 
     * @param int $utente
     * @param int $staff
     * @param int $evento
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * 
     * @return WEvento evento richiesto
     */
    public static function getEvento(int $utente, int $staff, int $evento) : WEvento {
        $conn = parent::getConnection();
        
        $stmtSelezione = $conn->prepare("SELECT e.id, e.idStaff, e.idCreatore, e.nome, e.inizio, e.descrizione, e.fine, e.indirizzo, e.stato, e.timestampUltimaModifica, e.idModificatore FROM evento AS e INNER JOIN membro AS m ON m.idStaff = e.idStaff WHERE e.id = :idEvento AND m.idUtente = :idUtente AND m.idStaff = :idStaff");
        $stmtSelezione->bindValue(":idUtente", $utente, PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idStaff", $staff, PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idEvento", $evento, PDO::PARAM_INT);
        $stmtSelezione->execute();
        
        $result = NULL;
        
        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WEvento::of($riga);
        }
        
        $conn = NULL;
        
        return $result;
    }

    private function __construct()
    {}
}

