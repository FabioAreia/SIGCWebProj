/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


jQuery.extend({
    highlight: function (node, re, nodeName, className) {
        if (node.nodeType === 3) {
            var match = node.data.match(re);
            if (match) {
                var highlight = document.createElement(nodeName || 'span');
                highlight.className = className || 'highlight';
                var wordNode = node.splitText(match.index);
                wordNode.splitText(match[0].length);
                var wordClone = wordNode.cloneNode(true);
                highlight.appendChild(wordClone);
                wordNode.parentNode.replaceChild(highlight, wordNode);
                return 1; //skip added node in parent
            }
        } else if ((node.nodeType === 1 && node.childNodes) && // only element nodes that have children
                !/(script|style)/i.test(node.tagName) && // ignore script and style nodes
                !(node.tagName === nodeName.toUpperCase() && node.className === className)) { // skip if already highlighted
            for (var i = 0; i < node.childNodes.length; i++) {
                i += jQuery.highlight(node.childNodes[i], re, nodeName, className);
            }
        }
        return 0;
    }
});

jQuery.fn.unhighlight = function (options) {
    var settings = { className: 'highlight', element: 'span' };
    jQuery.extend(settings, options);

    return this.find(settings.element + "." + settings.className).each(function () {
        var parent = this.parentNode;
        parent.replaceChild(this.firstChild, this);
        parent.normalize();
    }).end();
};

jQuery.fn.highlight = function (words, options) {
    var settings = { className: 'highlight', element: 'span', caseSensitive: false, wordsOnly: false };
    jQuery.extend(settings, options);

    if (words.constructor === String) {
        words = [words];
    }
    words = jQuery.grep(words, function(word, i){
      return word != '';
    });
    words = jQuery.map(words, function(word, i) {
      return word.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&");
    });
    if (words.length == 0) { return this; };

    var flag = settings.caseSensitive ? "" : "i";
    var pattern = "(" + words.join("|") + ")";
    if (settings.wordsOnly) {
        pattern = "\\b" + pattern + "\\b";
    }
    var re = new RegExp(pattern, flag);

    return this.each(function () {
        jQuery.highlight(this, re, settings.element, settings.className);
    });
};


var currentMenu = 1;
var currentPage = 0;
var totalpages = 0;
var currentPageVideos = 0;
var totalpagesVideos = 0;
var currentPageUsers = 0;
var totalpagesUsers = 0;
var currSearch = "";

var gettingcomments;
var lastgetcomupdate = new Date().getTime();

var vididg;
var chanidg;
var lastcomcount = 0;
var lastpage = -1;
var lastrequestsuccess = false;
var videoComments = [];


function initStuff() {
    $('#pages').hide();
    $('#pagesvideos').hide();
    $('#pagesusers').hide();
    $('#resultsvideos').hide();
    $('#resultsusers').hide();
    showMenu(2);
    updateStatShow();
}

function showMenu(idx) {
    $('#content_1').hide();
    $('#content_2').hide();
    $('#content_3').hide();
    $('#content_4').hide();

    $('#content_' + idx).show();
    currentMenu = idx;
}

function showPesquisa(idx){
    $('#resultscomments').hide();
    $('#resultsvideos').hide();
    $('#resultsusers').hide();
    
    if(idx==0){
        $('#resultscomments').show();
    }else if(idx==1){
        $('#resultsvideos').show();
    }else {
        $('#resultsusers').show();
    }
}

function startpage() {
    searchc(0);
    currentPage = 0;
}
function endpage() {
    searchc(totalpages);
    currentPage = totalpages;
}
function previouspage() {
    currentPage--;
    if (currentPage < 0)
        currentPage = 0;
    searchc(currentPage);
}
function nextpage() {
    currentPage++;
    if (currentPage > totalpages)
        currentPage = totalpages;
    searchc(currentPage);
}


function startpagev() {
    searchv(0);
    currentPageVideos = 0;
}
function endpagev() {
    searchv(totalpagesVideos);
    currentPageVideos = totalpagesVideos;
}
function previouspagev() {
    currentPageVideos--;
    if (currentPageVideos < 0)
        currentPageVideos = 0;
    searchv(currentPageVideos);
}
function nextpagev() {
    currentPageVideos++;
    if (currentPageVideos > totalpagesVideos)
        currentPageVideos = totalpagesVideos;
    searchv(currentPageVideos);
}



function startpageu() {
    searchu(0);
    currentPageUsers = 0;
}
function endpageu() {
    searchu(totalpagesUsers);
    currentPageUsers = totalpagesUsers;
}
function previouspageu() {
    currentPageUsers--;
    if (currentPageUsers < 0)
        currentPageUsers = 0;
    searchu(currentPageUsers);
}
function nextpageu() {
    currentPageUsers++;
    if (currentPageUsers > totalpagesUsers)
        currentPageUsers = totalpagesUsers;
    searchu(currentPageUsers);
}

