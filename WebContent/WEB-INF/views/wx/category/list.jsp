<%--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>栏目管理</title>
<%
    String theme = com.glaf.core.util.RequestUtils.getTheme(request);
    request.setAttribute("theme", theme);
%> 
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/scripts/artDialog/skins/default.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/scripts/easyui/themes/${theme}/easyui.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/scripts/ztree/css/zTreeStyle/zTreeStyle.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/themes/${theme}/styles.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/icons/styles.css"/>
<%@ include file="/WEB-INF/views/wx/inc/wx_scripts.jsp"%>
<script type="text/javascript">

    var prevTreeNode;

    var setting = {
			async: {
				enable: true,
				url: '<%=request.getContextPath()%>/rs/wx/category/treeJson/${accountId}?type=${type}',
				dataFilter: filter
			},
			callback: {
				onClick: zTreeOnClick
			}
		};
  
  	function filter(treeId, parentNode, childNodes) {
		if (!childNodes) return null;
		for (var i=0, l=childNodes.length; i<l; i++) {
			childNodes[i].name = childNodes[i].name.replace(/\.n/g, '.');
			childNodes[i].icon="<%=request.getContextPath()%>/icons/icons/basic.gif";
		}
		return childNodes;
	}

 
    function getUrl(treeId, treeNode) {
		if(treeNode != null){
		    var param = "parentId="+treeNode.id;
		    return "<%=request.getContextPath()%>/mx/wx/wxCategory/treeJson/${accountId}?type=${type}&"+param;
		}
		return "<%=request.getContextPath()%>/mx/wx/wxCategory/treeJson/${accountId}?type=${type}";
	}


    function zTreeOnClick(event, treeId, treeNode, clickFlag) {
		jQuery("#nodeId").val(treeNode.id);
		loadData('<%=request.getContextPath()%>/mx/wx/wxCategory/json/${accountId}?parentId='+treeNode.id);
	}

	function loadData(url){
		  jQuery.get(url,{qq:'xx'},function(data){
		      //var text = JSON.stringify(data); 
              //alert(text);
			  jQuery('#mydatagrid').datagrid('loadData', data);
		  },'json');
	  }

    jQuery(document).ready(function(){
			jQuery.fn.zTree.init(jQuery("#myTree"), setting);
	});

	function loadMyData(url){
		  //alert(url);
		  jQuery.get(url,{qq:'xx'},function(data){
		      //var text = JSON.stringify(data); 
              //alert(text);
			  $('#mydatagrid').datagrid('loadData',data);
		  },'json');
	  }

    jQuery(function(){
		jQuery('#mydatagrid').datagrid({
				width:1000,
				height:480,
				fit:true,
				fitColumns:true,
				nowrap: false,
				striped: true,
				collapsible:true,
				url:'<%=request.getContextPath()%>/mx/wx/wxCategory/json/${accountId}',
				remoteSort: false,
				singleSelect:true,
				idField:'id',
				columns:[[
	                {title:'序号', field:'startIndex',width:60,sortable:false},
					{title:'封面图片', field:'coverIcon', align:'center', valign:'middle', width:80, formatter:formatterCover},
					{title:'名称', field:'name', width:120},
					{title:'描述', field:'desc', width:180},
					{title:'顺序', field:'sort', width:60},
					{title:'跳转地址', field:'url', width:280},
					{title:'前台显示', field:'indexShow', width:80, formatter:formatterShow},
					{title:'是否有效', field:'locked', width:80, formatter:formatterStatus},
					{title:'功能键', field:'functionKey', width:120, formatter:formatterKeys}
				]],
				rownumbers:false,
				pagination:true,
				pageSize: <%=com.glaf.core.util.Paging.DEFAULT_PAGE_SIZE%>,
				pageList: [10,15,20,25,30,40,50,100],
				pagePosition: 'both',
				onDblClickRow: onRowClick 
			});

			var p = jQuery('#mydatagrid').datagrid('getPager');
			jQuery(p).pagination({
				onSelectPage:function(pageNumber, pageSize){
					 var nodeId = jQuery("#nodeId").val();
                     var link = '<%=request.getContextPath()%>/mx/wx/wxCategory/json/${accountId}?parentId='+nodeId+'&page='+pageNumber+'&rows='+pageSize;
                     //alert(link);
					 loadMyData(link);
				}
		    });
	});


    function formatterCover(val, row){
		if(row.coverIcon != null && row.coverIcon != ''){
           return "<img src='${contextPath}"+row.coverIcon+"' width='18' height='18' border='0'>";
		}
		return "";
	}

    function formatterShow(val, row){
       if(val == 1){
			return '<span style="color:green; font: bold 13px 宋体;">是</span>';
	   } else  {
			return '<span style="color:red; font: bold 13px 宋体;">否</span>';
	   }  
	}
		 
	function formatterStatus(val, row){
       if(val == 0){
			return '<span style="color:green; font: bold 13px 宋体;">是</span>';
	   } else  {
			return '<span style="color:red; font: bold 13px 宋体;">否</span>';
	   }  
	}

	function formatterKeys(val, row){
		return "<a href='javascript:editRow("+row.id+");'>修改</a>&nbsp;<a href='javascript:editPPT("+row.id+");'>幻灯片</a>&nbsp;";
	}

	function editPPT(rowId){
		var link='<%=request.getContextPath()%>/mx/wx/wxContent/ppt/${accountId}?categoryId='+rowId+'&fromUrl=${fromUrl}';
        //art.dialog.open(link, { height: 420, width: 880, title: "栏目幻灯片", lock: true, scrollbars:"no" }, false);
		location.href=link;
	}


	function editRow(rowId){
	    var link = '<%=request.getContextPath()%>/mx/wx/wxCategory/edit?accountId=${accountId}&type=${type}&fromUrl=${fromUrl}&id='+rowId;
	    //art.dialog.open(link, { height: 420, width: 780, title: "修改记录", lock: true, scrollbars:"no" }, false);
		location.href=link;
	}


	function articleList(categoryId){
        location.href="<%=request.getContextPath()%>/mx/wx/wxContent/articleList/${accountId}?from=category&fromUrl=${fromUrl}&categoryId="+categoryId;
	}


	function addNew(){
		var nodeId = jQuery("#nodeId").val();
		var link = "<%=request.getContextPath()%>/mx/wx/wxCategory/edit?accountId=${accountId}&type=${type}&fromUrl=${fromUrl}&parentId="+nodeId;
	    //art.dialog.open(link, { height: 420, width: 780, title: "添加记录", lock: true, scrollbars:"yes" }, false);
		location.href=link;
	}

	function onRowClick(rowIndex, row){
	    var link = '<%=request.getContextPath()%>/mx/wx/wxCategory/edit?accountId=${accountId}&type=${type}&fromUrl=${fromUrl}&id='+row.id;
	    //art.dialog.open(link, { height: 480, width: 780, title: "修改记录", lock: true, scrollbars:"yes" }, false);
		location.href=link;
	}

	function searchWin(){
	    jQuery('#dlg').dialog('open').dialog('setTitle','栏目查询');
	    //jQuery('#searchForm').form('clear');
	}

	function resize(){
		jQuery('#mydatagrid').datagrid('resize', {
			width:800,
			height:400
		});
	}

	function editSelected(){
	    var rows = jQuery('#mydatagrid').datagrid('getSelections');
	    if(rows == null || rows.length !=1){
		  alert("请选择其中一条记录。");
		  return;
	    }
	    var selected = jQuery('#mydatagrid').datagrid('getSelected');
	    if (selected ){
		  //location.href="<%=request.getContextPath()%>/mx/wx/wxCategory?method=edit&rowId="+selected.id;
		  var link = "<%=request.getContextPath()%>/mx/wx/wxCategory/edit?accountId=${accountId}&type=${type}&fromUrl=${fromUrl}&id="+selected.id;
		  //art.dialog.open(link, { height: 480, width: 780, title: "修改记录", lock: true, scrollbars:"yes" }, false);
		  location.href=link;
	    }
	}

	function listTemplateSettings(){
		var rows = jQuery('#mydatagrid').datagrid('getSelections');
	    if(rows == null || rows.length !=1){
		  alert("请选择其中一条记录。");
		  return;
	    }
	    var selected = jQuery('#mydatagrid').datagrid('getSelected');
	    if (selected ){
		   location.href="${contextPath}/mx/wx/wxTemplate/settings?accountId=${accountId}&type=1&categoryId="+selected.id;
	    }
	}

	function detailTemplateSettings(){
		var rows = jQuery('#mydatagrid').datagrid('getSelections');
	    if(rows == null || rows.length !=1){
		  alert("请选择其中一条记录。");
		  return;
	    }
	    var selected = jQuery('#mydatagrid').datagrid('getSelected');
	    if (selected ){
		    location.href="${contextPath}/mx/wx/wxTemplate/settings?accountId=${accountId}&type=2&categoryId="+selected.id;
	    }
	}

	function viewSelected(){
		var rows = jQuery('#mydatagrid').datagrid('getSelections');
		if(rows == null || rows.length !=1){
			alert("请选择其中一条记录。");
			return;
		}
		var selected = jQuery('#mydatagrid').datagrid('getSelected');
		if (selected ){
		    location.href="<%=request.getContextPath()%>/mx/wx/wxCategory/edit?accountId=${accountId}&type=${type}&id="+selected.id;
		}
	}

	function deleteSelections(){
		var ids = [];
		var rows = jQuery('#mydatagrid').datagrid('getSelections');
		for(var i=0;i<rows.length;i++){
			ids.push(rows[i].id);
		}
		if(ids.length > 0 ){
		  if(confirm("删除栏目会将栏目下的素材及内容全部删除并且不能恢复，确定删除吗？")){
			  if(confirm("删除会导致已经引用了该栏目素材的内容无法显示，确定删除吗？")){
				var rowIds = ids.join(',');
				jQuery.ajax({
					   type: "POST",
					   url: '<%=request.getContextPath()%>/mx/wx/wxCategory/delete?ids='+rowIds,
					   dataType:  'json',
					   error: function(data){
						   alert('服务器处理错误！');
					   },
					   success: function(data){
						   if(data != null && data.message != null){
							   alert(data.message);
						   } else {
							   alert('操作成功完成！');
						   }
						   //jQuery('#mydatagrid').datagrid('reload');
						   window.location.reload();
					   }
				 });
			  }
		  }
		} else {
			alert("请选择至少一条记录。");
		}
	}

	function reloadGrid(){
		var queryParams = $('#mydatagrid').datagrid('options').queryParams; 
		queryParams.parentId=jQuery("#nodeId").val();
		//alert(queryParams.parentId);
	    jQuery('#mydatagrid').datagrid('reload');
	}

	function getSelected(){
	    var selected = jQuery('#mydatagrid').datagrid('getSelected');
	    if (selected){
		  alert(selected.code+":"+selected.name+":"+selected.addr+":"+selected.col4);
	    }
	}

	function getSelections(){
	    var ids = [];
	    var rows = jQuery('#mydatagrid').datagrid('getSelections');
	    for(var i=0;i<rows.length;i++){
		  ids.push(rows[i].code);
	    }
	    alert(ids.join(':'));
	}

	function clearSelections(){
	    jQuery('#mydatagrid').datagrid('clearSelections');
	}

	function searchData(){
        var title = document.getElementById("title").value.trim();
		document.getElementById("nameLike").value = title;
		var params = jQuery("#iForm").formSerialize();
        jQuery.ajax({
                      type: "POST",
                      url: '<%=request.getContextPath()%>/mx/wx/wxCategory/json/${accountId}',
                      dataType:  'json',
                      data: params,
                      error: function(data){
                                alert('服务器处理错误！');
                      },
                      success: function(data){
                                jQuery('#mydatagrid').datagrid('loadData', data);
                      }
                    });
	}
		 
		 
