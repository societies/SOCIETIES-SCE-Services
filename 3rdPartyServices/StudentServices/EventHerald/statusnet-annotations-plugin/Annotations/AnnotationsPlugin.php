<?php
/**
 * Plugin to do Twitter-style Annotations (custom semantic metadata attached to notices).
 * @see  http://dev.twitter.com/pages/annotations_overview
 *
 * PHP version 5
 *
 * LICENCE: This program is free software: you can redistribute it and/or modify
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
 * @category  Plugin
 * @package   StatusNet
 * @author    Julien Chaumond <julien@pnzi.com>
 * @license   http://www.fsf.org/licensing/licenses/agpl-3.0.html GNU Affero General Public License version 3.0
 * @link      http://status.net/
 * @link      http://pnzi.com/
 * @copyright Michael Treyvaud 2012 <michael.treyvaud@gmail.com>
 */

if (!defined('STATUSNET') && !defined('LACONICA')) {
    exit(1);
}


require_once INSTALLDIR . '/plugins/Annotations/classes/Annotations.php';
require_once INSTALLDIR . '/plugins/Annotations/classes/Review.php';
require_once INSTALLDIR . '/plugins/Annotations/classes/Custom_TSSG_Event.php';

/**
 * Plugin to do Twitter-style Annotations (custom semantic metadata attached to notices).
 * @see  http://dev.twitter.com/pages/annotations_overview
 * 
 * Right now, submitted Annotations are only accepted in JSON format 
 *   (see scripts/createnotice.php in the plugin's folder);
 * either from the API or from the Web interface 
 *   (set $demo parameter to true to add sample annotations to the Web interface's notice form)
 * 
 * Annotations are stored directly in JSON in a custom table (annotations).
 * However, they can be displayed in either JSON or XML depending on the API call
 *   (see scripts/shownotice.php in the plugin's folder, or just call any 
 *      API call containing an annotated notice,
 *      for example  http://EXAMPLE.COM/api/statuses/public_timeline.xml)
 * 
 * Additionally, for selected Annotations types/attributes couples, the plugin 
 * stores their values directly in a specific database table, 
 * where the table name is the annotation type,
 *       the column name is the annotation attribute,
         and the value is the annotation value.
 * We implement this for review-typed annotations (saved to a review table)
 *    @see data class in classes/Review.php
 * 
 * @category Plugin
 * @package  StatusNet
 * @author   Julien Chaumond <julien@pnzi.com>
 * @license  http://www.fsf.org/licensing/licenses/agpl-3.0.html GNU Affero General Public License version 3.0
 * @link     http://status.net/
 * @copyright Michael Treyvaud 2012 <michael.treyvaud@gmail.com>
 */

class AnnotationsPlugin extends Plugin
{
    // Whether to add sample annotations to the Web interface's default notice form
    public $demo = true;
    // Selected (optional) Annotations types and/or attributes to store in separate database table(s)
    /*public $stored_annotations = array(array(
					'review' => array('content' => null, 'rating' => null),
					'custom_tssg_event' => array('event_title' => null,'event_start_time' => null,'event_end_time' => null,'event_date' => null,'event_location' => null,'event_url' => null,'event_description' => null,'event_lat' => null,'event_long' => null)));
	*/
	public $stored_annotations = array(array('custom_tssg_event' => array('event_title' => null,'event_start_time' => null,'event_end_time' => null,'event_date' => null,'event_location' => null,'event_url' => null,'event_description' => null,'event_lat' => null,'event_long' => null)));

    // To set or unset these parameters, use e.g.:
    //   addPlugin('Annotations', array('demo' => false, 'stored_annotations' => null));
    
