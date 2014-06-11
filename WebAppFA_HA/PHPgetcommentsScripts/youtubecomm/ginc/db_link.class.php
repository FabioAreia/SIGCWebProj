<?php
class db_link{
	
	public $lastresults;
	public $GCONN;
	public $lastqry;
	//var $result;
	
	public function db_link(){
		//printf("Connecting\n");
		$this->GCONN['db_server'] = "localhost";
		$this->GCONN['db_login'] = "root";
		//$GCONN['db_login'] = "root";
		$this->GCONN['db_password'] = "";
		$this->GCONN['db_database'] = "youtubecomments";
		//$this->GCONN['conn'] = mysqli_connect ($this->GCONN["db_server"], $this->GCONN["db_login"], $this->GCONN["db_password"]) or die ('I cannot connect to the database because: ' . mysqli_error());
		$this->GCONN['conn'] = new mysqli($this->GCONN["db_server"], $this->GCONN["db_login"], $this->GCONN["db_password"], $this->GCONN["db_database"]);
		/* check connection */
		
		if (mysqli_connect_errno()) {
			printf("Connect failed: %s\n", mysqli_connect_error());
			exit();
		}
		
		//mysqli_select_db($this->GCONN['conn'], $this->GCONN["db_database"]);
	}
	
	public function insert($table, $values){
		$qry = "INSERT into $table (";
		$j = 0;
		foreach ($values as $key => $value)
		{
			$j>0?$qry.=", ":$j++;
			$qry .= "`$key`";
		}
		$qry .= ") VALUES (";
		$j = 0;
		foreach ($values as $key => $value)
		{
			$j>0?$qry.=", ":$j++;
			$qry .= "'".mysqli_real_escape_string($this->GCONN['conn'],$value)."'";
		}
		$qry .= ")";
		//print($qry);
		$this->lastqry = $qry;
		return mysqli_query($this->GCONN['conn'],$qry);
	}

	public function get_struct($table){
		$result = $this->select_qry($table, null, null, null);
		$fields = mysqli_list_fields($this->GCONN['conn'],$GLOBALS[db_database], $table);
		$columns = mysqli_num_fields($this->GCONN['conn'],$fields);
		$struct = array();
		for ($g=0; $g<$columns; $g++)
			$struct[$g] = mysqli_field_name($this->GCONN['conn'],$result, $g);
		return $struct;
	}

	public function update($table, $values, $cond){
		$qry = "UPDATE $table SET ";
		$j = 0;
		foreach ($values as $key => $value)
		{
			$j>0?$qry.=", ":$j++;
			$qry .= "`$key`='".mysqli_real_escape_string($this->GCONN['conn'],$value)."'";
		}
		if ($cond!=null){
			$qry .= " WHERE ";
			$j = 0;
			foreach ($cond as $key => $value)
			{
				$j>0?$qry.=" AND ":$j++;
				$qry .= "`$key`='".mysqli_real_escape_string($this->GCONN['conn'],$value)."'";
			}
		}
		//print($qry);
		$this->lastqry = $qry;
		return mysqli_query($this->GCONN['conn'],$qry);
	}

	public function updateXOR($table, $values, $cond){
		$qry = "UPDATE $table SET ";
		$j = 0;
		foreach ($values as $key => $value)
		{
			$j>0?$qry.=", ":$j++;
			$qry .= "`$key`='$value'";
		}
		if ($cond!=null){
			$qry .= " WHERE ";
			$j = 0;
			for ($m=0; $m<count($cond); $m++){
				foreach ($cond[$m] as $key => $value)
				{
					$j>0?$qry.=" OR ":$j++;
					$qry .= "`$key`='$value'";
				}
			}
		}
//		print($qry);
		$this->lastqry = $qry;
		return mysqli_query($this->GCONN['conn'],$qry);
	}

	public function select_qry($table, $cond=null, $fields=null, $order=null, $ord=null){
		$qry ="";
		$qry .= "SELECT ";
		if ($fields==null){
			$qry .= "* FROM `$table`";
			}
			else{
				for ($i=0; $i<count($fields); $i++){
					$i>0?$qry .= ", $fields[$i]":$qry .= "$fields[$i]";
				}
				$qry .= " FROM `$table`";
			}
		if ($cond!=null){
			$qry .= " WHERE ";
			$j = 0;
			foreach ($cond as $key => $value)
			{
				$j>0?$qry.=" AND ":$j++;
				$qry .= "`$key`='$value'";
			}
		}
		if ($order!=null){
			$qry .= " ORDER by `$order`";
		}
		if ($ord!=null){
			//echo "tou aki";
			$qry .= " DESC";
		}
//		print($qry."<br>");
		$this->lastqry = $qry;
		return mysqli_query($this->GCONN['conn'],$qry);
	}
	