</script>
</head>
<body style="margin:1px;">  
<input type="hidden" id="nodeId" name="nodeId" value="" >
<div class="easyui-layout" data-options="fit:true">   
   	<div data-options="region:'north',split:true,border:true" style="height:40px"> 
		<div class="toolbar-backgroud"  > 
			<img src="<%=request.getContextPath()%>/images/window.png">
			&nbsp;<span class="x_content_title">栏目列表</span>
			<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-add'" 
			   onclick="javascript:addNew();">新增</a>  
			<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-edit'"
			   onclick="javascript:editSelected();">修改</a>  
			<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-remove'"
			   onclick="javascript:deleteSelections();">删除</a> 
			<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-reload'"
			   onclick="javascript:reloadGrid();">重载</a> 
			<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-sys'"
			   onclick="javascript:listTemplateSettings();">列表页模板设置</a>
			<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-sys'"
			   onclick="javascript:detailTemplateSettings();">详细页模板设置</a>
			<input id="title" name="title" type="text" 
	               class="x-searchtext" size="50" maxlength="200"/>
	        <a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-search'"
	           onclick="javascript:searchData();">查找</a>
		</div> 
	</div> 
       
	<div data-options="region:'west',split:true" style="width:195px;">
		<ul id="myTree" class="ztree"></ul>  
    </div>  

	<div data-options="region:'center',border:true">
		<table id="mydatagrid"></table>
	</div>  

</div>
<form id="iForm" name="iForm" method="post">
<input type="hidden" id="nameLike" name="nameLike">
</form> 
</body>
</html>
