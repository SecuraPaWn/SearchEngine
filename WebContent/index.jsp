<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Our Search Engine</title>
<link rel="stylesheet" type="text/css" href="css/main.css" />
<script type="text/javascript"
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.js"></script>
<script><!--

$(document).ready(function(){ 
            $('#searchBtn').click(function(){
				$("#container").empty();
				var txtVal = $("#SearchBox").val();
                $.ajax({
                    url:'testing',
                    type:'get',
					data: 'querystr='+txtVal,
                    dataType: 'json',
                    success: function(res) {
						var result = res.Results;
						if(result.length == 0) { alert("No Records Found"); }
						for(var i=0; i < result.length; i++) {
							$('#container').append("<div id=\"link\"><a href=\""+ result[i].URL + "\" target=\"_blank\">"+(result[i].Title).replace(/[^\w\s]/gi, '')+"</a></div>");
							$('#container').append("<div id=\"url\">"+result[i].URL+"</div>");
							$('#container').append("<div id=\"contents\">"+result[i].Contents+"</div>");
							$('#container').append("<br/>");
						}
                    }
                });
            });
    });

--></script>


</head>
<body>

</body>
<div id="titlearea">UCI Search Engine</div>
<div id="searcharea">
	<input type="text" maxLength=40 name=text1 id="SearchBox"
	style="WIDTH: 500px; HEIGHT: 28px" size=20 /> 
	<input type="button"
	style="margin-LEFT: 20px; WIDTH: 150px; HEIGHT: 35px; font-weight: bolder" id="searchBtn"
	align="middle" value="SEARCH"/>
</div>
<div id="container"></div>
</html>