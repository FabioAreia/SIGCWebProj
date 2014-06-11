<?php
header("Content-type: text/csv");
header("Content-Disposition: attachment; filename=traindata.csv");
header("Pragma: no-cache");
header("Expires: 0");

$BASE = "./";

include $BASE . "ginc/gmauzao.inc.php";



$dblink = new db_link();

$qry = "SELECT ch.name, c.comment FROM `comments` as c, `channels` as ch, `tag_rel` as tr, `tags` as t, `users` as u, `videos` as v WHERE c.id_user = u.id AND c.id_channel = ch.id AND c.id_video = v.id AND tr.videos_id = c.id_video AND tr.tags_id = t.id ORDER BY t.term";

$allresults = $dblink->selectQRY($qry);

$csv_header = array("canal", "comment");
echo implode(",", $csv_header)."\n";

for ($u=0; $u<count($allresults); $u++){
	$allresults[$u]['comment'] = cleanString($allresults[$u]['comment']);

	if(strlen($allresults[$u]['comment'])>20){
		echo implode(",", $allresults[$u])."\n";
	}
}


function cleanString($frase){
	//$res = str_replace(array('"',',','.',';'), '', $frase);
	/*$res = str_replace(',', '', $res);
	$res = str_replace('.', '', $res);
	$res = str_replace(';', '', $res);*/
	$res = preg_replace( "/\r|\n/", "", $frase );
	$res = str_replace('  ', ' ', $res);
	$res = str_replace(',', ' ', $res);
	$res = str_replace('\t', ' ', $res);
	$res = str_replace('\n', ' ', $res);
	$res = str_replace('\r', ' ', $res);
	$res = str_replace('\r\n', ' ', $res);
	$res = preg_replace('/[^a-zA-Z0-9\s:()3,.!?]/', '', $res);
	$res = trim($res);
	return $res;
}


function outputCSV($data) {
    $output = fopen("php://output", "w");
    foreach ($data as $row) {
        fputcsv($output, $row);
    }
    fclose($output);
}


?>