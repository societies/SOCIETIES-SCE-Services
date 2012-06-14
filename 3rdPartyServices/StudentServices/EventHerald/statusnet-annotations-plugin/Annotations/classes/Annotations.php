<?php
/**
 * Data class for Annotations
 *
 * PHP version 5
 *
 * @category Data
 * @package  StatusNet
 * @author   Julien Chaumond <julien@pnzi.com>
 * @license  http://www.fsf.org/licensing/licenses/agpl.html AGPLv3
 * @link     http://status.net/
 * @copyright Michael Treyvaud 2012 <michael.treyvaud@gmail.com>
 * 
 * StatusNet - the distributed open-source microblogging tool
 * Copyright (C) 2009, StatusNet, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

if (!defined('STATUSNET') && !defined('LACONICA')) {
    exit(1);
}

require_once INSTALLDIR . '/classes/Memcached_DataObject.php';

define('MAX_ANNOTATIONS_SIZE', 512);


class Annotations extends Memcached_DataObject
{
    public $__table = 'annotations';        // table name
    public $notice_id;                      // int(11)   primary_key not_null
    public $content;                        // varchar(512)

    /**
     * Get an instance by key
     *
     * This is a utility method to get a single instance with a given key value.
     *
     * @param string $k Key to use to lookup
     * @param mixed  $v Value to lookup
     *
     * @return  object found, or null for no hits
     *
     */

    function staticGet($k, $v=null)
    {
        return Memcached_DataObject::staticGet('Annotations', $k, $v);
    }

    /**
     * return table definition for DB_DataObject
     *
     * DB_DataObject needs to know something about the table to manipulate
     * instances. This method provides all the DB_DataObject needs to know.
     *
     e#reApi @return array array of column definitions
     */

    function table()
    {
        return array('notice_id' => DB_DATAOBJECT_INT + DB_DATAOBJECT_NOTNULL,
                     'content' => DB_DATAOBJECT_STR);
    }
    
    
    /**
     * return key definitions for DB_DataObject
     *
     * DB_DataObject needs to know about keys that the table has, since it
     * won't appear in StatusNet's own keys list. In most cases, this will
     * simply reference your keyTypes() function.
     *
     * @return array list of key field names
     */

    function keys()
    {
        return array_keys($this->keyTypes());
    }

    /**
     * return key definitions for Memcached_DataObject
     *
     * Our caching system uses the same key definitions, but uses a different
     * method to get them. This key information is used to store and clear
     * cached data, so be sure to list any key that will be used for static
     * lookups.
     *
     * @return array associative array of key definitions, field name to type:
     *         'K' for primary key: for compound keys, add an entry for each component;
     *         'U' for unique keys: compound keys are not well supported here.
     */

    function keyTypes()
    {
        return array('notice_id' => 'K');
    }

    /**
     * Magic formula for non-autoincrementing integer primary keys
     *
     * If a table has a single integer column as its primary key, DB_DataObject
     * assumes that the column is auto-incrementing and makes a sequence table
     * to do this incrementation. Since we don't need this for our class, we
     * overload this method and return the magic formula that DB_DataObject needs.
     *
     * @return array magic three-false array that stops auto-incrementing.
     */

    function sequenceKey()
    {
        return array(false, false, false);
    }
    
    
    static function contentTooLong($content)
    {
        $contentlimit = MAX_ANNOTATIONS_SIZE;
        return ($contentlimit > 0 && !empty($content) && (mb_strlen($content) > $contentlimit));
    }


    /**
     * Save new Annotations
     *
     * @return boolean
     */
    
    static function saveAnnotations($notice_id, $content)
    {
        // To have $notice->id populated,
        // we need to call saveAnnotations() from onEndNoticeSave, not onStartNoticeSave
        
        $an = new Annotations();

        $an->notice_id  = $notice_id;
        $an->content    = $content;
        
        $result = $an->insert();

        if (!$result) {
            throw Exception(sprintf(_m("Could not save annotations for notice %s"),
                                    $notice_id));
        }
        
        return true;
    }
    
    
    static function getAnnotations($notice)
    {
        $an = Annotations::staticGet('notice_id', $notice->id);
        
        if (empty($an)) {
            return null;
        }
        else {
            return $an;
        }   
    }


}
