import threadpool
from bs4 import BeautifulSoup
import re
from Common import DB
from Common import DbParams, db_lock, start_url


'''
将数据库pointing字段解析成为url id连成的字符串
'''


def getPath(url):
    if re.search("[^/]+/([^/]+|$)", url) is not None:
        return url[0:url.rindex('/')]
    else:
        return url


def get_url_tb_ids(db):
    res = db.executeSelectSql("SELECT id FROM url_tb ORDER BY id WHERE id>6188", ())
    #     print("res",res)
    return [tup[0] for tup in res] if res else []


def getHrefOfHtml(html):
    re_href = re.compile('<[Aa][^>]*?href=.*?>')
    try:
        href_list = [BeautifulSoup(x, "html.parser").a['href'] for x in re_href.findall(html)]
    except Exception as e:
        print(repr(e))
    for href in href_list:
        yield href


# 用于从网页正文中提取超链接
def parseHrefFromHtml(thispage, html):
    pointingTo = []
    for href in getHrefOfHtml(html):
        href = href.strip()
        #         print(href)
        re_http = re.compile("^\s*http[s]{0,1}:")
        try:
            if re_http.search(href) is not None:
                # 说明带有http或https
                if href.startswith(start_url):
                    pointingTo.append(href)
            else:
                #                 print("yes")
                if href == "/#" or href == "#" or len(href) == 0:
                    continue
                if href.startswith("/"):
                    # 相对根目录
                    href = start_url + href
                else:
                    # 相对当前页目录
                    href = getPath(thispage) + "/" + href
                #                 print(href)
                pointingTo.append(href)
        except Exception as e:
            print(repr(e))
    #     print(len(pointingTo))
    return pointingTo


def parseOnePageHtml(db, id):
    print("%s" % (id))
    db_lock.acquire()
    res_content = db.executeSelectSql("SELECT content from url_tb WHERE id=%s", (id,))
    res_url = db.executeSelectSql("SELECT url from url_tb WHERE id=%s", (id,))
    db_lock.release()
    url_num = []
    # print(res_content)
    if res_content and res_content[0] and res_content[0][0] and res_url and res_url[0] and res_url[0][0]:
        url_list = parseHrefFromHtml(res_url[0][0], res_content[0][0])
        print(url_list)
        for url in url_list:
            # 对于链接中的每一个
            # print(url)
            db_lock.acquire()
            url_id_res = db.executeSelectSql("SELECT id FROM url_tb WHERE url=%s", (url,))
            db_lock.release()
            #                 print(url_id_res)
            if url_id_res and url_id_res[0] and url_id_res[0][0]:
                url_id = url_id_res[0][0]
                url_num.append(str(url_id))

    new_pointing = ','.join(url_num)
    print(new_pointing)
    db_lock.acquire()
    db.executeUpdateSql("UPDATE url_tb SET pointing=%s WHERE id=%s", (new_pointing, id))
    db_lock.release()


def processPointing(db):
    id_list = get_url_tb_ids(db)
    #     print(id_list)
    pool = threadpool.ThreadPool(num_workers=5)  # 建立一个拥有十个线程的线程池
    args = [((db, x), {}) for x in id_list]
    reqs = threadpool.makeRequests(parseOnePageHtml, args)
    [pool.putRequest(req) for req in reqs]
    pool.wait()
    print("完成解析")


if __name__ == "__main__":
    db = DB(DbParams["ip"], DbParams["user"], DbParams["password"], DbParams["db_name"], charset=DbParams["charset"])
    processPointing(db)
