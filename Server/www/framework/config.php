<?php

//define('TIMEZONE', 'Europe/Rome');
define('TIMEZONE', 'UTC');
date_default_timezone_set(TIMEZONE);

$GLOBALS["tokenLength"] = 32;
$GLOBALS["scadenzaTokenGiorni"] = 1;

$GLOBALS["serverURL"] = "localhost";
$GLOBALS['databaseType'] = "mysql";

//$GLOBALS['databaseName'] = "prapp";
//$GLOBALS['username'] = "root";

$GLOBALS['databaseName'] = "my_prapp";
$GLOBALS['username'] = "prapp";

$GLOBALS['password'] = "";
$GLOBALS['charset'] = "utf8";

$GLOBALS['watchdogThreshold'] = 5;