function searchcomments(pagina) {

    var searchtext = $('#procura').val();
    currSearch = searchtext;
    searchc(pagina);
    searchv(0);
    searchu(0);
}

function searchc(pagina) {
    var numperpage = 5;
    var searchtext = currSearch;
    //alert(searchtext);
    $.get("http://localhost:8080/comments/sigc/pesquisa", {"query": searchtext, "page": pagina, "numperpage": numperpage, "toget":"comment"})
            .done(function(data) {
                var dataqry = jQuery.parseJSON(data);
                //alert(dataqry['results']);
                $('#comchooser').text('Comments ('+dataqry['results']+')');
                if (dataqry['results'] > numperpage) {
                    $('#pages').show();
                    totalpages = Math.ceil(dataqry['results'] / numperpage);
                }
                $('#results').empty();
                data = dataqry['docs'];
                for (var h = 0; h < data.length; h++) {

                    //alert(data[h]["titulo"]);
                    var tempelem = $("<div class=\"resultadoc\" onclick=\"vididg='"+data[h]["video"]+"'; $('#procuraclass').val('http://www.youtube.com/watch?v='+vididg);  getYoutubeComents('"+data[h]["video"]+"', 0); showMenu(1);\">");
                    tempelem.appendTo('#results');
                    
                    $("<div class=\"score\">").html("<label class=\"reslabel\">Score:</label> " + parseFloat(data[h]["searchscore"]).toFixed(2)).appendTo(tempelem);
                    $("<div class=\"titulo\">").html("<label class=\"reslabel\"></label><span id=\"coment_"+h+"\">" + data[h]["comment"]+"</span>").appendTo(tempelem);
                    $("<div class=\"canal\">").html("<label class=\"reslabel\">Canal:</label> " + data[h]["canal"]).appendTo(tempelem);
                    
                    $("<div class=\"user\">").html("<label class=\"reslabel\">User:</label> " + data[h]["user"]).appendTo(tempelem);
                    $("<div class=\"rating\">").html("<label class=\"reslabel\">Class:</label> <span id=\"rating_"+h+"\"></span>").appendTo(tempelem);
                    
                    var palavras = currSearch.split(' ');
                    for (var r = 0; r < palavras.length; r++) {
                        $("#coment_"+h).highlight(palavras[r], { element: 'span', className: 'matchword' });
                    }
                    
                }
                
                sentimentcommentTarget('rating_'+0);
            });
}


function searchv(pagina) {
    var numperpage = 5;
    var searchtext = currSearch;
    //alert(searchtext);
    $.get("http://localhost:8080/comments/sigc/pesquisa", {"query": searchtext, "page": pagina, "numperpage": numperpage, "toget":"titulo"})
            .done(function(data) {
                var dataqry = jQuery.parseJSON(data);
                //alert(dataqry['results']);
                $('#vidchooser').text('Videos ('+dataqry['results']+')');
                if (dataqry['results'] > numperpage) {
                    $('#pagesvideos').show();
                    totalpagesVideos = Math.ceil(dataqry['results'] / numperpage);
                }
                $('#resultvideos').empty();
                data = dataqry['docs'];
                for (var h = 0; h < data.length; h++) {

                    //alert(data[h]["titulo"]);
                    var tempelem = $("<div class=\"resultadob\" onclick=\"vididg='"+data[h]["video"]+"'; $('#procuraclass').val('http://www.youtube.com/watch?v='+vididg); getYoutubeComents('"+data[h]["video"]+"', 0); showMenu(1);\">");
                    tempelem.appendTo('#resultvideos');
                    
                    $("<div class=\"score\">").html("<label class=\"reslabel\">Score:</label> " + parseFloat(data[h]["searchscore"]).toFixed(2)).appendTo(tempelem);
                    $("<div class=\"titulo\">").html("<label class=\"reslabel\">Título:</label> <span id=\"titulo_"+h+"\">" + data[h]["titulo"]+"</span>").appendTo(tempelem);
                    $("<div class=\"canal\">").html("<label class=\"reslabel\">Canal:</label> " + data[h]["canal"]).appendTo(tempelem);
                    
                    $("<div class=\"user\">").html("<label class=\"reslabel\">User:</label> " + data[h]["user"]).appendTo(tempelem);
                    $("<div class=\"rating\">").html("<label class=\"reslabel\">Rating:</label> " + parseFloat(data[h]["score"]).toFixed(2)).appendTo(tempelem);
                    $("<div class=\"likes\">").html("<label class=\"reslabel\">Likes:</label> " + data[h]["likes"]).appendTo(tempelem);
                    $("<div class=\"likes\">").html("<label class=\"reslabel\">Dislikes:</label> " + data[h]["dislikes"]).appendTo(tempelem);
                    $("<div class=\"tema\">").html("<label class=\"reslabel\">Tema:</label> " + data[h]["tema"]).appendTo(tempelem);
                    $("<div class=\"commentr\">").html("<label class=\"reslabel\">Comment:</label> <span id=\"comentv_"+h+"\">" + data[h]["comment"]+"</span>").appendTo(tempelem);
                    
                    
                    var palavras = currSearch.split(' ');
                    for (var r = 0; r < palavras.length; r++) {
                        $("#titulo_"+h).highlight(palavras[r], { element: 'span', className: 'matchword' });
                        $("#comentv_"+h).highlight(palavras[r], { element: 'span', className: 'matchword' });
                    }
                }
                
                
            });
}


