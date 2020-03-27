<?php

require_once 'config.php';

header('Content-type: text/plain; charset=utf-8');

spl_autoload_register('MyAutoloader'); 

function MyAutoloader ($class)
{
	$classOK = str_replace('\\', "/", $class);
    
    $url = getcwd() . "/". $classOK . ".php";
    
    //echo $url . "\n";

	// Cartella corrente.
	if (file_exists($url))
		include_once $url;

}

session_start();

$handler = new com\handler\HTTPHandler(new com\view\printer\JSONPrinter());
$handler->handle();