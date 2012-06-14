#!/usr/bin/env php
<?php
/*
 * StatusNet - a distributed open-source microblogging tool
 * Copyright (C) 2008, 2009, StatusNet, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * @copyright Michael Treyvaud 2012 <michael.treyvaud@gmail.com>
 */





/*
 * Or just call http://EXAMPLE.COM/api/statuses/show.xml?id=NOTICEID
 *         e.g. http://pnzi.com/api/statuses/show.xml?id=94
           or   http://pnzi.com/api/statuses/public_timeline.xml
 */




define('INSTALLDIR', realpath(dirname(__FILE__) . '/../../..'));

require_once INSTALLDIR.'/scripts/commandline.inc';

try {
    

    // Just change 74.json to whatever id you would like to retrieve
    // Make sure you replace urltostatusnet to your own url!
    $url = "urltostatusnet/api/statuses/show/74.json";

    $ch = curl_init();
    
    curl_setopt($ch, CURLOPT_URL, $url);
    
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    
    curl_setopt($ch, CURLOPT_HTTPHEADER, array('Expect:'));
    
    $output = curl_exec($ch);
    
    curl_close($ch); 
    
    echo $output;

} catch (Exception $e) {
    print $e->getMessage() . "\n";
    exit(1);
}
