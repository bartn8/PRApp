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

namespace com\model;

use InvalidArgumentException;

/**
 * Gestisce la creazione di hash per le password.
 * Modello Singleton.
 *
 * @author Luca Bartolomei bartn8@hotmail.it
 *        
 */
class Hash
{

    private const DEFAULT_ALGORITHM = PASSWORD_DEFAULT;

    /**
     * Singleton della classe.
     *
     * @var Hash
     */
    private static $singleton;

    /**
     * Restituisce l'istanza singleton.
     * La crea se necessario.
     *
     * @return \com\model\Hash
     */
    public static function getSingleton()
    {
        if (Hash::$singleton == NULL) {
            Hash::$singleton = new Hash(isset($GLOBALS['cryptAlgorithm']) ? $GLOBALS['cryptAlgorithm'] : Hash::DEFAULT_ALGORITHM);
        }
        
        return Hash::$singleton;
    }

    /**
     * Rappresenta il tipo di algoritmo utilizzato (ES SHA 256).
     *
     * @var int
     */
    private $algorithm;

    /**
     * Salt utilizzato durante l'operazione di hash.
     *
     * @var string
     */
    
    /**
     * Genera l'istanza singleton.
     * Utilizzato solo da factory.
     *
     * @param string $algorithm
     */
    private function __construct($algorithm)
    {
        $this->algorithm = $algorithm;
    }

    /**
     * Genera un salt di dimensione variabile.
     *
     * @param int $length
     * @throws \InvalidArgumentException length non valido
     */
    public function generateSalt($length)
    {
        if (! is_int($length) || $length <= 0)
            throw new \InvalidArgumentException("length non valido");
        
        $binSalt = openssl_random_pseudo_bytes($length / 2);
        $this->salt = bin2hex($binSalt);
    }

    /**
     * Genera un hash sicuro per la password.
     *
     * @param string $password
     *            password da proteggere
     * @throws InvalidArgumentException password non valida
     * @return string hash calcolato
     */
    public function hashPassword($password)
    {
        if (! is_string($password) || mb_strlen($password, "utf-8") <= 0)
            throw new InvalidArgumentException("Password non valida");
        
        return password_hash($password, $this->algorithm);
    }

    /**
     * Valuta la password inserita dall'utente.
     *
     * @param string $password
     *            password da confrontare con quella del database
     * @param string $hash
     *            hash generato precedentemente da hashPassword
     * @throws InvalidArgumentException Password o hash non validi
     * @return boolean risultato vero se password giusta
     */
    public function evalutatePassword($password, $hash)
    {
        if (! is_string($password) || mb_strlen($password, "utf-8") <= 0)
            throw new InvalidArgumentException("Password non valida");
        
        if (! is_string($hash) || mb_strlen($hash, "utf-8") <= 0)
            throw new InvalidArgumentException("Hash non valida");
        
        return password_verify($password, $hash);
    }

    //https://stackoverflow.com/questions/1846202/php-how-to-generate-a-random-unique-alphanumeric-string/13733588#13733588

    private function crypto_rand_secure($min, $max)
    {
        $range = $max - $min;
        if ($range < 1) return $min; // not so random...
        $log = ceil(log($range, 2));
        $bytes = (int) ($log / 8) + 1; // length in bytes
        $bits = (int) $log + 1; // length in bits
        $filter = (int) (1 << $bits) - 1; // set all lower bits to 1
        do {
            $rnd = hexdec(bin2hex(openssl_random_pseudo_bytes($bytes)));
            $rnd = $rnd & $filter; // discard irrelevant bits
        } while ($rnd > $range);
        return $min + $rnd;
    }
    
    /**
     * Restituisce un token valido di lunghezza n.
     * 
     * @param int $length lunghezza token
     * 
     * @return string token generato.
     */
    private function _getToken($length)
    {
        $token = "";
        $codeAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        $codeAlphabet.= "abcdefghijklmnopqrstuvwxyz";
        $codeAlphabet.= "0123456789";
        $max = strlen($codeAlphabet); // edited
    
        for ($i=0; $i < $length; $i++) {
            $token .= $codeAlphabet[$this->crypto_rand_secure(0, $max-1)];
        }
    
        return $token;
    }

    /**
     * Restituisce un token valido.
     *  
     * @return string token generato.
     */
    public function getToken()
    {
        return $this->_getToken($GLOBALS["tokenLength"]);
    }

}