    function onCheckSchema()
    {
        $schema = Schema::get();

        $schema->ensureTable('annotations',
                             array(new ColumnDef('notice_id', 'int', 11, false, 'PRI'),
                                   // XXX: new ColumnDef('rating', 'int', 1),
                                   new ColumnDef('content', 'varchar', 
                                                 /*size*/ MAX_ANNOTATIONS_SIZE, 
                                                 /*nullable*/ false,
                                                 /*index*/ 'MUL')
                                   ));
        
        /* Example of a table specific to a certain annotations type/attributes couple:
        
        $schema->ensureTable('review',
                             array(new ColumnDef('notice_id', 'int', 11, false, 'PRI'),
                                   new ColumnDef('rating', 'int', 1),
                                   new ColumnDef('content', 'varchar', 64, false, 'MUL')
                                   ));


	//*/

	

	// Added to allow event meta data to be stored
	$schema->ensureTable('custom_tssg_event',
				array(new ColumnDef('notice_id','int',11,false,'PRI'),
					new ColumnDef('event_title','varchar',36,false),
					new ColumnDef('event_start_time','timestamp'),
					new ColumnDef('event_end_time','timestamp'),
					new ColumnDef('event_date','datetime'),
					new ColumnDef('event_location','varchar',255),
					new ColumnDef('event_url','varchar',255),
                                        new ColumnDef('event_description','varchar',255),
                                        new ColumnDef('event_lat','decimal(10,7)'),
                                        new ColumnDef('event_long','decimal(10,7)'),
				));
        return true;
    }
    
    
    /*
     * Following five functions: to **create** an annotated notice
     */
    function onEndPrepareApiStatusesUpdate($action, $args)
    {
        // at the end of preparing args for the status update action (API)
        
        $action->annotations = $action->trimmed('annotations');
        
        return true;
    }
    
    function onEndHandleApiStatusesUpdate($action, $args, $options)
    {
        // right before saving the notice for the status update action (API)
        
        if (json_decode($action->annotations) == NULL) {
            // Check that it is valid JSON
            
            $action->clientError(_('Annotations not valid JSON.'), 406);
            return false;
        }
        
        if (Annotations::contentTooLong($action->annotations)) {
            $action->clientError(_('Annotations too long.'), 406);
            return false;
        }
        
        $options['annotations'] = $action->annotations;
        
        return true;
    }
    
    function onEndHandleStatusesUpdate($action, $options)
    {
        // right before saving the notice for the status update action (Web interface, not API)
        
        if (json_decode($action->trimmed('annotations')) == NULL) {
            // Check that it is valid JSON
            
            $action->clientError(_('Annotations not valid JSON.'), 406);
            return false;
        }
        
        if (Annotations::contentTooLong($action->trimmed('annotations'))) {
            $action->clientError(_('Annotations too long.'), 406);
            return false;
        }
        
        $an = array('annotations' => $action->trimmed('annotations'));
        
        $options = array_merge($options, $an);
        
        return true;
    }
    
    function onSaveNewNoticeAddOptions($options, $notice)
    {
        // at the end of adding attributes to a saved notice based on options
        
        extract($options);
        
        $notice->annotations = $annotations;
        
        return true;
   }
    

    function onEndNoticeSave($notice)
    {
        if (!empty($notice->annotations)) {
            
            Annotations::saveAnnotations($notice->id, $notice->annotations);
            
            // Store selected Annotations types/attributes in separate database table
            
            //  First decode to an associative array
            
            $annotations = json_decode($notice->annotations, true); 

                     
            foreach ($annotations as $annotation) {
                foreach ($this->stored_annotations as $stored_annotation) {
                    // Circle through all stored annotations types to check if we need to store to database
                    
                    if (key($annotation) == key($stored_annotation)) {
                        // Match for Annotations type
                        
                        $an = $annotation[key($annotation)];
                        $sa = $stored_annotation[key($stored_annotation)];
                        // Is there an elegant way to do that?
                        
                        // Only keep attributes that we want to store to database
                        
                        $an = array_intersect_key($an, $sa);
                        
                        // $an now contains all annotations attributes (for this type) that we want to store
                        
                        // Do we have a data class for this annotations type?
                        
                        $class_name = ucfirst(key($annotation));
                        
                        if (class_exists($class_name)) {
                            
                            // Data class must contain a save method, see example in classes/Review.php
                            
                            $class_name::save($notice->id, $an);
                            
                        }
                    }
                    
                }
            } 
        }        
        return true;
    }
    
    
    /*
     * Following three functions: to **show** an annotated notice
     *
     *   First two: in the API, third one: in the Web interface
     *
     * Display annotations tag even if empty (that's what's done for other attributes)
     */
    
    function onEndApiGetSimpleStatusArray($notice, $twitter_status)
    {
        // at the end of fetching all notice attributes to show a notice (API)
        
        $annotations = Annotations::getAnnotations($notice);
        
        if (!empty($annotations)) {
            
            // Decode to an associative array
            
            $twitter_status['annotations'] = json_decode($annotations->content, true);
            
            // Associative array can then be re-encoded to JSON or XML later on
        }
        else {
            $twitter_status['annotations'] = null;
        }
        
        return true;
    }
    
