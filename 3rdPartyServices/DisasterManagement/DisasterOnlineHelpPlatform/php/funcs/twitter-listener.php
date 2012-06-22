<?php


/* CONSTS */
$ANONYMOUS_USER_ID = 1;



function findRequestId($answer)
{
	preg_match('/^\D*(?=\d)/', $answer, $m);
	$firstNumberPosition = strlen($m[0]);
	$posOfSeperator = strpos($answer, ":", $firstNumberPosition);

	if($firstNumberPosition === false || $posOfSeperator === false)
	{
		return -1; // no number matched
	}
	else
	{
		$requestID = substr($answer, $firstNumberPosition, $posOfSeperator - $firstNumberPosition);
	}

	if ( is_numeric(trim($requestID)) ){
		return trim($requestID);
	} else {
		return -1; // no valid number found
	}

}

function seperateAnswer($reqId, $message){
	print "MESSAGE: ".$message."\n";
	$messageStartingAtId = substr($message, strpos($message, $reqId));
	print "MESSAGESTARTINGATID: ".$messageStartingAtId."\n";
	return substr($messageStartingAtId, strpos($messageStartingAtId, ":") + 1);
}

function writeAnswerToDB($reqId, $answer){
	global $ANONYMOUS_USER_ID;
	$answer = htmlspecialchars($answer);
	print "ANSWER: ".$answer."\n";

	//check wether this id is really taken

	$ticket_res = mysql_query("SELECT t.id FROM tickets AS t WHERE t.id = ".$reqId);

	$row = mysql_fetch_object($ticket_res);

	if ( $row->id != $reqId){
		print "THERE IS NO TICKET WITH THIS ID";
		return;
	}


	$sql = "INSERT INTO `societies`.`nm_users_tickets` (`ticket_id` , `user_id` , `answer` , `time`) VALUES ( '". mysql_real_escape_string($reqId) ."','". 	mysql_real_escape_string($ANONYMOUS_USER_ID) ."', '" . mysql_real_escape_string($answer) . "', CURRENT_TIMESTAMP)";

	mysql_query($sql);
}




include("./xmpphp/XMPPHP/XMPP.php");
$conn->connect();
while(!$conn->disconnected) {
	$payloads = $conn->processUntil(array('message', 'presence', 'end_stream', 'session_start'));
	foreach($payloads as $event) {
		$pl = $event[1];
		switch($event[0]) {
			case 'message':
				print "uh - new message!!################################";
				if(trim($pl['body']) == '') {
					break; // empty messages when establishing connection with new chat-partner
				}
				if($pl['body'] == 'The message has been sent.') {
					break; // feedback from thirdparty-forwarder
				}
				if($pl['body'] == 'quit'){
					$conn->message($pl['from'], $body="Okay, I'm shutting down now.", $type=$pl['type']);
					$conn->disconnect();
					break;
				}
				// print "---------------------------------------------------------------------------------\n";
				//print "Message from: {$pl['from']}\n";
				//if($pl['subject']) print "Subject: {$pl['subject']}\n";
				print $pl['body'] . "\n";
				print "---------------------------------------------------------------------------------\n";

				if( !(strpos($pl['body'], '[ERROR]') === false) || (!strpos($pl['body'], '[ERROR]') === false)) {

					/*$myFile = "./answers.txt";
					 $fh = fopen($myFile, 'a') or die("can't open file");
					$stringData = date("r")." ERROR/WARNING from ".$pl['from'].": ".$pl['body']."\n";
					fwrite($fh, $stringData);
					fclose($fh);*/

					break; //some error from twitter, no sending back - but write into file
				}

				$reqId = findRequestId($pl['body']);

				if ($reqId == -1){ //invalid ID
					//$conn->message($pl['from'], $body="Sorry, we couldn't identify a valid Request-ID. Pls answer like this => 'R 123: my answer.'", $type=$pl['type']);
					break;
				}

				//$conn->message($pl['from'], $body="Thanks for answering request $reqId.", $type=$pl['type']);

				writeAnswerToDB($reqId, seperateAnswer($reqId, $pl['body']));

				/*$myFile = "./answers.txt";
				 $fh = fopen($myFile, 'a') or die("can't open file");
				$stringData = date("r")." Message from ".$pl['from'].": ".$pl['body']."\n";
				fwrite($fh, $stringData);
				fclose($fh); */

				break;
			case 'presence':
				print "Presence: {$pl['from']} [{$pl['show']}] {$pl['status']}\n";
				break;
			case 'session_start':
				$conn->presence($status="Cheese!");
				break;
		}
	}
}
?>