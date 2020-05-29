<?php
			$host = "localhost:3306";
			$username = $_GET["username"];
			$password = $_GET["password"];
      		$categoryID = $_GET["categoryID"];
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
				global $conn, $categoryID;
				$connection = connect();//connect to database

				if($connection){
					try{
					$sql = "DELETE c.*,s.*
                  FROM category AS c
                  LEFT JOIN student AS s
                  ON s.categoryID = c.categoryID
                  WHERE c.categoryID = '{$categoryID}';";

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