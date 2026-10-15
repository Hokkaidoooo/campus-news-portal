/**
 * 公共分页函数
 * 删除、编辑、顶部搜索条件搜索、底部分页 共同使用
 * @type {Object}
 */
var page=new Object();
page.starttime=null;//开始时间
page.endtime=null;//结束时间
page.now=1;//当前页数，页面加载时调用fysz()赋值
page.size=5;//每页多少条
page.order=true; //排序方式
page.sort=null;//升降序
page.scolumn=null;//搜索列，和关键词结合使用
page.sname=null;//关键词
page.drop=null;//页面加载时调用fysz()赋值 servlets/news/drop.htm
page.edit=null;//页面加载时调用fysz()赋值 servlets/news/modify.htm
page.list=null;//页面加载时调用fysz()赋值 servlets/news/list.htm

//分页参数设置 当前页以及增删改相对链接，页面加载时传递
function fysz(now,drop,edit,list){
    page.now=now;
    page.drop=drop;
    page.edit=edit;
    page.list=list;
}

//分页参数获取(浏览器get请求方式参数拼接)
function fycs(){
    page.starttime=$("#starttime").val();
    page.endtime=$("#endtime").val();
    page.size=parseInt($("#fy_pagesize").val());//获取页面每页多少条的下拉值
    page.order=$("#fy_order").val();
    page.sort=$("#fy_sort").val();
    page.scolumn=$("#fy_column").val();
    page.sname=$("#fy_name").val();
    page.x_name1=$("#x_name1").val();
    page.x_name2=$("#x_name2").val();
    page.x_name3=$("#x_name3").val();
    var res="";
    if(page.starttime){
        res+="starttime="+encodeURIComponent(page.starttime)+"&";
    }
    if(page.endtime){
        res+="endtime="+encodeURIComponent(page.endtime)+"&";
    }
    if(page.sname){
        res+="x_name="+encodeURIComponent(page.sname)+"&";
    }
    if(page.x_name1){
        res+="x_name1="+encodeURIComponent(page.x_name1)+"&";
    }
    if(page.x_name2){
        res+="x_name2="+encodeURIComponent(page.x_name2)+"&";
    }
    if(page.x_name3){
        res+="x_name3="+encodeURIComponent(page.x_name3)+"&";
    }
    res+="x_now="+page.now+"&x_size="+page.size+"&x_order="+page.order+"&x_sort="+page.sort;
    return res;
}

//分页加载
function fyjz(now){
    if(now){
        page.now=now;
    }
    pubLinkJump(ink.home+page.list+"?"+fycs());
}

//分页回车，关键词搜索框回车 和 搜索按钮点击触发
function fyhc(e){
    if(e){
        e=e||window.event;
        if((e.keyCode?e.keyCode:(e.which?e.which:e.charCode))==13){
            fyjz(1);
        }
    }else{
        fyjz(1);
    }
}

//分页跳页，跳转到指定页数
function fyty(){
    var val=$("#fy_now").val().replace(/\D/g,"");
    var now=parseInt(val);
    if(!now){
        $("#fy_now").val("");
        return;
    }
    $("#fy_now").val(val);
    fyjz(now);
}

//分页删除(单个)
function fysc(id){
    if(!id||!window.confirm("确认删除吗")){
        return;
    }
    pubLinkJump(ink.home+page.drop+"?id="+encodeURIComponent(id)+"&"+fycs());
}

//分页删除(多个)
function fyscs(){
    var ids="";
    var objs=$(".table tbody td input:checkbox:checked");
    for(var i=0;i<objs.length;i++){
        ids+=objs[i].value+"_";
    }
    if(ids.length>0){
        ids=ids.substring(0,ids.length-1);
    }
    fysc(ids);
}

//分页编辑
function fybj(id){
    if(!id){
        return;
    }
    pubLinkJump(ink.home+page.edit+"?id="+encodeURIComponent(id)+"&"+fycs());
}
