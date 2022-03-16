<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8"/>
    <title></title>
    <link rel="stylesheet" href="/index/css/bootstrap.min.css">
    <script type="text/javascript" src="/index/js/jquery.min.js"></script>
    <script type="text/javascript" src="/index/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/index/js/template-web.js"></script>
    <script type="text/javascript" src="/index/js/axios.min.js"></script>
</head>
<body>
<div class="container-fluid">
    <div class="row" style="margin-top: 50px">
        <div class="col-lg-6 col-lg-offset-3">
            <div class="input-group input-group-lg">
                <input type="text" id="questionTitle" class="form-control" placeholder="输入问题...">
                <span class="input-group-btn">
                    <button class="btn btn-primary" onclick="search()" type="button">搜索</button>
                </span>
            </div>
        </div>
    </div>
    <div class="row" id="app" style="margin-top: 20px">
    </div>
</div>
</body>
<script id="list" type="text/html">
    {{each recordList as value}}
    <div class="col-lg-6 col-lg-offset-3">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">{{ value.questionType | enumItemFormatter }}</h3>
            </div>
            <div class="panel-body">
                <h5>{{@ value.title }}</h5>
                {{each value.items as item}}
                <div>{{item.prefix}}.{{item.content}}</div>
                {{/each}}
            </div>
            <div class="panel-footer">
                正确答案:{{ value.correct }}
            </div>
        </div>
    </div>
    {{/each}}
    <div class="col-lg-6 col-lg-offset-3">
        <nav aria-label="Page navigation">
            <ul class="pagination">
                <li>
                    <a onclick="prePage()" aria-label="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>
                <% for(var i = 0; i < pageCount; i++){ %>
                    {{if i == currentPage}}
                    <li class="active"><a href="#"><%= i+1%></a></li>
                    {{else}}
                    <li><a onclick="goPage({{i}})"><%= i+1%></a></li>
                    {{/if}}
                <% } %>
                <li>
                    <a onclick="nextPage()" aria-label="Next">
                        <span aria-hidden="true">&raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>
    </div>
</script>
<script id="noData" type="text/html">
    <div class="col-lg-6 col-lg-offset-3">
        <div>没有找到相关内容</div>
    </div>
</script>
<script type="text/javascript" src="/index/js/enumItems.js"></script>
<script>
    var pageData = {}

    var searchForm = {
        pageIndex: 0,
        pageSize: 10
    }

    template.defaults.imports.enumItemFormatter = function (key) {
      for (var item of state.exam.question.typeEnum) {
        if (item.key === key) {
          return item.value
        }
      }
      return null
    }
    function search() {
       searchForm.pageIndex = 0
       page();
    }

    function page(){
        searchForm['title'] = $('#questionTitle').val()
        axios.post('/api/ftl/student/question/page', searchForm)
        .then(function (response) {
            pageData = response.data.response
            if (pageData.recordCount == 0){
                var html = template('noData',pageData)
            }else{
                var html = template('list', pageData);
            }
            document.getElementById('app').innerHTML = html;
        })
        .catch(function (error) {
          console.log(error);
        });
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