	public function search($table, $cond=null, $fields=null, $order=null, $ord=null){
		$result = $this->select_qry($table, $cond, $fields, $order, $ord);
		$searchresult = array();
		$e = 0;
		if ($result!=""){
		while($row = mysqli_fetch_array($result, MYSQL_ASSOC)){
			$thisresult = array();
			foreach ($row as $key => $value)
			{
				$thisresult[$key] = $value;
//				echo "<br>##key-".$key."##value-".$value;
			}
			$searchresult[$e++] = $thisresult;
		}
		}
		$this->lastresults = $searchresult;
		return $searchresult;
	}

	public function selectQRY($qry){
		$result = mysqli_query($this->GCONN['conn'],$qry);
		$this->lastqry = $qry;
		$searchresult = array();
		$e = 0;
		if ($result!=""){
		while($row = mysqli_fetch_array($result, MYSQL_ASSOC)){
			$thisresult = array();
			foreach ($row as $key => $value)
			{
				$thisresult[$key] = $value;
//				echo "<br>##key-".$key."##value-".$value;
			}
			$searchresult[$e++] = $thisresult;
		}
		}
		$this->lastresults = $searchresult;
		return $searchresult;
	}
	
	public function selectQRY2($qry, $start, $number){
		$result = mysqli_query($this->GCONN['conn'],$qry);
		$this->lastqry = $qry;
		$searchresult = array();
		$e = 0;
		if ($result!=""){
		$count = 0;
		$count2 = 0;
		while($row = mysqli_fetch_array($result, MYSQL_ASSOC)){
			if($count>=$start){
				$thisresult = array();
				foreach ($row as $key => $value)
				{
					$thisresult[$key] = $value;
	//				echo "<br>##key-".$key."##value-".$value;
				}
				$searchresult[$e++] = $thisresult;
			}
		}
		}
		$this->lastresults = $searchresult;
		return $searchresult;
	}

	
	public function selectXOR_qry($table, $cond=null, $fields=null, $order=null, $ord=null){
		$qry ="";
		$qry .= "SELECT ";
		if ($fields==null){
			$qry .= "* FROM `$table`";
			}
			else{
				for ($i=0; $i<count($fields); $i++){
					$i>0?$qry .= ", $fields[$i]":$qry .= "$fields[$i]";
				}
				$qry .= " FROM `$table`";
			}
		if ($cond!=null){
			//print_r($cond);
			$qry .= " WHERE ";
			$j = 0;
			for ($m=0; $m<count($cond); $m++){
				foreach ($cond[$m] as $key => $value)
				{
					$j>0?$qry.=" OR ":$j++;
					$qry .= "`$key`='$value'";
				}
			}
		}
		if ($order!=null){
			$qry .= " ORDER by `$order`";
		}
		if ($ord!=null){
			//echo "tou aki";
			$qry .= " DESC";
		}
		//print($qry);
		$this->lastqry = $qry;
		return mysqli_query($this->GCONN['conn'],$qry);
	}
	
	public function searchXOR($table, $cond=null, $fields=null, $order=null, $ord=null){
		$result = $this->selectXOR_qry($table, $cond, $fields, $order, $ord);
		$searchresult = array();
		$e = 0;
		if ($result!=""){
		while($row = mysqli_fetch_array($result, MYSQL_ASSOC)){
			$thisresult = array();
			foreach ($row as $key => $value)
			{
				$thisresult[$key] = $value;
//				echo "<br>##key-".$key."##value-".$value;
			}
			$searchresult[$e++] = $thisresult;
		}
		}
		$this->lastresults = $searchresult;
		return $searchresult;
	}

	public function insertedID(){
		return mysqli_insert_id($this->GCONN['conn']);
	}
	
	public function runQRY($qry){
		$result = mysqli_query($this->GCONN['conn'],$qry);
		$this->lastqry = $qry;
		return $result;
	}
	
	public function delete($table, $cond){
		$qry = "DELETE from $table WHERE ";
		$j = 0;
		foreach ($cond as $key => $value)
		{
			$j>0?$qry.=" AND ":$j++;
			$qry .= "`$key`='$value'";
		}
//		print($qry);
		$this->lastqry = $qry;
		return mysqli_query($this->GCONN['conn'],$qry);
		
	}
	
	public function get_last_results(){
		return $this->lastresults;
	}
	
}
?>