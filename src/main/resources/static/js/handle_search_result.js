/**
 * Created by cz on 17-6-19.
 */
var globalVariables = {
    "numberOfPage": 10,//每页查询数量
    "pageIndex": 0,//当前页index
    "curQuery": ""//当前请求
}
var _ctx = $("meta[name='ctx']").attr("content");

$(document).ready(function () {

    search();
    // detail();//从主页跳转该页面
    pageClick();//处理首页，上一页，下一页的点击
    var keyword = getQueryString("keyword");
    globalVariables.curQuery = keyword;
    $("#search").val(keyword);
});


function pageClick() {
    $("#first-page").click(fisrtPage);
    $("#last-page").click(lastPage);
    $("#next-page").click(nextPage);
}

function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    return r?decodeURIComponent(r[2]):null;
}

// function detail() {
//     //首次加载该页面时，发送请求
//     keyword = getQueryString("keyword");
//     if(keyword!=null)
//         request(keyword,globalVariables.numberOfPage*globalVariables.pageIndex,globalVariables.numberOfPage,process_result);
//     else {
//         $("#searchResult").empty();
//         $("#loading").html("查询结果为空");
//     }
// }
function search() {
    $('#search').keypress(function (event) {
        if (event.which == 13) {//如果直接按下enter
            $('#searchButton').click();
        }
    });
    $('#searchButton').click(function () {
        console.log("click press");
        resetGlobalVals();
        var keyword = $("#search").val();

        request(keyword,globalVariables.numberOfPage*globalVariables.pageIndex,globalVariables.numberOfPage,process_result);
    });
}

function fisrtPage() {
    if(globalVariables.pageIndex > 0) {
        globalVariables.pageIndex = 0;
        request(globalVariables.curQuery,0,globalVariables.numberOfPage,process_result);
    }
}

function lastPage() {
    if(globalVariables.pageIndex >= 1) {
        globalVariables.pageIndex -= 1;
        request(globalVariables.curQuery,globalVariables.pageIndex*globalVariables.numberOfPage,globalVariables.numberOfPage,process_result);
    }
}

function nextPage() {
    if($("#searchResult").children().length==10) {
        //说明当前页有数据,则可以继续翻到下一页
        globalVariables.pageIndex += 1;
        request(globalVariables.curQuery,globalVariables.pageIndex*globalVariables.numberOfPage,globalVariables.numberOfPage,process_result);
    }
}
function resetGlobalVals() {
    globalVariables.pageIndex = 0;
    globalVariables.numberOfPage = 10;
    globalVariables.curQuery = ""
}

function changeURLPar(destiny, par, par_value) {
    var pattern = par + '=([^&]*)';
    var replaceText = par + '=' + par_value;
    if (destiny.match(pattern)) {
        var tmp = '/\\' + par + '=[^&]*/';
        tmp = destiny.replace(eval(tmp), replaceText);
        return (tmp);
    }
    else {
        if (destiny.match('[\?]')) {
            return destiny + '&' + replaceText;
        }
        else {
            return destiny + '?' + replaceText;
        }
    }
}

function process_result(data) {

    if(data==null || data["data"].length == 0) {
        $("#context").hide();
        $("#loading").show();
        $("#loading").html("查询结果为空");
    }
    else {
        var realData = data["data"];
        $("#searchResult").empty();//每次获得新数据将原有数据清空
        ht = "";
        for(var i = 0; i < realData.length; i++) {
            ht += '<ul class="search-results-'+(i+1)+
                '"><li class="main-link"><a style="cursor: pointer;" target="_blank" href="'+realData[i].link+'"><span>'+
                realData[i].title+'</span></a></li><li class="second-link"><a style="cursor: pointer;" target="_blank" href="'
                +realData[i].link+'"><span>'+
                realData[i].link+'</span></a></li><li class="description"><span>'+
                realData[i].discription+'</span></li></ul>';
        }
        $("#searchResult").append(ht);
        $("#loading").hide();
        $("#context").show();
        $("#pageSetting").show();
        $("#curPage").text(""+(globalVariables.pageIndex+1));
    }
}

function request(keyword,start,num,succeed_callback) {
    if(keyword=="") {
        window.location.href = _ctx+"/index";
        return false;
    }
    else {
        if(keyword.length > 20) {
            keyword = keyword.substr(0,20);
        }
        var params = {
            "keyword":keyword,
            "start":start,
            "num":globalVariables.numberOfPage
        }
        globalVariables.curQuery = keyword;
        newUrl = changeURLPar(window.location.href,"keyword",globalVariables.curQuery);
        history.pushState(null,null,newUrl);
        $.ajax({
            url: _ctx+"/search",
            type: 'get',
            data: params,
            dataType: 'json',
            success:function (data) {
                succeed_callback(data);
            },
            error:function () {//显示网络出错信息
                $("#context").hide();
                $("#loading").show();
                $("#loading").html("网络出错！");
            }
        })
    }

}




