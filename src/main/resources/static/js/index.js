$(document).ready(function () {
    searchProc();
})

function searchProc() {
    $("#SearchWord").keypress(function (event) {
        if(event.which == 13) {
            $("#SearchButton").click();
        }
    })
    $("#SearchButton").click(function () {
        console.log("start search");
        var keyword = $("#SearchWord").val();
        if(keyword=="") {
            window.location.reload();
            return false;
        }
        else {
            window.location.href="doSearch?keyword="+keyword;
            return false;
        }
    })
}