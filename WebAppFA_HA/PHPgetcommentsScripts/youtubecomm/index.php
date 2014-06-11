<html>
<head>
<!-- <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script> -->

	<script src="mootools-core-1.4.4.js" type="text/javascript" charset="utf-8"></script>
	<script src="mootools-more-1.4.0.1.js" type="text/javascript" charset="utf-8"></script>
</head>
<body>
<div id="comments">
</div>
<input type="text" id="vidid" name="vidid" value="" />
<input type="button" value="getcoments" onclick="addChannel();" />
<script type="text/javascript">


var channelqueue = [];
var videoqueue = [];
var lastrequestsuccess = true;
var lastreqtype = 0;
var failcount = 0;

var lastvidcount = 0;
var lastcomcount = 0;
var vidpage = 0;
var compage = 0;

var currvid = {};
var currchan = '';

var vididg = 0;
var chanidg = 0;


function logit(what){
	var ocomment = new Element('div', {
		'class':'comment'
	});
    
    ocomment.set('text',what);
    
    $('comments').adopt(ocomment);
}


function funcaoperiodica(){
	var nexttimer = 100;
	var logstr = '';
	if(lastrequestsuccess){
		logstr += "Last req success; ";
		lastrequestsuccess = false;
		failcount=0;
		if(lastcomcount==50){
			lastcomcount = 0;
			compage++;
			lastreqtype = 2;
			vididg = currvid['bdid'];
			chanidg = currvid['chanbdid'];
			logstr += "Continue comms v:"+JSON.encode(currvid)+" p:"+compage+"; ";
			getYoutubeComents.delay(nexttimer,false,[currvid['vid'], compage]);
		}else if(videoqueue.length>0){
			currvid = videoqueue.shift();
			lastcomcount = 0;
			compage = 0;
			lastreqtype = 2;
			logstr += "Next vid v:"+JSON.encode(currvid)+" p:"+compage+"; ";
			vididg = currvid['bdid'];
			chanidg = currvid['chanbdid'];
			getYoutubeComents.delay(nexttimer,false,[currvid['vid'], compage]);
		}else if(lastvidcount==10){
			lastvidcount = 0;
			vidpage++;
			lastreqtype = 1;
			logstr += "Continue vids v:"+currchan+" p:"+vidpage+"; ";
			goVideosChannel.delay(nexttimer,false,[currchan, vidpage]);
		}else if(channelqueue.length>0){
			currchan = channelqueue.shift();
			lastvidcount = 0;
			vidpage = 0;
			lastreqtype = 1;
			logstr += "Next chan v:"+currchan+" p:"+vidpage+"; ";
			goVideosChannel.delay(nexttimer,false,[currchan, vidpage]);
		}else {
			logstr = "Nothing to do; ";
			lastrequestsuccess = true;
		}
	}else {
		logstr += "Last req failed; ";
		nexttimer = 180000;
		failcount++;
		if(failcount==1)
			lastrequestsuccess=true;
		if(lastreqtype==2){
			lastcomcount = 0;
			logstr += "Repeat comm req v:"+JSON.encode(currvid)+" p:"+compage+"; ";
			vididg = currvid['bdid'];
			chanidg = currvid['chanbdid'];
			getYoutubeComents.delay(nexttimer,false,[currvid['vid'], compage]);
		}else if(lastreqtype==1){
			lastvidcount = 0;
			logstr += "Repeat vid req v:"+currchan+" p:"+vidpage+"; ";
			goVideosChannel.delay(nexttimer,false,[currchan, vidpage]);
		}

	}
	logit(logstr);
	funcaoperiodica.delay(nexttimer+5900);
}

function addChannel(){
	var videotoget = $('vidid').get('value');
	channelqueue.push(videotoget);
	logit("Added channel "+videotoget+";");
}

function getPostRequestSync(dataPost){
    var respostaReq;
    var myRequest = new Request({
        url: 'http://localhost/sigc/youtubecomm/jsresp.php?opt=comments&time='+new Date().getTime(),
        method: 'post',
        async:false,
        onRequest: function() {
        },
        onSuccess: function(responseText){
            var resposta = JSON.decode(responseText);
            respostaReq = resposta;
            },
        onFailure: function() {
            alert('Sorry, your request failed :(');
        },
        onException: function(){
            alert('Sorry, your request failed :(');
        },
        data: dataPost
    });
    myRequest.post();
    return respostaReq;
}