    function onShowStatusXmlAnnotations($action, $annotations)
    {
        // JSON-type associative array to XML transform.
        
        // when encountering an Annotations when showing a notice (API)
        
        // Max depth supported: 2.
        // @see  http://dev.twitter.com/pages/annotations_overview
        
        if (empty($annotations)) {
            $action->element('annotations');
        }
        else {
            $attrs = array('type' => 'array');
            $action->elementStart('annotations', $attrs);
            foreach($annotations as $annotation) {
                foreach($annotation as $element => $value) {
                    $action->elementStart('annotation');
                    $action->element('type', null, $element);
                    $action->elementStart('attributes');
                    foreach($value as $d2name => $d2value) {
                        $action->elementStart('attribute');
                        $action->element('name', null, $d2name);
                        $action->element('value', null, $d2value);
                        $action->elementEnd('attribute');
                    }
                    $action->elementEnd('attributes');
                    $action->elementEnd('annotation');
                }
            }
            $action->elementEnd('annotations');
        }
        
        return true;
    }
    
    function onEndShowNoticeItem($action)
    {
        // Annotations display in the Web interface
        // DOES NOT ACTUALLY GRAB ANY ANNOTATIONS!!!!
        $annotations = Annotations::getAnnotations($action->notice);
        
        $action->out->elementStart('div', array('style' => 'display:none;'));
        
        // Turn that into Microformats, RDFa, or whatever
        
        // For example try hreview here instead of in notice text itself:
        //    annotations type or attribute => class attribute
        //    annotations value => text
        
        if ($annotations && $annotations->content) {
            $action->out->raw($annotations->content);
        }
        
        $action->out->elementEnd('div');

        
        return true;
    }
    
    
    /**
     * Ensure that annotations for a notice are deleted
     * when that notice gets deleted.
     *
     * @param Notice $notice
     * @param array &$related list of related tables; entries
     *              with matching notice_id will be deleted.
     *
     * @return boolean hook result
     */

    function onNoticeDeleteRelated($notice)
    {
	$noticeId = $notice->id;

	/**
	 *
 	 * This code connects to the DB and manually removes previously saves annotations
	 *
	 * Not entirely sure if this will effect anything further down the line.
	 * Will come back and neaten this up if required for my use
	 *
 	 */

	
	// You will have to change these details based on your own database login/password
	$con = mysql_connect("domain","database-username","data-basepassword");
	if(!$con){
		die('Could not connect: ' . mysql_error());
	}

	//Uses the "statusnet" database - defined already within the config.php file
	mysql_select_db("statusnet",$con);


	//Removes annotations associated with the notice id.
	$result = mysql_query("DELETE FROM annotations
				WHERE NOTICE_ID=".$noticeId);

	// Because the only annotations I will be using is the Custom_TSSG_Event, I know that any message should also be within the Custom_TSSG_Event table
	$result = mysql_query("DELETE FROM custom_tssg_event WHERE NOTICE_ID=".$noticeId);


	mysql_close($con);
        return true;
    }

    
    
    function onEndShowNoticeFormData($action)
    {
        // Add hidden metadata context to the Web interface's notice form
        
        if ($this->demo) {
            
            $annotations = array(array('custom_tssg_event' => array('event_title' => null,'event_start_time' => null,'event_end_time' => null,'event_date' => null,'event_location' => null,'event_url' => null,'event_description' => null,'event_lat' => null,'event_long' => null)));
            
            $an = json_encode($annotations);
            
            // This is Twitter's desired format:
            //  [{"product":{"title":"Canon_EOS_550D"}},{"review":{"content":"Canon_EOS_550D","rating":"5"}}]
            
            //  [{"type":{"another_attribute":"value", "attribute":"value"}},
            //   {"another_type":{"another_attribute":"value", "attribute":"value"}}]
            
            $action->out->hidden('notice_data-annotations', $an, 'annotations');
            
            // Is there a better place to store JSON data than in the value field of an input tag?
            
            // XXX: Abstract a few functions to interact with JSON metadata from a page's javascript calls
        }
        
        return true;
    }
    
    
    function onEndShowScripts($action)
    {
        //$action->script(common_path('/var/www/statusnet/plugins/Annotations/annotations.js'));
	$action->script(common_path('/js/annotations.js'));
    	
	//Added to allow further processing
	return true;
	}
    
    
    function onPluginVersion(&$versions)
    {
        $versions[] = array('name' => 'Annotations',
                            'version' => '0.1',
                            'author' => 'Julien Chaumond',
                            'homepage' => 'http://status.net/wiki/Plugin:Annotations',
                            'description' =>
                            _m('Plugin to do Twitter-style Annotations (custom semantic metadata attached to notices).'));
        return true;
    }
}

