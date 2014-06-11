<?php

$BASE = "./";

include $BASE . "ginc/gmauzao.inc.php";



$dblink = new db_link();

/*
echo "GET: ";
print_r($_GET);
echo "<br/>";

echo "POST: ";
print_r($_POST);
echo "<br/>";
*/


//$dblink->insert("userinfo", array("user"=>"utilizador1", "pass"=>"password1"));

//$users = $dblink->search("userinfo",null,null,null);



$resultado = array("success"=>0, "qrys"=>array());

if($_GET['opt'] == 'comments'){
	
	
	if(isset($_POST['todo'])){

		if($_POST['todo']=='newvideoinfo'){

			$chanexists = $dblink->search('channels',array('name' => $_POST['channel']));
				array_push($resultado["qrys"],$dblink->lastqry);

			if(count($chanexists)>0){
				$chanid = $chanexists[0]['id'];
			}else {
				$dblink->insert('channels',array('name' => $_POST['channel']));
				array_push($resultado["qrys"],$dblink->lastqry);
				$chanid = $dblink->insertedID();
			}

			$exists = $dblink->search('videos',array('vid' => $_POST['vid']));
			array_push($resultado["qrys"],$dblink->lastqry);

			if(count($exists)==0){

				$values = $_POST['vidinfo'];

				$dblink->insert('videos',$values);
				array_push($resultado["qrys"],$dblink->lastqry);

				$vidid = $dblink->insertedID();

				$tags = $_POST['vidtags'];

				for($i=0; $i<count($tags); $i++){
					$existstag = $dblink->search('tags',$tags[$i]);
					array_push($resultado["qrys"],$dblink->lastqry);

					if(count($existstag)>0){
						$existsrel = $dblink->search('tag_rel',array('videos_id'=>$vidid,'tags_id'=>$existstag[0]['id']));
						array_push($resultado["qrys"],$dblink->lastqry);

						if(count($existsrel)==0){
							$dblink->insert('tag_rel',array('videos_id'=>$vidid,'tags_id'=>$existstag[0]['id']));
							array_push($resultado["qrys"],$dblink->lastqry);
						}

					}else {

						$dblink->insert('tags',$tags[$i]);
						array_push($resultado["qrys"],$dblink->lastqry);

						$tagid = $dblink->insertedID();

						$dblink->insert('tag_rel',array('videos_id'=>$vidid,'tags_id'=>$tagid));
						array_push($resultado["qrys"],$dblink->lastqry);
					}
				}

			}else {
				$vidid = $exists[0]['id'];
			}

		
			$resultado['success'] = 1;

			$resultado['vid'] = $vidid;
			$resultado['chanid'] = $chanid;


		}else if($_POST['todo']=='insertcomment'){

			$existsuser = $dblink->search('users',array('name'=>$_POST['user']));

			if(count($existsuser)>0){
				$userid = $existsuser[0]['id'];
			}else {
				$dblink->insert('users',array('name'=>$_POST['user']));
				array_push($resultado["qrys"],$dblink->lastqry);
				$userid = $dblink->insertedID();
			}


			$values = array('id_channel'=>$_POST['cid'],'id_video'=>$_POST['vid'],'id_user'=>$userid, 'comment'=> $_POST['comment']);
			$dblink->insert('comments',$values);
			array_push($resultado["qrys"],$dblink->lastqry);

		}

	}


}

echo json_encode($resultado);

?>