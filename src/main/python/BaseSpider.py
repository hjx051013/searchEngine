import re
import requests
import threadpool
from bs4 import BeautifulSoup
from Common import DB, DbParams, start_url, db_rwlock

'''
爬虫模块，根据起始url爬取广度该网站下的所有url,存url，html文本，页内超链接到数据库中
'''
visited_dict = {}
visit_list = []
visit_list.append(start_url)
headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36'}

def rm_tags(html):
    re_script = re.compile('<\s*script[^>]*>[^<]*<\s*/\s*script\s*>', re.I)  # Script
    re_style = re.compile('<\s*style[^>]*>[^<]*<\s*/\s*style\s*>', re.I)  # style
    re_comment = re.compile('<!--.*-->')  # HTML注释
    s = re_script.sub("", html)
    s = re_style.sub("", s)
    s = re_comment.sub("", s)
    reg_all = re.compile('<[^>]*>', re.I)
    s = reg_all.sub("", s)
    return s


def getHtml(url):
    '''
    获取指定url的页面
    :param url: 指定的url链接
    :return:
    '''
    try:
        # print(url)
        response = requests.get(url, headers=headers, timeout=20)
        if response.encoding == None:
            return None
        else:
            return response.content.decode(response.encoding)
    except Exception as e:
        print(repr(e))
        return None


def getHrefOfHtml(html):
    '''
    从html网页文本中提取超链接
    :param html: 指定的html文本
    :return:
    '''
    re_href = re.compile('<[Aa][^>]*?href=.*?>')
    try:
        href_list = [BeautifulSoup(x, "html.parser").a['href'] for x in re_href.findall(html)]
    except Exception as e:
        print(repr(e))
    for href in href_list:
        yield href


def getPath(url):
    if re.search("[^/]+/([^/]+|$)", url) != None:
        return url[0:url.rindex('/')]
    else:
        return url


def saveHtmlToDb(db, url, html, pointingTo):
    db.executeUpdateSql("INSERT INTO url_tb(url,content,pointing) VALUES (%s,%s,%s)", (url, html, pointingTo))


def get_url_tb_size(db):
    res = db.executeSelectSql("SELECT COUNT(*) FROM url_tb", ())
    return res[0][0]


def get_url_tb_ids(db):
    res = db.executeSelectSql("SELECT id FROM url_tb ORDER BY id", ())
    #     print("res",res)
    return [tup[0] for tup in res] if res else []
'''
#用于从网页正文中提取超链接
def parseHrefFromHtml(thispage,html):
    pointingTo = []
    for href in getHrefOfHtml(html):
        href = href.strip()
#         print(href)
        re_http = re.compile("^\s*http[s]{0,1}:")
        try:
            if re_http.search(href)!=None:
                #说明带有http或https
                if  href.startswith(start_url):
                    pointingTo.append(href)        
            else:
#                 print("yes")
                if href=="/#" or href=="#" or len(href)==0:
                    continue
                if href.startswith("/"):
                    #相对根目录
                    href = start_url+href
                else:
                    #相对当前页目录
                    href = getPath(thispage)+"/"+href
#                 print(href)
                pointingTo.append(href)              
        except Exception as e:
            print(repr(e))
#     print(len(pointingTo))
    return pointingTo


def processPointing(db):
    #在数据库已经存有html文本时从html文本中提取超链接存入pointingTo字段
    id_list = get_url_tb_ids()
    #     print(id_list)
    for id in id_list:
        if id >= 500:
            res_content = db.executeSelectSql("SELECT content from url_tb WHERE id=%s", (id,))
            res_url = db.executeSelectSql("SELECT url from url_tb WHERE id=%s", (id,))
            if res_content and res_content[0] and res_content[0][0] and res_url and res_url[0] and res_url[0][0]:
                url_list = parseHrefFromHtml(res_url[0][0], res_content[0][0])
                url_num = []
                for url in url_list:
                    # 对于链接中的每一个
                    #                 print(url)
                    url_id_res = db.executeSelectSql("SELECT id FROM url_tb WHERE url=%s", (url,))
                    #                 print(url_id_res)
                    if url_id_res and url_id_res[0] and url_id_res[0][0]:
                        url_id = url_id_res[0][0]
                        url_num.append(str(url_id))
                new_pointing = ','.join(url_num)
                print(new_pointing)
                db.executeUpdateSql("UPDATE url_tb SET pointing=%s WHERE id=%s", (new_pointing, id))

'''


def dealPage(db, url_node):
    '''
    获取一个单页面上的网页正文，并获取所有链接，存到数据库中，放到队列中
    '''
    global visit_list
    global visited_dict
    # print(url_node)
    html = getHtml(url_node)
    # print(url_node)
    if html == None:
        return
    pointingTo = []
    re_http = re.compile("^\s*http[s]{0,1}:")
    for href in getHrefOfHtml(html):
        '''
        对于页面中的每一个超链接
        '''
        href = href.strip()
        try:
            if re_http.search(href) != None:
                # 说明此链接带有http或https
                if href.startswith(start_url):
                    pointingTo.append(href)
                    if visited_dict.get(href) == None:
                        # 如果html没有加入过队列并且是以start_url开头的
                        visited_dict[href] = True
                        visit_list.append(href)

            else:
                if href == "/#" or href == "#" or len(href) == 0:
                    continue
                if href.startswith("/"):
                    # 相对根目录
                    href = start_url + href
                else:
                    # 相对当前页目录
                    href = getPath(url_node) + "/" + href
                pointingTo.append(href)
                if visited_dict.get(href) == None:
                    # start_url下的下的子链接
                    visited_dict[href] = True
                    visit_list.append(href)

        except Exception as e:
            print(repr(e))
    pointingTo = ",".join(pointingTo)
    # print(pointingTo)
    db_rwlock.writer_lock.acquire()
    saveHtmlToDb(db, url_node, html, pointingTo)
    db_rwlock.writer_lock.release()


if __name__ == "__main__":
    db = DB(DbParams["ip"], DbParams["user"], DbParams["password"], DbParams["db_name"], charset=DbParams["charset"])
    pool = threadpool.ThreadPool(num_workers=3)  # 建立一个拥有十个线程的线程池
    count = 0
    while len(visit_list) != 0:
        # 获得当前队列中的所有url链接，对它们的处理交给线程池多线程处理
        #         url_node = visit_list.pop()
        # print("ok")
        temp_url_list = visit_list.copy()
        visit_list.clear()
        args = [((db, url), {}) for url in temp_url_list]
        #         print(args)
        reqs = threadpool.makeRequests(dealPage, args)
        [pool.putRequest(req) for req in reqs]
        pool.wait()  # 等待将该层页面全部爬完
        visit_list = list(set(visit_list))  # 获得的新的visit_list去重
        print(visit_list)
        print("完成第一层搜索")
    print("爬虫完毕")
