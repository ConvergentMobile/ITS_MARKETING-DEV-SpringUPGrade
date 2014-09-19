<?php
        session_start();
        if (isset($GLOBALS["HTTP_RAW_POST_DATA"]))
        {
                @header('Content-Type: image/jpeg');
                @header("Content-Disposition: attachment; filename=".$_GET['name']);

                // get bytearray
                $jpg = $GLOBALS["HTTP_RAW_POST_DATA"];

                // Write the new JPG
                list($usec, $sec) = explode(" ", microtime());
                $filename = $sec.substr($usec,2).".png";
                $_SESSION['imageFile'] = "";
                $_SESSION['imageFile'] = $filename;
                $f = fopen("../../outfiles/hotspot/liberty_tax/" . $_SESSION['acnum'] . "_" . $_SESSION['keyword'] . "_" . $filename, 'w');
                fwrite($f, $jpg);
                fclose($f);
        }
?>
