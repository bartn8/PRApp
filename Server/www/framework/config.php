<?php

//TODO: forse meglio UTC: dopo impostare connessione database con utc e restituire con formato iso8061
//define('TIMEZONE', 'Europe/Rome');
define('TIMEZONE', 'UTC');
date_default_timezone_set(TIMEZONE);

$GLOBALS["tokenLength"] = 32;
$GLOBALS["scadenzaTokenGiorni"] = 1;

$GLOBALS["serverURL"] = "localhost";
$GLOBALS['databaseType'] = "mysql";

$GLOBALS['databaseName'] = "prapp";
$GLOBALS['username'] = "root";

$GLOBALS['password'] = "";
$GLOBALS['charset'] = "utf8";
