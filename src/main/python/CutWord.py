import re
import jieba
import threadpool
from Common import DB, DbParams, db_lock


'''
从html文本中解析中文，分词并建立倒排索引
'''

def rm_tags(html):
    re_script = re.compile('<\s*script[^>]*>[^<]*<\s*/\s*script\s*>', re.I)  # Script
    re_style = re.compile('<\s*style[^>]*>[^<]*<\s*/\s*style\s*>', re.I)  # style
    re_comment = re.compile('<!--.*-->')  # HTML注释
    s = re_script.sub("", html)
    s = re_style.sub("", s)
    s = re_comment.sub("", s)
    reg_all = re.compile('<[^>]*>', re.I)  # 所有Tag标签
    s = reg_all.sub("", s)
    re_blank = re.compile('\s+', re.I)  # 去除空格
    re_mark = re.compile('[^\w\s]')  # 去除非文字非空格
    re_letter = re.compile('[a-z]', re.I)  # 去除英文
    s = re_blank.sub("", s)
    s = re_mark.sub("", s)
    s = re_letter.sub("", s)
    return s  # 获得页面上的中文


def testRmTags():
    id = 1
    db = DB("47.100.50.2", 'root', '123', 'ruanjian', charset='utf8')
    res_content = db.executeSelectSql("SELECT id FROM url_tb WHERE id>6188 ORDER BY id", (id,))
    if res_content and res_content[0] and res_content[0][0]:
        html_parse = rm_tags(res_content[0][0])
        print(html_parse)


def parseStrToDict(wordIndex):
    docId_num_list = wordIndex.split(",")
    docId_num_dict = {}
    for docId_num in docId_num_list:
        docId, num = [int(x) for x in docId_num.split("/")]
        docId_num_dict[docId] = num
    return docId_num_dict


def parseDictToStr(wordIndexDict):
    wordIndexStr = ""
    for key, val in wordIndexDict.items():
        wordIndexStr += str(key) + "/" + str(val) + ","
    return wordIndexStr[0:-1]


def testDictStrChange():
    docId_num_dict = parseStrToDict("4/5,7/8,999/1234")
    print(docId_num_dict)
    print(parseDictToStr(docId_num_dict))


def get_url_tb_ids(db):
    res = db.executeSelectSql("SELECT id FROM url_tb WHERE id>6383 ORDER BY id", ())
    #     print("res",res)
    return [tup[0] for tup in res] if res else []


def saveIndexToDb(db, id_, word_dict):
    # 这里的word_dict是对应每一页的单词的倒排索引，因此docId是唯一的
    re_id = re.compile("{0}/\d*".format(id_))
    for word in word_dict.keys():
        # 对于词典中的每一个词
        db_lock.acquire()
        res_wordIndex = db.executeSelectSql("SELECT result FROM dict_tb1 WHERE word=%s", (word,))
        db_lock.release()

        if res_wordIndex and res_wordIndex[0] and res_wordIndex[0][0]:  # 有这个单词的倒排索引
            wordIndex = res_wordIndex[0][0]
            mat = re_id.search(wordIndex)
            if mat != None:
                # 该单词的倒排索引中已经记录该docId
                continue
            else:
                wordIndex += ",{0}/{1}".format(id_, word_dict[word])
            db_lock.acquire()
            db.executeUpdateSql("UPDATE dict_tb1 SET result=%s WHERE word=%s", (wordIndex, word))
            db_lock.release()
        else:  # 没有这个单词的倒排索引
            wordDict_str = "{0}/{1}".format(id_, word_dict[word])
            db_lock.acquire()
            db.executeUpdateSql("INSERT INTO dict_tb1 (word,result) VALUES(%s,%s)", (word, wordDict_str))
            db_lock.release()
    print("向数据库插入一个word_dict:" + str(id_))


def testSaveIndexToDb(db):
    word_docId_num_dict = {'浙江': {1: 33}, '大学': {1: 30}, '浙江大学': {1: 30}, '软件': {1: 14}, '学院': {1: 17}, '首页': {1: 1},
                           '概况': {1: 1}, '简介': {1: 1}, '领导': {1: 1}, '办学': {1: 1}, '特色': {1: 1}}
    saveIndexToDb(db, word_docId_num_dict)
    print("done")


def saveTitleTextToDb(db, content, title, id_):
    db_lock.acquire()
    db.executeUpdateSql("UPDATE url_tb SET text=%s WHERE id=%s", (content, id_))
    db.executeUpdateSql("UPDATE url_tb SET title=%s WHERE id=%s", (title, id_))
    db_lock.release()


def getTitleFromHtml(html):
    '''
    返回html页面的标题
    :param html: 给定的html页面正文
    :return: 有匹配返回标题，无匹配返回None
    '''
    regTitle = re.compile("<title>([^<]*)</title>")
    mat = regTitle.search(html)
    if mat is None:
        return mat.group(1)
    else:
        return ""


def buildOnePage(db, id_):
    print("开始建立第{0}页的分词索引".format(id_))
    word_dict = {}
    db_lock.acquire()
    res_content = db.executeSelectSql("SELECT content FROM url_tb WHERE id=%s", (id_,))
    db_lock.release()
    if res_content and res_content[0] and res_content[0][0]:
        html = res_content[0][0]
        title = getTitleFromHtml(html)
        content = rm_tags(html)
        saveTitleTextToDb(db, content, title, id_)
        seglist = jieba.cut_for_search(content)
        #         print(",".join(seglist))
        for word in seglist:
            if word_dict.get(word) is None:
                # 在字典中
                word_dict[word] += 1
            else:
                word_dict[word] = 1
    #     print(word_dict)
    saveIndexToDb(db, id_, word_dict)
    print("单页的分词索引已建立")


def buildInvertedIndex(db, limit=None):
    id_list = get_url_tb_ids(db)
    pool = threadpool.ThreadPool(num_workers=10)  # 建立一个拥有十个线程的线程池
    args = [((db, x), {}) for x in id_list]
    reqs = threadpool.makeRequests(buildOnePage, args)
    [pool.putRequest(req) for req in reqs]
    pool.wait()
    print("完成分词")


if __name__ == "__main__":
    db = DB(DbParams["ip"], DbParams["user"], DbParams["password"], DbParams["db_name"], charset=DbParams["charset"])
    buildInvertedIndex(db)

