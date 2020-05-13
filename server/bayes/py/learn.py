import sys
import pandas as pd
import networkx as nx
import numpy as np
from pgmpy.estimators import BayesianEstimator
from pgmpy.models import BayesianModel
from pgmpy.estimators import HillClimbSearch, BicScore, K2Score
import matplotlib.pyplot as plt


class LearnBayes:

    def getegdes(self, str):
        edges = str.replace(' ', '').replace('[', '').replace(']', '').replace('(', '').\
            replace(')', '').replace("'",'').replace('\n', '').split(',')
        return edges

    def learn(self, file1, file2):
        f1 = open(file1)
        lines = f1.readlines()
        edges = self.getegdes(lines[0])
        data = pd.read_csv(file2)

        G = nx.DiGraph()
        est = HillClimbSearch(data, scoring_method=BicScore(data))
        model = est.estimate()
        G.add_edges_from(model.edges())


        for i in range(int(len(edges) / 2)):
            G_ = G.copy()
            G_.add_edge(edges[2 * i], edges[2 * i + 1])
            if G_.number_of_selfloops == 0:
                G.add_edge(edges[2 * i], edges[2 * i + 1])

        G_ = BayesianModel()
        G_.add_edges_from(G.edges)
        G = G_


        # N = G.number_of_nodes()
        # B = np.zeros((N*(N-1)//2, N))
        # i = 0
        # y = []
        # k = 0
        # nodes = list(G.nodes._nodes.keys())
        # for i in range(len(nodes)):
        #     for j in range(i+1, len(nodes)):
        #         if nx.has_path(G, nodes[i], nodes[j]):
        #             y.append(1)
        #             B[k, i] = 1
        #             B[k, j] = -1
        #         elif nx.has_path(G, nodes[j], nodes[i]):
        #             y.append(-1)
        #             B[k, i] = 1
        #             B[k, j] = -1
        #         else:
        #             y.append(0)
        #         k += 1
        #
        # W = np.eye(N, N)
        # est = HillClimbSearch(data, scoring_method=BicScore(data))
        # model = est.estimate()
        # G_ = nx.DiGraph()
        # G_.add_edges_from(model.edges())
        # queue = []
        # for node in G_.nodes():
        #     if G_.in_degree(node) == 0:
        #         queue.append(node)
        #         G.node[node]['s'] = N
        #     else:
        #         G.node[node]['s'] = N//2
        # while len(queue)>0:
        #     now = queue[0]
        #     l = list(G_._succ[now].keys())
        #     for i in l:
        #         G.node[i]['s'] = G.node[now]['s'] - 1
        #     queue += l
        #     queue.pop(0)
        #
        # phai = []
        # for node in G.nodes():
        #     phai.append(G.node[node]['s'])
        # miu1 = np.dot(np.transpose(B), B)
        # miu1 = np.linalg.pinv(miu1)
        # miu2 = np.dot(np.transpose(B), y)
        # miu2 = miu2 + phai
        # miu = np.dot(miu1, miu2)
        #
        # seq = miu.tolist()
        # seq = list(zip(seq, nodes))
        # seq = sorted(seq, key=lambda s: s[0])
        # seq = [x[1] for x in seq]



        # nx.draw(G)
        # plt.show()
        estimator = BayesianEstimator(G, data)



        edges = []
        for i in G.edges:
            edges.append(str(i))
        print(edges)
        for i in G.nodes:
            cpd = estimator.estimate_cpd(i, prior_type="K2")
            nodeName = i
            values = dict(data[i].value_counts())
            valueNum = len(values)
            CPT = np.transpose(cpd.values)
            # CPT = cpd.values
            sequence = cpd.variables[1::]
            card = []
            for x in sequence:
                s = len(dict(data[x].value_counts()))
                card.append(s)
            output = nodeName+'\t'+str(valueNum)+'\t'+str(CPT.tolist())+'\t'+ str(sequence)+'\t'+str(card)
            print(output)
            



if __name__ == "__main__":
    file1 = sys.argv[1]
    file2 = sys.argv[2]
    b = LearnBayes()
    b.learn(file1, file2)
