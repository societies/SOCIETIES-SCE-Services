/*
 * StatusNet - a distributed open-source microblogging tool
 * Copyright (C) 2010, StatusNet, Inc.
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
 * StatusNet Annotations
 *
 * @category  UI interaction
 * @package   StatusNet
 * @author    Julien Chaumond <julien@pnzi.com>
 * @license   http://www.fsf.org/licensing/licenses/agpl-3.0.html GNU Affero General Public License version 3.0
 * @link      http://status.net/
 */


// XXX: This is Work in Progress


var SNA = { // StatusNetAnnotations

    Annotate: function(ann_type, ann_attr, ann_value) {

        // Interact with Annotations
        
        var annotations = jQuery.parseJSON($("#notice_data-annotations").attr("value"));
        
        // Cycle through annotations:
        //   "tweets can have more than one annotation of the same type, 
        //    annotations of the same type are still separate annotations"
        //      @see  http://dev.twitter.com/pages/annotations_overview
        
        for (i = 0; i < annotations.length; i++) {
            if (annotations[i].ann_type !== undefined) {
                annotations[i].ann_type.ann_attr = String(ann_value);
                annotations[i].ann_type.unknown = "test";
            }
        }
        
        
        // Either browser supports JSON natively or json2.js is included
        var anntxt = JSON.stringify(annotations);
        
        
        $("#notice_data-annotations").attr("value", anntxt);
    }
};

