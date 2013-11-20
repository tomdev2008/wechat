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
<%
    String theme = com.glaf.core.util.RequestUtils.getTheme(request);
    request.setAttribute("theme", theme);
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>投票</title>
<link href="<%=request.getContextPath()%>/scripts/artDialog/skins/default.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/scripts/easyui/themes/${theme}/easyui.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/themes/${theme}/styles.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/icons/styles.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/jquery.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/jquery.form.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/artDialog/artDialog.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/artDialog/plugins/iframeTools.js"></script>
<script type="text/javascript">
   var contextPath="<%=request.getContextPath()%>";

   jQuery(function(){
		jQuery('#mydatagrid').datagrid({
				width:1000,
				height:480,
				fit:true,
				fitColumns: true,
				nowrap: false,
				striped: true,
				collapsible: true,
				url: '<%=request.getContextPath()%>/mx/wx/wxVote/json/${accountId}',
				//sortName: 'id',
				//sortOrder: 'desc',
				remoteSort: false,
				singleSelect: true,
				idField: 'id',
				columns:[[
				        {title:'序号', field:'startIndex', width:80, sortable:false},
					    {title:'主题',field:'title', width:220},
					    {title:'是否有效',field:'status', width:90, formatter:formatterStatus},
					    {title:'是否记名',field:'signFlag', width:90, formatter:formatterStatus2},
					    {title:'是否多选',field:'multiFlag', width:90, formatter:formatterStatus2},
					    {title:'是否限制IP',field:'limitFlag', width:120, formatter:formatterStatus2},
					    {title:'限制时间间隔（分钟）',field:'limitTimeInterval', width:150},
					    {title:'开始日期',field:'startDate', width:90},
					    {title:'结束日期',field:'endDate', width:90},
					    {title:'功能键',field:'functionKey',width:120, formatter:formatterKeys}
				]],
				rownumbers: false,
				pagination: true,
				pageSize: 15,
				pageList: [10,15,20,25,30,40,50,100],
				onDblClickRow: onRowClick 
			});

			var p = jQuery('#mydatagrid').datagrid('getPager');
			jQuery(p).pagination({
				onBeforeRefresh:function(){
					//alert('before refresh');
				}
		    });
	});


	function formatterStatus(val, row){
       if(val == 1){
			return '<span style="color:green; font: bold 13px 宋体;">是</span>';
	   } else  {
			return '<span style="color:red; font: bold 13px 宋体;">否</span>';
	   }  
	}


	function formatterStatus2(val, row){
       if(val == 1){
			return '<span style="color:blue; font: bold 13px 宋体;">是</span>';
	   } else  {
			return '<span style="color:blue; font: bold 13px 宋体;">否</span>';
	   }  
	}


	function formatterKeys(val, row){
		return "<a href='#' onclick='javascript:editRow("+row.id+");'>修改</a>&nbsp;<a href='#' onclick='javascript:deleteRow("+row.id+");'>删除</a>&nbsp;<a href='#' onclick='javascript:viewMobileSite("+row.id+");'>预览</a>";
	}

	function editRow(rowId){
            //window.open('<%=request.getContextPath()%>/wx/wxMember/edit?id='+row.id);
	    var link = '<%=request.getContextPath()%>/mx/wx/wxVote/edit?accountId=${accountId}&id='+rowId;
	    art.dialog.open(link, { height: 420, width: 680, title: "修改记录", lock: true, scrollbars:"no" }, false);
	}

    function deleteRow(id){
		if(confirm("数据删除后不能恢复，确定删除吗？")){
			jQuery.ajax({
				   type: "POST",
				   url: '<%=request.getContextPath()%>/mx/wx/wxVote/delete?id='+id,
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
					   jQuery('#mydatagrid').datagrid('reload');
				   }
			 });
		} 
	}

		 
	function addNew(){
	    //location.href="<%=request.getContextPath()%>/apps/wxVote/edit";
	    var link="<%=request.getContextPath()%>/mx/wx/wxVote/edit?accountId=${accountId}";
	    art.dialog.open(link, { height: 420, width: 680, title: "添加记录", lock: true, scrollbars:"no" }, false);
	}

	function onRowClick(rowIndex, row){
            //window.open('<%=request.getContextPath()%>/apps/wxVote/edit?id='+row.id);
	    var link = '<%=request.getContextPath()%>/mx/wx/wxVote/edit?accountId=${accountId}&id='+row.id;
	    art.dialog.open(link, { height: 420, width: 680, title: "修改记录", lock: true, scrollbars:"no" }, false);
	}

	function searchWin(){
	    jQuery('#dlg').dialog('open').dialog('setTitle','投票查询');
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
		//location.href="<%=request.getContextPath()%>/apps/wxVote/edit?id="+selected.id;
		var link = "<%=request.getContextPath()%>/mx/wx/wxVote/edit?accountId=${accountId}&id="+selected.id;
		art.dialog.open(link, { height: 420, width: 680, title: "修改记录", lock: true, scrollbars:"no" }, false);
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
		    location.href="<%=request.getContextPath()%>/mx/wx/wxVote/edit?accountId=${accountId}&readonly=true&id="+selected.id;
		}
	}

	function deleteSelections(){
		var ids = [];
		var rows = jQuery('#mydatagrid').datagrid('getSelections');
		for(var i=0;i<rows.length;i++){
			ids.push(rows[i].id);
		}
		if(ids.length > 0 && confirm("数据删除后不能恢复，确定删除吗？")){
		    var str = ids.join(',');
			jQuery.ajax({
				   type: "POST",
				   url: '<%=request.getContextPath()%>/mx/wx/wxVote/delete?ids='+str,
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
					   jQuery('#mydatagrid').datagrid('reload');
				   }
			 });
		} else {
			alert("请选择至少一条记录。");
		}
	}

	function reloadGrid(){
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

	function loadGridData(url){
            jQuery.post(url,{qq:'xx'},function(data){
            //var text = JSON.stringify(data); 
            //alert(text);
            jQuery('#mydatagrid').datagrid('loadData', data);
            },'json');
	  }

	function searchData(){
            var params = jQuery("#searchForm").formSerialize();
            jQuery.ajax({
                        type: "POST",
                        url: '<%=request.getContextPath()%>/mx/wx/wxVote/json/${accountId}',
                        dataType:  'json',
                        data: params,
                        error: function(data){
                                  alert('服务器处理错误！');
                        },
                        success: function(data){
                                  jQuery('#mydatagrid').datagrid('loadData', data);
                        }
                        });

	    jQuery('#dlg').dialog('close');
	}

	function viewMobileSite(id){		 
		var link = '<%=request.getContextPath()%>/website/wx/vote/vote/'+id;
		//art.dialog.open(link, { height: 720, width: 400, title: "预览效果", lock: true, scrollbars:"no" }, false);
		var x=200;
		var y=150;
		var fx = "height=520,width=400,status=0,toolbar=no,menubar=no,location=no,scrollbars=yes,top="+y+",left="+x+",resizable=no,modal=yes,dependent=yes,dialog=yes,minimizable=no";
		if(jQuery.browser.msie){
			window.open(link,  "预览效果", fx);
		} else {
			window.open(link, self, fx, true);
		}
	}
	
	function viewSite(){
		var rows = jQuery('#mydatagrid').datagrid('getSelections');
	    if(rows == null || rows.length !=1){
		  alert("请选择其中一条记录。");
		  return;
	    }
		var selected = jQuery('#mydatagrid').datagrid('getSelected');
	    if (selected){
			var link = '<%=request.getContextPath()%>/website/wx/vote/vote/'+selected.id;
			//art.dialog.open(link, { height: 720, width: 400, title: "预览效果", lock: true, scrollbars:"no" }, false);
			var x=200;
			var y=150;
			var fx = "height=520,width=400,status=0,toolbar=no,menubar=no,location=no,scrollbars=yes,top="+y+",left="+x+",resizable=no,modal=yes,dependent=yes,dialog=yes,minimizable=no";
			if(jQuery.browser.msie){
				window.open(link,  "预览效果", fx);
			} else {
				window.open(link, self, fx, true);
			}
		}
	}
	
</script>
</head>
<body style="margin:1px;">  
<div style="margin:0;"></div>  
<div class="easyui-layout" data-options="fit:true">  
   <div data-options="region:'north',split:true,border:true" style="height:40px"> 
    <div class="toolbar-backgroud"  > 
	<img src="<%=request.getContextPath()%>/images/window.png">
	&nbsp;<span class="x_content_title">投票列表</span>
    <a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-add'" 
	   onclick="javascript:addNew();">新增</a>  
    <a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-edit'"
	   onclick="javascript:editSelected();">修改</a>  
	<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-remove'"
	   onclick="javascript:deleteSelections();">删除</a> 
	<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-search'"
	   onclick="javascript:searchWin();">查找</a>
	<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'preview'"
	   onclick="javascript:viewSite();">预览</a>
   </div> 
  </div> 
  <div data-options="region:'center',border:true">
	 <table id="mydatagrid"></table>
  </div>  
</div>
<div id="edit_dlg" class="easyui-dialog" style="width:400px;height:280px;padding:10px 20px"
	closed="true" buttons="#dlg-buttons">
    <form id="editForm" name="editForm" method="post">
         
    </form>
</div>
<div id="dlg" class="easyui-dialog" style="width:400px;height:280px;padding:10px 20px"
	closed="true" buttons="#dlg-buttons">
  <form id="searchForm" name="searchForm" method="post">
  <table class="easyui-form" >
    <tbody>
    <tr>
	<td>主题</td>
	<td>
        <input id="titleLike" name="titleLike" class="easyui-validatebox" type="text"></input>
       </td>
     </tr>
    <tr>
	<td>内容</td>
	<td>
        <input id="contentLike" name="contentLike" class="easyui-validatebox" type="text"></input>
       </td>
     </tr>
    <tr>
	<td>主题图片</td>
	<td>
        <input id="iconLike" name="iconLike" class="easyui-validatebox" type="text"></input>
       </td>
     </tr>
    <tr>
	<td>状态</td>
	<td>
	<input id="status" name="status" class="easyui-numberbox" precision="0" ></input>
       </td>
     </tr>
    <tr>
	<td>是否记名</td>
	<td>
	<input id="signFlag" name="signFlag" class="easyui-numberbox" precision="0" ></input>
       </td>
     </tr>
    <tr>
	<td>是否多选</td>
	<td>
	<input id="multiFlag" name="multiFlag" class="easyui-numberbox" precision="0" ></input>
       </td>
     </tr>
    <tr>
	<td>是否限制IP</td>
	<td>
	<input id="limitFlag" name="limitFlag" class="easyui-numberbox" precision="0" ></input>
       </td>
     </tr>
    <tr>
	<td>限制时间间隔</td>
	<td>
	<input id="limitTimeInterval" name="limitTimeInterval" class="easyui-numberbox" precision="0" ></input>
       </td>
     </tr>
    <tr>
	<td>开始日期</td>
	<td>
	<input id="startDateLessThanOrEqual" name="startDateLessThanOrEqual" class="easyui-datebox"></input>
       </td>
     </tr>
    <tr>
	<td>结束日期</td>
	<td>
	<input id="endDateLessThanOrEqual" name="endDateLessThanOrEqual" class="easyui-datebox"></input>
       </td>
     </tr>
    <tr>
	<td>创建人</td>
	<td>
        <input id="createByLike" name="createByLike" class="easyui-validatebox" type="text"></input>
       </td>
     </tr>
    <tr>
	<td>创建日期</td>
	<td>
	<input id="createDateLessThanOrEqual" name="createDateLessThanOrEqual" class="easyui-datebox"></input>
       </td>
     </tr>
      </tbody>
    </table>
  </form>
</div>
<div id="dlg-buttons">
	<a href="#" class="easyui-linkbutton" iconCls="icon-ok" onclick="javascript:searchData()">查询</a>
	<a href="#" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:jQuery('#dlg').dialog('close')">取消</a>
</div>
</body>
</html>
