import sys
import pandas as pd
import networkx as nx
import numpy as np
from pgmpy.estimators import BayesianEstimator
from pgmpy.models import BayesianModel
from pgmpy.estimators import HillClimbSearch, BicScore
from pgmpy.estimators import K2Score, BdeuScore
import matplotlib.pyplot as plt
from sklearn import metrics as mr


class OptBayes:

    def getegdes(self, str):
        edges = str.replace(' ', '').replace('[', '').replace(']', '').replace('(', '').\
            replace(')', '').replace("'",'').replace('\n', '').split(',')
        return edges

    def getnode(self, str):
        nodes = str.replace(' ', '').replace('[', '').replace(']', '').replace('(', '').\
            replace(')', '').replace("'",'').replace('\n', '').split(',')
        return nodes

    def opt(self, file1, file2):
        f1 = open(file1)
        lines = f1.readlines()
        nodes = self.getegdes(lines[0])
        edges = self.getegdes(lines[1])
        data = pd.read_csv(file2)

        G = BayesianModel()
        G.add_nodes_from(nodes)
        for i in range(int(len(edges) / 2)):
            G.add_edge(edges[2 * i], edges[2 * i + 1])
        # nx.draw(G)
        # plt.show()
        k2 = K2Score(data).score(G)
        bic = BicScore(data).score(G)
        bdeu = BdeuScore(data).score(G)
        print(k2,",",bic,",",bdeu)

        est = HillClimbSearch(data, scoring_method=K2Score(data))
        model = est.estimate()
        model_edges = model.edges()
        G_ = nx.DiGraph()
        G_.add_edges_from(model_edges)
        G_copy = nx.DiGraph()
        G_copy.add_edges_from(G.edges)
        add = []
        add_mut = []
        delete = []
        delete_mut = []
        # a = list(G.edges._adjdict.key())
        for edge in model_edges:
            node1 = edge[0]
            node2 = edge[1]
            if not nx.has_path(G, node2, node1):
                if not G.has_edge(node1, node2):
                    this = (node1, node2)
                    # this = '('+node1+','+node2+')'
                    add.append(this)
                    x = data[node1]
                    mut = mr.mutual_info_score(data[node1],data[node2])
                    add_mut.append(mut)
        seq = list(zip(add_mut, add))
        seq = sorted(seq, key=lambda s: s[0], reverse=True)
        alpha = 0.015
        # if seq[0][0] > alpha:
        #     add = seq[0:1]

        add = seq[0:1]


        data_edges = []
        for edge in G.edges:
            node1 = edge[0]
            node2 = edge[1]
            mut = mr.mutual_info_score(data[node1],data[node2])
            delete_mut.append(mut)
            data_edges.append(edge)
            # if not (nx.has_path(G_, node1, node2) or nx.has_path(G_, node2, node1)):
            #     this = '('+node1+','+node2+')'
            #     delete.append(this)
        seq = list(zip(delete_mut, data_edges))
        seq = sorted(seq, key=lambda s: s[0])

        # if seq[0][0] < alpha:
        #     delete = seq[0:1]
        if len(edges)>2:
            delete = seq[0:1]
            if len(add)>0:
                if delete[0][0] > add[0][0]:
                    delete = []

        with open('opt_output.txt', 'w') as f:
            f.write('k2 bic bdeu\n')
            f.write(str(k2))
            f.write(' ')
            f.write(str(bic))
            f.write(' ')
            f.write(str(bdeu))
            f.write('\nadd\n')
            print('add')
            for i in add:
                f.write(str(i[1]))
                print(str(i[1])+","+str(i[0]))
                # print(i[0])
                f.write('\n')
                f.write(str(i[0]))
                f.write('\n')

            f.write('delete\n')
            print('delete')
            for j in delete:
                f.write(str(j[1]))
                f.write('\n')
                f.write(str(j[0]))
                f.write('\n')
                print(str(j[1]) + "," + str(j[0]))
                # print(j[0])
            print('cpt')
            estimator = BayesianEstimator(G, data)
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
                output = nodeName + '\t' + str(valueNum) + '\t' + str(CPT.tolist()) + '\t' + str(sequence) + '\t' + str(
                    card)
                print(output)
                f.write('\n')
                f.write(output)


if __name__ == "__main__":
    file1 = sys.argv[1]
    file2 = sys.argv[2]
    b = OptBayes()
    b.opt(file1, file2)