function searchu(pagina) {
    var numperpage = 5;
    var searchtext = currSearch;
    //alert(searchtext);
    $.get("http://localhost:8080/comments/sigc/pesquisa", {"query": searchtext, "page": pagina, "numperpage": numperpage, "toget":"user"})
            .done(function(data) {
                var dataqry = jQuery.parseJSON(data);
                //alert(dataqry['results']);
                $('#usrchooser').text('Users ('+dataqry['results']+')');
                if (dataqry['results'] > numperpage) {
                    $('#pagesusers').show();
                    totalpagesUsers = Math.ceil(dataqry['results'] / numperpage);
                }
                $('#resultusers').empty();
                data = dataqry['docs'];
                for (var h = 0; h < data.length; h++) {

                    //alert(data[h]["titulo"]);
                    var tempelem = $("<div class=\"resultadou\" >");
                    tempelem.appendTo('#resultusers');
                    
                    $("<div class=\"score\">").html("<label class=\"reslabel\">Score:</label> " + parseFloat(data[h]["searchscore"]).toFixed(2)).appendTo(tempelem);
                    
                    $("<div class=\"titulo\">").html("<label class=\"reslabel\">User:</label> <span id=\"user_"+h+"\">" + data[h]["user"]+"</span>").appendTo(tempelem);
                    
                    
                    var palavras = currSearch.split(' ');
                    for (var r = 0; r < palavras.length; r++) {
                        $("#user_"+h).highlight(palavras[r], { element: 'span', className: 'matchword' });
                        
                    }
                    
                }
                
                
            });
}


function classifycomment(searchtext) {

$('#classification').empty();
    
    //alert(searchtext);
    $.ajax({
        type: 'POST',
        contentType: 'application/json',
        url: 'http://localhost:8080/comments/sigc/pesquisa/class',
        dataType: "json",
        data: JSON.stringify({"query": searchtext}),
        success: function(data) {
                //var dataqry = jQuery.parseJSON(data);
                dataqry = data;
                //alert(dataqry['result']);
                var tempelem = $("<div class=\"resultado\">");
                tempelem.appendTo('#classification');
                $("<div class=\"comment\">").html("<label class=\"reslabel\" for=\"chanclass\">Canal:</label> <span id=\"chanclass\">" + dataqry['result'] + "</span> <label class=\"reslabel\" for=\"vidrating\">Rating:</label> <span id=\"vidrating\"></span><br/><iframe width=\"560\" height=\"315\" src=\"//www.youtube.com/embed/"+vididg+"\" frameborder=\"0\" style=\"margin-top:10px;\" allowfullscreen></iframe>").appendTo(tempelem);
            },
        error: function(jqXHR, textStatus, errorThrown){
            
        }
    });
    
    /*$.post("http://localhost:8080/comments/sigc/pesquisa/class", {"query": searchtext},function(data) {
                var dataqry = jQuery.parseJSON(data);
                //alert(dataqry['result']);
                var tempelem = $("<div class=\"resultado\">");
                tempelem.appendTo('#classification');
                $("<div class=\"comment\">").html("<label class=\"reslabel\" for=\"chanclass\">Canal:</label> <span id=\"chanclass\">" + dataqry['result'] + "</span> <label class=\"reslabel\" for=\"vidrating\">Rating:</label> <span id=\"vidrating\"></span><br/><iframe width=\"560\" height=\"315\" src=\"//www.youtube.com/embed/"+vididg+"\" frameborder=\"0\" style=\"margin-top:10px;\" allowfullscreen></iframe>").appendTo(tempelem);
            });*/
}

