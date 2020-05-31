<?php
			header("Access-Control-Allow-Origin: *");
			header("Access-Control-Allow-Headers: access");
			header("Access-Control-Allow-Methods: GET");
			header("Access-Control-Allow-Credentials: true");
			header('Content-Type: application/json');

			$host = "localhost:3306";
			$username = $_GET["username"];
			$password = $_GET["password"];
			$dbname = "Klassenkassa";
			$conn = null;

			function connect(){
				global $host, $username, $password, $dbname, $conn;
				
				$conn = null;
				
				try {
					$conn = new PDO("mysql:host={$host};dbname={$dbname}", $username, $password);
					$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
					return true;
				}catch(PDOException $e){
					echo json_encode(array("message" => "Wrong username or password!"));
					return false;
				}
			}
			
			function closeConnection(){
				global $conn;
				$conn = null;
			}
			
			function mainFunction(){
				global $conn;
				$connection = connect();//connect to database
				
				if($connection){
					try{
					$sql = "SELECT * 
							FROM student;";
					
					$res = $conn->query($sql);
					
					$resarray = array();
					while($row=$res->fetchAll(PDO::FETCH_OBJ))
					{
						$resarray[] = $row;
					}
					echo json_encode($resarray);
					
					
					closeConnection();//close connection to database
					}catch(PDOException $e){
						echo json_encode(array("message" => "Table does not exist!"));
					}
				}
			}
	  		
	  		mainFunction();
?>