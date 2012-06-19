<?php

/**
 *	@category Data
 *	@author	Michael Treyvaud <michael.treyvaud@gmail.com>
 *	@copyright Michael Treyvaud 2012
 *
 *	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU Affero General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the
 * 	GNU Affero General Public License for more details.
 *
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

if (!defined('STATUSNET') && !defined('LACONICA')) {
    exit(1);
}

require_once INSTALLDIR . '/classes/Memcached_DataObject.php';


class Custom_TSSG_Event extends Memcached_DataObject
{
    	public $__table = 'custom_tssg_event';	//
    	public $notice_id;			//
    	public $event_title;			//
	public $event_start_time;		//
	public $event_end_time;			//
	public $event_date;			//
	public $event_location;			//
	public $event_url;			//
	public $event_description;		//
	public $event_lat;			//
	public $event_long;			//


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
        return Memcached_DataObject::staticGet('Custom_TSSG_Event', $k, $v);
    }

    /**
     * return table definition for DB_DataObject
     *
     * DB_DataObject needs to know something about the table to manipulate
     * instances. This method provides all the DB_DataObject needs to know.
     *
     * @return array array of column definitions
     */

    function table()
    {
        return array('notice_id' => DB_DATAOBJECT_INT + DB_DATAOBJECT_NOTNULL,
                     'event_title' => DB_DATAOBJECT_STR + DB_DATAOBJECT_NOTNULL,
                     'event_start_time' => DB_DATAOBJECT_TIME,
			'event_end_time' => DB_DATAOBJECT_TIME,
			'event_date' => DB_DATAOBJECT_DATE,
			'event_location' => DB_DATAOBJECT_STR,
			'event_url' => DB_DATAOBJECT_STR,
			'event_description' => DB_DATAOBJECT_STR,
			'event_lat' => DB_OBJECT_INT,
			'event_long' => DB_OBJECT_INT
			);
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
    

    /**
     * Save new Review
     *
     * @return boolean
     */
    
    static function save($notice_id, $attributes)
    {
        // To have $notice->id populated,
        // we need to call save() from onEndNoticeSave, not onStartNoticeSave
        
        $an = new Custom_TSSG_Event();

        $an->notice_id  = $notice_id;
        $an->event_title     = $attributes['event_title'];
        $an->event_start_time    = $attributes['event_start_time'];
	$an->event_date = $attributes['event_date'];
	$an->event_location = $attributes['event_location'];
	$an->event_url = $attributes['event_url'];
	$an->event_description = $attributes['event_description'];
	$an->event_lat = $attributes['event_lat'];
	$an->event_long  = $attributes['event_long'];
	$an->event_end_time = $attributes['event_end_time'];
        
        $result = $an->insert();

        if (!$result) {
            throw Exception(sprintf(_m("Could not save Custom_TSSG_Event for notice %s"),
                                    $notice_id));
        }
        
        return true;
    }
    

}

