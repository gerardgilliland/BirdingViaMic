<html>
<!-- 
called from Android app BirdingViaMic
called with: 
https://www.modelsw.com/Nvidia/GetSpecieRef.php?Id=0&FileName=/storage/emulated/0/Android/data/com.modelsw.birdingviamic/files/Song/American_Crow_XC197259.m4a
https://www.modelsw.com/Nvidia/GetSpecieRef.php?Id=0&FileName=/storage/emulated/0/Android/data/com.modelsw.birdingviamic/files/Song/House Wren XC101247.m4a
called from web page: 
https://www.modelsw.com/Nvidia/GetSpecieRef.php?Id=0&FileName=C:/Android/unknown/Song/American_Crow_2aCTr.wav
https://www.modelsw.com/Nvidia/GetSpecieRef.php?Id=0&FileName=C:/Android/unknown/Song/American Robin_XC171727Tr.wav
after transfer success: run the delete query below
-->

<head>
<?php
    require __DIR__ . '/vendor/autoload.php'; 
    include composer.json;
    include composer.lock;
    include vendor;
    echo "PHP_OS_FAMILY: " .PHP_OS_FAMILY . "<br>";	
    echo "lsb_release -a: Debian GNU/Linux 10 (buster) <br>";
    echo "PHP Version: " . PHP_VERSION . "<br>";	
    $cwd = getcwd(); // current working directory
    set_include_path($cwd . PATH_SEPARATOR . 'phpseclib3');
    echo "cwd: $cwd<br>";
    set_include_path('/usr/lib/php7.4');

    echo "<br>Database<br>";
    $host_name = 'db5005901698.hosting-data.io';
    $database = 'dbs4947775';
    $db_user = 'dbu1628153';
    $password = 'password';
    $link = new mysqli($host_name, $db_user, $password, $database);
    if ($link->connect_error) {
        die('Failed to connect to MySQL: '. $link->connect_error .'<br>');
    } else {
        echo 'Connected to MySQL server.<br>';
    }
    $table = 'tblUniqueNumber';
    // **************************************
    // disable -- COMMENT OUT this delete query when done testing 
    /*
    $qry = "DELETE FROM $table WHERE Num > 2";
    //echo "qry: $qry<br>";
    $rs = mysqli_query($link, $qry);
    $affr = mysqli_affected_rows($link);
    echo "row count: " . $affr . " (positive number is success.)<br>";	
    exit();
    */
    // **************************************
    $id = $_GET[Id]; // individual 
    $localName = $_GET[FileName]; // includes path
    echo "localName: $localName<br>";	
    if ($localName == "") {
        echo "GET[NameNotPassed]<br>";
        exit();
    }
    $qry = "SELECT MAX(Num) as MaxNum FROM $table";
    //echo "qry: $qry<br>";
    $rs = mysqli_query($link, $qry);
    $row = mysqli_fetch_row($rs); 
    $num = $row[0];
    $num += 1; 	
    $inx = strpos($localName, "/Song/")+6;
    $songName = substr($localName,$inx); // just the song name	
    echo "songName: $songName<br>";
    $remoteName = "/Song/".$songName;  
    echo "remoteName: $remoteName<br>";	
    $q = chr(34);
    $songName = $q . $songName . $q;
    $specieRef = 0;
    $percent = 0;
    $accepted = 0;
    $modified = "";
    $modified = $q . $modified . $q;
    // INSERT INTO $table VALUES(0, 2, "American Robin2.m4a", 0, 0, 0, "")
    $qry = "INSERT INTO $table VALUES ($id, $num, $songName, $specieRef, $percent, $accepted, $modified)";
    echo "qry: $qry<br>";	
    $rs = mysqli_query($link, $qry);
    $affr = mysqli_affected_rows($link);
    echo "row count: " . $affr . " (positive number is success.)<br>";	

    echo "<br>File Transfer<br>";
    // On linux, one would enable ftp on their php7 server by uncommenting or adding the line "extension=ftp.so" 
    // in their php.ini file (likely at /etc/php/php.ini).
    // is this required -- phpseclib3 is installed -- enabling ftp is not mentioned
	
    $server = "home208845805.1and1-data.host";
    $ip = "216.250.121.88";
    echo "server: $server<br>";	
    $user = "u45596567-Nvidia";
    $pw = "password";
    $folder = "/Nvidia";
    $path = $server.$folder;
    set_include_path('/usr/lib/php7.4');
    echo "path: ". $path."<br>";
    set_include_path($path);

    //include('Math/BigInteger.php');
    //include('Math/Common/FiniteField/Integer.php');
    //include('Math/BinaryField/Integer.php');
    //include('Math/Common/FiniteField.php');
    //include('Math/BinaryField.php');
    //include('Crypt/Common/SymmetricKey.php');
    //include('Crypt/Common/BlockCipher.php');
    //include('Crypt/Rijndael.php');
    //include('Common/Functions/Strings.php');
    include('Net/SSH2.php');
    include('Net/SFTP.php');
    use phpseclib3\Net\SFTP;
    use phpseclib3\Net\SSH2;
    $ssh = new SSH2($server, 22);
    if ($expected != $ssh->getServerPublicHostKey()) {
        throw new \Exception('Host key verification failed');
    }	
    echo "Transfer File: $remoteName<br>";
    $sftp = new SFTP($server.$folder);	
    echo "Set new sftp<br>";
    $sftp->login($user, $pw);
    echo "Successful login<br>";
    $sftp->put($remoteName, $localName, SFTP::SOURCE_LOCAL_FILE);
    echo "Transfered: $remoteName<br>";
    exit();

?>
</head>
	
<body>

</body>

</html>
