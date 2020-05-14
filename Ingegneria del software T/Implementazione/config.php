<?php

define('TIMEZONE', 'UTC');
date_default_timezone_set(TIMEZONE);

$GLOBALS["databaseURL"] = "localhost";
$GLOBALS['databaseType'] = "mysql";
$GLOBALS['databaseName'] = "my_prapp";
$GLOBALS['databaseUsername'] = "root";
$GLOBALS['databasePassword'] = "";
$GLOBALS['databaseCharset'] = "utf8";

$GLOBALS['defaultSystemAdministatorUsername'] = "admin";
$GLOBALS['defaultSystemAdministatorPassword'] = "admin";
