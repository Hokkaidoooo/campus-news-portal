/**
 * 下拉栏目导航加载
 * l:level栏目级别
 * c:scope如新闻为A课程为B
 * s1-4:select下拉id名
 * def:当前文章一二三四级(如果有这么多级)的栏目id，用于加载完栏目选中，新建文章时一般为空
 */
function xllmdh(l,c,s1,s2,s3,s4,def){
	var s=null;
	var d=null;
	if(l==1){
		$("#"+s1).html("<option value=''>请选择</option>");
		$("#"+s2).html("<option value=''>请选择</option>");
		$("#"+s3).html("<option value=''>请选择</option>");
		$("#"+s4).html("<option value=''>请选择</option>");
		s=s1;
		d={"level":1,"scope":c};
	}else if(l==2){
		$("#"+s2).html("<option value=''>请选择</option>");
		$("#"+s3).html("<option value=''>请选择</option>");
		$("#"+s4).html("<option value=''>请选择</option>");
		var v=$("#"+s1).val();
		if(v){
			s=s2;
			d={"level":2,"scope":c,"pid":v};
		}
	}else if(l==3){
		$("#"+s3).html("<option value=''>请选择</option>");
		$("#"+s4).html("<option value=''>请选择</option>");
		var v=$("#"+s2).val();
		if(v){
			s=s3;
			d={"level":3,"scope":c,"pid":v};
		}
	}else if(l==4){
		$("#"+s4).html("<option value=''>请选择</option>");
		var v=$("#"+s3).val();
		if(v){
			s=s4;
			d={"level":4,"scope":c,"pid":v};
		}
	}
	if(!d){
		return;
	}
	$.ajax({
		type:"post",
		url:ink.home+"admin/ajax/menu.htm",
		data:d,
		dataType:"json",
		success:function(data){
			var h="<option value=''>请选择</option>";
			var len=data.length;
			if(len&&len>0){
				for(var i=0;i<len;i++){
					h+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
				}
			}
			$("#"+s).html(h);
			if(def){
				pubSelected(s,def);
			}
		}
	});
}