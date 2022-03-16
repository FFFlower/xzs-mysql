<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8"/>
    <title></title>
    <link rel="stylesheet" href="/index/css/bootstrap.min.css">
    <script type="text/javascript" src="/index/js/jquery.min.js"></script>
    <script type="text/javascript" src="/index/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/index/js/axios.min.js"></script>
</head>
<body>
<div class="container-fluid">
    <div class="row" style="margin-top: 50px">
        <div class="col-lg-6 col-lg-offset-3">
            <div class="input-group input-group-lg">
                <input type="text" id="questionTitle" value="${title!}" class="form-control" placeholder="输入问题...">
                <span class="input-group-btn">
                    <button class="btn btn-primary" onclick="search()" type="button">搜索</button>
                </span>
            </div>
        </div>
    </div>
    <div class="row" id="app" style="margin-top: 20px">
        <#if pageInfo.pageCount gt 0>
            <#list pageInfo.recordList as value>
                <div class="col-lg-6 col-lg-offset-3">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h3 class="panel-title">${value.questionTypeName}</h3>
                        </div>
                        <div class="panel-body">
                            <h5>${value.title}</h5>
                            <#list value.items as item>
                                <div>${item.prefix}.${item.content}</div>
                            </#list>
                        </div>
                        <div class="panel-footer">
                            正确答案:${value.correct}
                        </div>
                    </div>
                </div>
            </#list>
            <div class="col-lg-6 col-lg-offset-3">
                <nav aria-label="Page navigation">
                    <ul class="pagination">
                        <li>
                            <a onclick="prePage()" aria-label="Previous">
                                <span aria-hidden="true">&laquo;</span>
                            </a>
                        </li>
                        <#list 1..pageInfo.pageCount as i>
                            <#if (i - 1) == pageInfo.currentPage>
                                <li class="active"><a href="#">${i}</a></li>
                            <#else>
                                <li><a onclick="goPage(${i-1})">${i}</a></li>
                            </#if>
                        </#list>
                        <li>
                            <a onclick="nextPage()" aria-label="Next">
                                <span aria-hidden="true">&raquo;</span>
                            </a>
                        </li>
                    </ul>
                </nav>
            </div>
        <#else>
            <div class="col-lg-6 col-lg-offset-3">
                <div>没有找到相关内容</div>
            </div>
        </#if>
    </div>
</div>
</body>
<script type="text/javascript" src="/index/js/enumItems.js"></script>
<script>
    var pageData = {}

    var searchForm = {
        pageIndex: 0,
        pageSize: 10
    }

    function search() {
       searchForm.pageIndex = 0
       page();
    }

    function page(){
        searchForm['title'] = $('#questionTitle').val();
        window.location.href = "/index?title=" + searchForm.title + "&pageIndex=" + searchForm.pageIndex + "&pageSize=" + searchForm.pageSize;
    }

    function prePage() {
        if(searchForm.pageIndex <= 0){
            searchForm.pageIndex = 0;
            return;
        }
        searchForm.pageIndex = searchForm.pageIndex - 1;
        page();
    }

    function goPage(pageIndex){
        searchForm.pageIndex = pageIndex;
        page();
    }

    function nextPage() {
        if(searchForm.pageIndex >= pageData.pageCount - 1){
            searchForm.pageIndex = pageData.pageCount - 1;
            return;
        }
        searchForm.pageIndex = searchForm.pageIndex + 1
        page();
    }

    $('#questionTitle').bind('keyup', function(event) {
    　　if (event.keyCode == "13") {
    　　　　search();
    　　}
    });
</script>
</html>