## 这是一个小型网站的通用搜索引擎

1. 效果参考网站: [软院搜索](http://47.100.50.2:8080/searchEngine)

2. 如何配置
    
    - 配置存储数据的ip及密码等参数，在ruanjian.sql中默认建立名为"ruanjian"的数据库
    ![Xnip2019-03-24_00-08-57.png](http://hjx-markdown-images.test.upcdn.net/2019/03/24/04b3cc736db64b68a8ace096d0921e3a.png)
    - `cd ./src/main/python`,`bash search.sh`即可自动建立数据库，
    并且爬取相应网站页面及处理数据
    - 在Intellij-idea中打开这个项目，下载maven依赖包
    - 对前端的mysql数据库及redis数据库进行配置
    ![Xnip2019-03-24_00-12-57.png](http://hjx-markdown-images.test.upcdn.net/2019/03/24/b9803816a553c159e2931c8a0d73232a.png)
