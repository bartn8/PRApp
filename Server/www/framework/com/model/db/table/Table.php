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

abstract class Table
{

    private static $serverURL = "localhost";

    private static $databaseType = "mysql";

    private static $databaseName = "prapp";

    private static $username = "root";

    private static $password = "";

    private static $charset = "utf8";

    //SQLSTATE vari....
    
    protected const UNIQUE_CODE = 1062;
    
    protected const INTEGRITY_CODE = 23000;
    
    protected const DATI_INCONGRUENTI_CODE = 70001;
    
    protected const DATA_NON_VALIDA_CODE = 70000;
    
    protected const STATO_NON_VALIDO_CODE = 70002;

    protected const VINCOLO_CODE = 70003;

    static function loadParameters()
    {
        if (isset($GLOBALS['serverURL']))
            Table::$serverURL = $GLOBALS['serverURL'];

        if (isset($GLOBALS['databaseType']))
            Table::$databaseType = $GLOBALS['databaseType'];

        if (isset($GLOBALS['databaseName']))
            Table::$databaseName = $GLOBALS['databaseName'];

        if (isset($GLOBALS['username']))
            Table::$username = $GLOBALS['username'];

        if (isset($GLOBALS['password']))
            Table::$password = $GLOBALS['password'];

        if (isset($GLOBALS['charset']))
            Table::$charset = $GLOBALS['charset'];
    }

    protected static function getConnection($syncTimezone = TRUE/*$args=NULL*/) : PDO
    {
        //$tmp = new PDO(Table::$databaseType . ":host=" . Table::$serverURL . ";dbname=" . Table::$databaseName . ";charset=utf8", Table::$username, Table::$password);
        $tmp = new PDO(Table::$databaseType . ":host=" . Table::$serverURL . ";dbname=" . Table::$databaseName . ";charset=" . Table::$charset, Table::$username, Table::$password);
        $tmp->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

        //TODO: forse meglio assegnare al database UTC?
        if ($syncTimezone) {
            // https://stackoverflow.com/questions/34428563/set-timezone-in-php-and-mysql
            $tz = (new \DateTime('now', new \DateTimeZone(TIMEZONE)))->format('P');
            $tmp->exec("SET time_zone='$tz';");
            
        }
        /*
         * if(is_array($args))
         * foreach($args as $key => $value)
         * $tmp->setAttribute($key, $value);
         */
        return $tmp;
    }
}

Table::loadParameters();