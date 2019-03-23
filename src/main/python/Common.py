# coding=utf-8
import mysql.connector
import rwlock


class DB:
    def __init__(self, host, user, password, db, port=3306, charset='utf-8'):
        self.db = mysql.connector.connect(host=host, user=user, passwd=password,database=db,port=3306,charset=charset)
        self.cursor = self.db.cursor()

    def executeUpdateSql(self, sql, params=None):
        try:
            self.cursor.execute(sql, params)
            self.db.commit()
        except Exception as e:
            self.db.rollback()
            print(repr(e))

    def executeSelectSql(self, sql, params):
        try:
            self.cursor.execute(sql, params)
            res = self.cursor.fetchall()
            return res
        except Exception as e:
            print(repr(e))
            return None


# 爬虫数据存储的数据库参数
DbParams = {
    "ip": "127.0.0.1",
    "user":"root",
    "password": "xxx",
    "db_name": "ruanjian",
    "charset": "utf8"
}
start_url = "http://www.cst.zju.edu.cn"
db_lock = rwlock.RWLock().writer_lock
db_rwlock = rwlock.RWLock()