function sentimentcomment(searchtext) {

    $.ajax({
        type: 'POST',
        contentType: 'application/json',
        url: 'http://localhost:8080/comments/sigc/pesquisa/sentiment',
        dataType: "json",
        data: JSON.stringify({"query": searchtext, "x":"0"}),
        success: function(data) {
                //var dataqry = jQuery.parseJSON(data);
                dataqry = data;
                //alert(dataqry['result']);
                
                $('#vidrating').text(((parseFloat(dataqry['result'])+0.7)*3.44).toFixed(2));
                /*var tempelem = $("<div class=\"resultado\">");
                tempelem.appendTo('#classification');
                $("<div class=\"comment\">").html("Canal: " + dataqry['result']).appendTo(tempelem);*/
            },
        error: function(jqXHR, textStatus, errorThrown){
            
        }
    });
    //alert(searchtext);
   
}

function sentimentcommentTarget(elemid) {
    var searchtext = $('#coment_'+elemid.split('_')[1]).text();
    $.ajax({
        type: 'POST',
        contentType: 'application/json',
        url: 'http://localhost:8080/comments/sigc/pesquisa/sentiment',
        dataType: "json",
        data: JSON.stringify({"query": searchtext, "x":"0"}),
        success: function(data) {
                //var dataqry = jQuery.parseJSON(data);
                dataqry = data;
                //alert(dataqry['result']);
                $('#'+elemid).text(((parseFloat(dataqry['result'])+0.7)*3.44).toFixed(2));
                /*var tempelem = $("<div class=\"resultado\">");
                tempelem.appendTo('#classification');
                $("<div class=\"comment\">").html("Canal: " + dataqry['result']).appendTo(tempelem);*/
                var splits = elemid.split('_');
                if(parseInt(splits[1])<4){
                    sentimentcommentTarget(splits[0]+'_'+(parseInt(splits[1])+1));
                }
            },
        error: function(jqXHR, textStatus, errorThrown){
            
        }
    });
    //alert(searchtext);
   
}


function checkVideo() {

    var searchtext = $('#procuraclass').val();
    var s1 = searchtext.split('?');
    if (s1.length == 2) {
        var s2 = s1[1].split('&');
        var s3 = s2[0].split('=');
        var videoId = s3[1];
        vididg = videoId;
        chanidg = 0;
        videoComments = [];
        getYoutubeComents(videoId,0);
    }

}





function getYoutubeComents(videoId, page) {

    gettingcomments = true;
    lastgetcomupdate = new Date().getTime();

    var numperpage = 50;
    lastpage = page;

    var startc = numperpage * page + 1;
    var maxres = 50;

    var respostaReq;

    var returnedcoms = 0;
    
    jQuery.getJSON("http://gdata.youtube.com/feeds/api/videos/" + videoId + "/comments?v=2&alt=json", {'start-index': startc,'max-results': maxres},function(data) {
            //alert(JSON.encode(data.feed.entry));
            lastrequestsuccess = true;

            if (data.feed.entry != undefined)
                lastcomcount = data.feed.entry.length;
            else
                lastcomcount = 0;

            //returnedcoms = data.feed.entry.length;
            if (data.feed.entry != undefined) {
                data.feed.entry.forEach(function(item, index, arr) {
                    /*var ocomment = new Element('div', {
                     'class':'comment'
                     });
                     
                     var author = new Element('a', {
                     'target':'_blank',
                     'class': 'youtube_user'
                     });
                     author.set('href', "http://youtube.com/user/" + item['author'][0]['name']['$t']);
                     author.set('html',item['author'][0]['name']['$t']);*/

                    if (item['content']['$t'].length > 1) {

                        var dataCom = {
                            'vid': vididg,
                            'user': item['author'][0]['name']['$t'],
                            'comment': item['content']['$t']
                        }
                        
                        videoComments.push(dataCom);

                        //var resposta = getPostRequestSync(dataPostC);
                        //alert(JSON.stringify(dataCom));

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
            if(lastcomcount==50&&lastpage<3){
                getYoutubeComents(videoId, lastpage+1);
            }else{
                finishedGettingVideoComs();
            }
            //logit("finished comm req n=" + lastcomcount + ";");
    });

    
}


function finishedGettingVideoComs(){
    //alert(videoComments.length);
    var comall = "";
    for (var g = 0; g<videoComments.length; g++){
        if(g>0){
            comall += " ";
        }
        comall += videoComments[g]['comment'];
    }
    classifycomment(comall);
    sentimentcomment(comall);
}
            
            
function updateStatShow(){
    var valor = $('#choosestat').val();
    var filhos = $('#statsresults').children();
    //alert(filhos.length);
    for (var h=0; h<filhos.length; h++){
        $(filhos[h]).hide();
        if(h==valor-1){
            $(filhos[h]).show();
        }
    }
    
}