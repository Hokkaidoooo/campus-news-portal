//公共js函数，放在left中全局使用

/**
 * 链接跳转
 * @param url
 */
function pubLinkJump(url){
    if(!url){
        return;
    }
    window.location=url;
}

/**
 * 下拉选中
 * @param id select的id
 * @param val 将被选中的option的id
 */
function pubSelected(id,val){
    var _option=$("#"+id+" option");
    var _i=0;
    for(;_i<_option.length;_i++){
        if(_option[_i].value==val){
            break;
        }
    }
    _option[_i<_option.length?_i:0].selected=true;
}