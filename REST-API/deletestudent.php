<?php
			$host = "localhost:3306";
			$username = $_GET["username"];
			$password = $_GET["password"];
      		$categoryID = $_GET["categoryID"];
      		$studentID = $_GET["studentID"];
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
				global $conn, $categoryID, $studentID;
				$connection = connect();//connect to database

				if($connection){
					try{
					$sql = "DELETE FROM student WHERE categoryID = {$categoryID} AND studentID = {$studentID};";

					$conn->query($sql);
					echo json_encode(array("message" => "Successful"));

					closeConnection();//close connection to database
					}catch(PDOException $e){
						echo json_encode(array("message" => "Error"));
					}
				}
			}

	  		mainFunction();
?>