function goVideosChannel(channelname, page){

	var numperpage = 10;

    var startc = numperpage*page+1;
    var maxres = 10;

    var respostaReq;

    var returnedvids = 0;

    var myJSONP = new Request.JSONP({
	    url: "https://gdata.youtube.com/feeds/api/videos?q="+channelname+"&v=2&alt=json",
	    log: true,
	    async: false,
	    data: {
	        'start-index': startc,
	        'max-results': maxres,
	        'orderby':'published'
	    },
	    onRequest: function(url){
	        // a script tag is created with a src attribute equal to url
	    },
	    onComplete: function(data){
	        //alert(JSON.encode(data.feed.entry));
	        lastrequestsuccess = true;

	        if(data.feed.entry!=undefined)
	        	lastvidcount = data.feed.entry.length;
	        else
	        	lastvidcount = 0;

	        if(data.feed.entry!=undefined){
		        for(var i=0; i<data.feed.entry.length; i++) {
		       	//for(var i=0; i<2; i++) {

		        	var dadosvideo = {};
		        	Object.append(dadosvideo,data.feed.entry[i]['gd$rating']);
		        	delete dadosvideo.rel;
		        	/*dadosvideo['average'] = data.feed.entry[i]['gd$rating']['average'];
		        	dadosvideo['max'] = data.feed.entry[i]['gd$rating']['max'];
		        	dadosvideo['min'] = data.feed.entry[i]['gd$rating']['min'];
		        	dadosvideo['numRaters'] = data.feed.entry[i]['gd$rating']['numRaters'];*/
		        	Object.append(dadosvideo,data.feed.entry[i]['yt$rating']);
		        	Object.append(dadosvideo,data.feed.entry[i]['yt$statistics']);

		        	var idstr = data.feed.entry[i].id['$t'].split(':');
		        	dadosvideo['vid'] = idstr[idstr.length-1];

					videotags = [];

					for(var j=0; j<data.feed.entry[i].category.length; j++){
						if(data.feed.entry[i].category[j]['label']==undefined||data.feed.entry[i].category[j]['term']==undefined){

						}else {
							videotags.push({ 'label': data.feed.entry[i].category[j]['label'], 'term': data.feed.entry[i].category[j]['term']});
						}
					}	        	

					dadosvideo['title'] = data.feed.entry[i].title['$t'];

					var dataPost = {
						'todo':'newvideoinfo',
						'channel':channelname,
						'vid':dadosvideo['vid'],
						'vidinfo':dadosvideo,
						'vidtags':videotags
					};

					resposta = getPostRequestSync(dataPost);

					//vididg = resposta['vid'];
					//chanidg = resposta['chanid'];

					//gettingcomments=true;
					//lastgetcomupdate = new Date().getTime();
					videoqueue.push({'vid':dadosvideo['vid'],'bdid':resposta['vid'],'chanbdid':resposta['chanid']});
					//getYoutubeComents(dadosvideo['vid'], 0);


				    /*var ocomment = new Element('div', {
						'class':'comment'
					});
		            
		            var author = new Element('a', {
		            	'target':'_blank',
		            	'class': 'youtube_user'
		            });
		            author.set('href', "http://youtube.com/user/" + data.feed.entry[i].title['$t']);
		            author.set('html',data.feed.entry[i].title['$t']);
		            
		            var content = new Element('div',{
		            	'class':'content',
		            	styles:{
		            		'font-size':'14px'
		            	}
		            });
		            content.set('html',JSON.encode(dataPost));
		            
		            ocomment.adopt(author).adopt(content);
		            $('comments').adopt(ocomment);*/
				}
			}
			logit("finished video req n="+lastvidcount+";");
			
			//alert(returnedvids);
			/*if(returnedvids==10){
				getNextPageVideos.delay(20000,false,[channelname,page+1]);
			}else{
				alert('finito!');
			}*/

	    }
	}).send();

	//return returnedvids;
}

//var vididg = 0;
//var chanidg = 0;

function getYoutubeComents(videoId, page){

	gettingcomments=true;
	lastgetcomupdate = new Date().getTime();

    var numperpage = 50;

    var startc = numperpage*page+1;
    var maxres = 50;

    var respostaReq;

    var returnedcoms = 0;

    var myJSONP = new Request.JSONP({
	    url: "http://gdata.youtube.com/feeds/api/videos/"+videoId+"/comments?v=2&alt=json",
	    log: true,
	    async: false,
	    data: {
	        'start-index': startc,
	        'max-results': maxres
	    },
	    onRequest: function(url){
	        // a script tag is created with a src attribute equal to url
	    },
	    onComplete: function(data){
	        //alert(JSON.encode(data.feed.entry));
			lastrequestsuccess = true;

	        if(data.feed.entry!=undefined)
	        	lastcomcount = data.feed.entry.length;
	        else
	        	lastcomcount = 0;
	        
	        //returnedcoms = data.feed.entry.length;
	        if(data.feed.entry!=undefined){
		        data.feed.entry.each(function(item,index){
					/*var ocomment = new Element('div', {
						'class':'comment'
					});
		            
		            var author = new Element('a', {
		            	'target':'_blank',
		            	'class': 'youtube_user'
		            });
		            author.set('href', "http://youtube.com/user/" + item['author'][0]['name']['$t']);
		            author.set('html',item['author'][0]['name']['$t']);*/

		            if(item['content']['$t'].length>1){

			            var dataPostC = {
			            	'todo':'insertcomment',
			            	'vid':vididg,
			            	'cid':chanidg,
			            	'user':item['author'][0]['name']['$t'],
			            	'comment':item['content']['$t']
			            }

			            var resposta = getPostRequestSync(dataPostC);

		            }
		            
		            /*var content = new Element('div',{
		            	'class':'content',
		            	styles:{
		            		'font-size':'14px'
		            	}
		            });
		            content.set('html',item['content']['$t']);
		            
		            ocomment.adopt(author).adopt(content);
		            $('comments').adopt(ocomment);*/
		        });
			}
			logit("finished comm req n="+lastcomcount+";");
			/*if(returnedcoms==10){
				getYoutubeComents.delay(10000,false,[videoId, page+1]);
			}else {
				gettingcomments=false;
				lastgetcomupdate = 0;
			}*/
	    }
	}).send();

    //return returnedcoms;
}

funcaoperiodica();

</script>
</body>	
</html>