
import networkx as nx
import threadpool
from Common import DB
from Common import DbParams, db_lock

'''
url pagerank计算模块，根据数据库中已有的所有url及其指向url构建网络图，计算各网页的pagerank
'''

def get_url_tb_ids(db):
    res = db.executeSelectSql("SELECT id FROM url_tb ORDER BY id", ())
    #     print("res",res)
    return [tup[0] for tup in res] if res else []


def test_get_url_tb_ids(db):
    id_list = get_url_tb_ids(db)
    print(type(id_list[0]))


def addEdge(db, G, id_list, id_):
    db_lock.acquire()
    pointingTo_res = db.executeSelectSql("SELECT pointing FROM url_tb WHERE id=%s", (id_,))
    db_lock.release()
    #         print(pointingTo_res)
    if pointingTo_res and pointingTo_res[0] and pointingTo_res[0][0]:
        pointingTo = pointingTo_res[0][0]
        pointing_num_set = set([int(x) for x in pointingTo.split(",") if int(x) in id_list])  # 获得
        #             print(pointing_num_set)
        for pointing_num_id in pointing_num_set:
            G.add_edge(id_, pointing_num_id)
    print(id_)


def buildDiGraph(db):
    G = nx.DiGraph()
    id_list = get_url_tb_ids(db)
    print(id_list)
    args = [((db, G, id_list, id_), {}) for id_ in id_list]
    pool = threadpool.ThreadPool(num_workers=5)
    reqs = threadpool.makeRequests(addEdge, args)
    [pool.putRequest(req) for req in reqs]
    pool.wait()
    return G


#     layout = nx.spring_layout(G)
#     nx.draw(G, pos=layout, with_labels=True, hold=False)
#     plt.show()
#     for index in G.edges(data=True):
#         print(index)   #输出所有边的节点关系和权重


if __name__ == "__main__":
    db = DB(DbParams["ip"], DbParams["user"], DbParams["password"], DbParams["db_name"])
    G = buildDiGraph(db)
    pr = nx.pagerank_numpy(G)
    for key, value in pr.items():
        db.executeUpdateSql("INSERT INTO url_pr(url_id,pr) VALUES(%s,%s)", (key, value))
