<?php
ini_set('display_errors', 0);
require_once('TwitterAPIExchange.php');
require_once('twittersources.php');

/** Set access tokens here - see: https://dev.twitter.com/apps/ **/
$settings = array(
    'oauth_access_token' => "15742542-uu3rMh9eR80YGBaKR8oHVpmCHzmp7RFHzwof9q5v6",
    'oauth_access_token_secret' => "v5RmrzpmP4HzW4ui9or8ImEs0ZMBzpro69bb2ilWuM",
    'consumer_key' => "SkctTZK91bKDpvbCSf4HA",
    'consumer_secret' => "VOUWIQs8hEE15DdVCgxA4LlsEFDpVmRjtwY7DQqvx5I"
);

$twitter = new TwitterAPIExchange($settings);

$mylocation = "".$_GET['l']; // location parameter passed into the script (see twittersources.php)

jobHunt($twitter,$twitterlocal,$twitternational,$mylocation); // pass control to controller method


/*
	CONTROLLER METHOD
*/
function jobHunt($twitter,$twitterlocal,$twitternational,$mylocation) {
	// 
	if($mylocation=='all' || $mylocation=="")
		updateNational($twitter,$twitternational);
	else {
		updateLocal($twitter,$twitterlocal[$mylocation]);
	}
}



/*
	UPDATE A LOCAL AREA FEED
*/
function updateLocal($twitter,$source) {
	$jobs = getJobs($twitter,$source);
	$jobsarray = populateArray($jobs);
	$jobsjson = arrayToJson($jobsarray);
	echo $jobsjson;
}



/*
	UPDATE THE MASTER FEED
*/
function updateNational($twitter,$twitternational) {
	// add individual feed into big array
	foreach($twitternational as $source) {
		$jobs = getJobs($twitter,$source);
		$jobsarray = populateArray($jobs);
		$testarray[] = $jobsarray;
	}
	
	// collapse big array to make it flat
	foreach($testarray as $jobset) {
		foreach($jobset as $job) {
			$finalarray[] = $job;
		}
	}
	
	// convert to JSON and output
	$jobsjson = arrayToJson($finalarray);
	echo $jobsjson;
}


/*
	PERFORM JOB SEARCH
*/
function getJobs($twitter,$mysource) {
	// set parameters
	$url = 'https://api.twitter.com/1.1/search/tweets.json';
	$getfield = '?q=from:'.$mysource.'+filter:links+-rt&count=100';
	$requestMethod = 'GET';
  
  	// get JSON result set
	$myjson = $twitter->setGetfield($getfield)
				 ->buildOauth($url, $requestMethod)
				 ->performRequest();
	
	// decode JSON to array	 
	$data = json_decode($myjson);
	return $data;
}



/*
	CYCLE THROUGH ALL JOB RESULTS AND POPULATE ARRAY
*/
function populateArray($data) {
	for ($i=0; $i<count($data->statuses); $i++)
	{	
		// ----------------------------- REMOVE URLS ------------------------------ //
		$reg_exUrl = "/(http|https|ftp|ftps)\:\/\/[a-zA-Z0-9\-\.]+\.[a-zA-Z]{2,3}(\/\S*)?/";
		$mytext = $data->statuses[$i]->text;
		if(preg_match($reg_exUrl, $mytext, $url)) {
			$myurl = (string)$url[0]; // only using first link. assuming only 1
			$mytext = str_replace($myurl, "", $mytext); // remove URL from original text
		} else {
			//echo $text;     // if no urls in the text just return the text
		}
	
	
		// ----------------------------- POPULATE ARRAY ---------------------------- //
		$twitter_results[$i] = array(
					'timestamp'		=>	strtotime($data->statuses[$i]->created_at),
					'username'		=>	$data->statuses[$i]->user->screen_name,
					'description'	=>	$mytext,
					'url'			=>	$myurl,
					//'post_id'		=>	$data->statuses[$i]->id_str,
					'source'		=>	"twitter"
				);
	}

	return $twitter_results;
}



/*
	convert array to JSON
*/
function arrayToJson($twitter_results) {
	$json = json_encode($twitter_results);
	return $json;
}

?>