//18.06.12 by zgy
//需要调用js的效果有：选项卡、无限级下拉菜单

$(document).ready(function(){
	$(".nav_tab_title , .nav_tab_title_this ").click(function(){
		$(this).siblings('li').attr('class','nav_tab_title');
		$(this).attr('class','nav_tab_title_this');
		//alert($(this).index()); 选项卡序号编号从0开始
		$(this).parent().siblings('div').attr('class','nav_tab_content');
		$(this).parent().siblings('div').eq($(this).index()).attr('class','nav_tab_content_this');
	});

});

function nav_menu(ulclass){
	$(document).ready(function(){
		$(ulclass+' li').hover(function(){
			$(this).children("ul").show(); //mouseover
		},function(){
			$(this).children("ul").hide(); //mouseout
		});
	});
}
