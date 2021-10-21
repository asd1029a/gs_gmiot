
function createJSTree(id,jsondata) {
	$('#'+id).jstree({
		"core": {
			"data": jsondata,
			 "themes":{
		            "icons":false
		        }
		},
		"checkbox" : {
			"keep_selected_style" : false
		},
		"plugins" : [ "checkbox" ]
	});
}

var areaCode = [];
function comboArea(id) {
	const jsonObj = {};
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type        : "POST",
		url         : "/select/common.getAreaSiCode/action",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
		async       : false,
		beforeSend: function (xhr) {
			// 전송 전 Code
		},
	}).done(function (result) {
		const rows = result.rows;
    	if(rows=='sessionOut'){
			alert('로그인 시간이 만료되었습니다.');
			closeWindow();
		}
    	
    	var treeData = [];
    	
    	if(rows.length > 1) {
        	const topP = {};
        	topP.parent = '#';
        	topP.id = 1;
        	topP.text = rows[0].cityNm;
        	treeData.push(topP);
    	}
    	
		var siTemp = {};
		$.each(rows, function (index) {
			siTemp.parent = treeData.length>0?1:'#';
			siTemp.id = Number(rows[index].siCd);
			siTemp.text = rows[index].siNm;

			treeData.push({id: siTemp.id, text: siTemp.text, parent: siTemp.parent});
			
			jsonObj.siCd = rows[index].siCd;
			$.ajax({
			    contentType : "application/json; charset=utf-8",
				type      : "POST",
				url       : "/select/common.getAreaDongCode/action",
				dataType  : "json",
				data      : JSON.stringify(jsonObj),
				async     : false,
				beforeSend: function (xhr) {
					// 전송 전 Code
				},
			}).done(function (result2) {
				const rows2 = result2.rows;
		    	if(rows2=='sessionOut'){
					alert('로그인 시간이 만료되었습니다.');
					closeWindow();
				}
				var dongTemp = {};
				$.each(rows2, function (index2) {
					dongTemp.id = Number(rows2[index2].dongCd);
					dongTemp.text = rows2[index2].dongNm;
					dongTemp.parent = rows[index].siCd;
					
					treeData.push({id: dongTemp.id, text: dongTemp.text, parent: dongTemp.parent});
					
				});
			});
		});
		createJSTree(id,treeData);
		
		$('#'+id).on("changed.jstree", function(e, data) {
			areaCode = data.selected;
		});
		
 	}).fail(function (xhr) {
		console.log(JSON.stringify(xhr));
	}).always(function() {
		
	});
}

var useCode = [];
function comboFcltUse(id,user,purpose) {
	useCode = [];
	$('#'+id).jstree("deselect_all").jstree("close_all");
	const jsonObj = {};
	jsonObj.userId = user;
	jsonObj.chkDeGrpCd = purpose
	
	$.ajax({
        contentType : "application/json; charset=utf-8",
		type      : "POST",
		url       : "/select/oprt.getCodeDetailList/action",
		dataType  : "json",
		data      : JSON.stringify(jsonObj),
		async     : false,
		beforeSend: function (xhr) {
			// 전송 전 Code
		},
	}).done(function (result) {
		const rows = result.rows;
    	if(rows=='sessionOut'){
			alert('로그인 시간이 만료되었습니다.');
			closeWindow();
		}
    	var treeData = [];
    	var temp = {};
    	temp.parent = '#';
		temp.id = 0;
		temp.text = '전체';
		treeData.push(temp);
		
		var temp2 = {};
		$.each(rows, function (index) {
    		//$('#'+id).append('<option value="'+rows[index].deCd+'">'+rows[index].deCdNm+'</option>');
			//temp2.id = Number(rows[index].deCd);
			temp2.id = rows[index].deCd;
			temp2.text = rows[index].deCdNm;
			temp2.parent = 0;
			
			treeData.push({id: temp2.id, text: temp2.text, parent: temp2.parent});
    	});
		if($('#' + id).jstree(true) == false){
			createJSTree(id,treeData);
		} else {
			$('#' + id).jstree(true).settings.core.data = treeData;
			$('#' + id).jstree(true).refresh();
		}
		
		$('#'+id).on("changed.jstree", function(e, data) {
			useCode = data.selected;
		});
 	}).fail(function (xhr) {
		console.log(JSON.stringify(xhr));
	}).always(function() {
		
	});
}

function comboEventGbn(id,user) {
	const jsonObj = {};
	jsonObj.userId = user;
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type      : "POST",
		url       : "/select/common.getEventGbnCode/action",
		dataType  : "json",
		data      : JSON.stringify(jsonObj),
		async     : false,
		beforeSend: function (xhr) {
			// 전송 전 Code
		},
	}).done(function (result) {
		const rows = result.rows;
    	if(rows=='sessionOut'){
			alert('로그인 시간이 만료되었습니다.');
			closeWindow();
		}
    	console.log(rows);
    	
    	$.each(rows, function (index) {
    		$('#'+id).append('<option value="'+rows[index].code+'">'+rows[index].name+'</option>');
    	});
	});
}