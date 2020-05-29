<?php
      header("Access-Control-Allow-Origin: *");
      header("Content-Type: application/json; charset=UTF-8");
      header("Access-Control-Allow-Methods: POST");
      header("Access-Control-Max-Age: 3600");
      header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

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
          $data = json_decode(file_get_contents("php://input"));
            if(!empty($data->name) && !empty($data->dueDate) && !empty($data->cost)){

              $sql = "INSERT INTO category (name,dueDate,cost)
                      VALUES ('{$data->name}','{$data->dueDate}','{$data->cost}');";

    					$res = $conn->query($sql);
              if($res){
                http_response_code(201);
                echo json_encode(array("message" => "Category was created."));
              }else{
                http_response_code(503);
                echo json_encode(array("message" => "Unable to create category."));
              }

    					closeConnection();//close connection to database
            }else{
              http_response_code(400);
              echo json_encode(array("message" => "Unable to create category. Data is incomplete."));
            }
					}catch(PDOException $e){
            			echo json_encode(array("message" => "Table does not exist!"));
					}
				}
			}

	  		mainFunction();
?>