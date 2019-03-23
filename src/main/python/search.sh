#!/bin/bash
if test $# = 1
then
	    mysql -u root -p -h $1 < ruanjian.sql
        sleep 1
        echo "数据库建表完毕"
        version=$(python --version | grep '3')
        if [[ $version != "" ]]
        then
                echo "下载相关python包"
                pip install -r requirements.txt
                echo "开始爬取网页"
                python BaseSpider.py
                echo "将数据库pointing字段解析成为url id连成的字符串"
                python Parser.py
                echo "计算每个网页的pagerank"
                python PageRank.py
                echo "分词并建立倒排索引"
                python CutWord.py
                echo "执行python程序完毕"
        else
                echo "不是python3"
        fi
else
        echo "用法：bash $0 [数据库ip]";
fi


