<?php
//session_start();

//include $BASE."ginc/dbfunc.inc.php";
//include $BASE."ginc/gconn.inc.php";
include $BASE."ginc/db_link.class.php";
//include $BASE."ginc/token_group.class.php";
//include $BASE."ginc/gsession.class.php";
setlocale(LC_TIME, "pt_PT");



function csv_to_array($filename='', $delimiter=',')
{
    if(!file_exists($filename) || !is_readable($filename))
        return FALSE;

    $data = array();
    if (($handle = fopen($filename, 'r')) !== FALSE)
    {
        while (($row = fgetcsv($handle, 5000, $delimiter)) !== FALSE)
        {
            $data[] = $row;
        }
        fclose($handle);
    }
    return $data;
}

function random_float($min, $max){
	$randint = mt_rand();
	$rand0to1 = $randint/mt_getrandmax();
	$rand0to1 = $rand0to1*($max-$min);
	$rand0to1 = $rand0to1+$min;
	return $rand0to1;
}



function printArray($oArray){
	echo "<pre>";
	print_r($oArray);
	echo "</pre>";
}


function array2json($arr) {
	//if(function_exists('json_encode')) return json_encode($arr); //Lastest versions of PHP already has this functionality.
	$parts = array();
	$is_list = false;

	//Find out if the given array is a numerical array
	$keys = array_keys($arr);
	$max_length = count($arr)-1;
	if(($keys[0] == 0) and ($keys[$max_length] == $max_length)) {//See if the first key is 0 and last key is length - 1
		$is_list = true;
		for($i=0; $i<count($keys); $i++) { //See if each key correspondes to its position
			if($i != $keys[$i]) { //A key fails at position check.
				$is_list = false; //It is an associative array.
				break;
			}
		}
	}

	foreach($arr as $key=>$value) {
		if(is_array($value)) { //Custom handling for arrays
			if($is_list) $parts[] = array2json($value); /* :RECURSION: */
			else $parts[] = '"' . $key . '":' . array2json($value); /* :RECURSION: */
		} else {
			$str = '';
			if(!$is_list) $str = '"' . $key . '":';

			//Custom handling for multiple data types
			if(is_numeric($value)) $str .= $value; //Numbers
			elseif($value === false) $str .= 'false'; //The booleans
			elseif($value === true) $str .= 'true';
			else $str .= '"' . addslashes($value) . '"'; //All other things
			// :TODO: Is there any more datatype we should be in the lookout for? (Object?)

			$parts[] = $str;
		}
	}
	$json = implode(',',$parts);

	if($is_list) return '[' . $json . ']';//Return numerical JSON
	return '{' . $json . '}';//Return associative JSON
}

function formataData($datain){
	
	$data = strftime("%Y-%m-%d %H:%M:%S", $datain);
	return $data;
}

function giveDataNowFormat(){
	$data = strftime("%Y-%m-%d %H:%M:%S", strtotime('now'));
	return $data;
}


function pickOne($cd, $one, $two){
	if($cd){
		return $one;
	}else {
		return $two;
	}
	return $two;
}

function g_send_mail($para, $mailfrom, $nomefrom, $assunto, $mailContent){
	
			 /*
			 * ENVIO DE MAIL
			 */
			
			$boundary='--' . md5( uniqid("arteladecasabondary") );
			$priorities = array( '1 (Highest)', '2 (High)', '3 (Normal)', '4 (Low)', '5 (Lowest)' );
			$priority=$priorities[2];
			$charset="utf-8";
			$ctencoding="8bit";
			$sep= chr(13) . chr(10);
			$disposition="inline";
				
				
			$to  = $para;
			$from = $mailfrom;
			
			
			$Name = $nomefrom; //senders name
			
			$header = '';
			$header.="From: $Name <" . $from . ">\nX-Priority: $priority\nCC: $cc\n";
			$header.="Mime-Version: 1.0\nContent-Type: multipart/mixed;\n boundary=\"$boundary\"\n";
			$header.="Content-Transfer-Encoding: $ctencoding\nX-Mailer: Php/libMailv1.3\n";
			
			
			$message .="This is a multi-part message in MIME format.\n--$boundary\n";
			$message .= "Content-Type: text/html; charset=$charset\r\n";
			$message .= "Content-Transfer-Encoding: $ctencoding\n\n" . $mailContent ."\n";
			
			
			//$email = "geral@ecviriato.pt"; //senders e-mail adress
			$recipient = $para; //recipient

			return mail($recipient, $assunto, $message, $header);
}



function stripScriptContent($str){
	$tempstr = strstr($srt, 'script type');
	$tempstr2 = strstr($tempstr, ">");
	$firstpos = strpos($str, "<script") + strpos($tempstr, ">");
	$lastpos = strpos($str, "</script>");
	$strnova = substr($str, 0, $firstpos) . " " . substr($str, $lastpos);
	//echo "NNNN".$strnova."NNNN";
	//echo "tempstr : ".$tempstr."<br>tempstr2 : ".$tempstr2. "<br>firstpos : ".$firstpos."<br>lastpos : ".$lastpos;
	return $strnova;
}
?>