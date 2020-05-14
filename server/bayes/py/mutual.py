import sys
import pandas as pd
import networkx as nx
import numpy as np
from pgmpy.estimators import BayesianEstimator
from pgmpy.models import BayesianModel
from pgmpy.estimators import HillClimbSearch, BicScore
from pgmpy.estimators import K2Score, BdeuScore
from sklearn import metrics as mr


class MutualInfo:

    def getegdes(self, str):
        edges = str.replace(' ', '').replace('[', '').replace(']', '').replace('(', '').\
            replace(')', '').replace("'",'').replace('\n', '').split(',')
        return edges

    def getnode(self, str):
        nodes = str.replace(' ', '').replace('[', '').replace(']', '').replace('(', '').\
            replace(')', '').replace("'",'').replace('\n', '').split(',')
        return nodes

    def cal(self, file1, file2):
        f1 = open(file1)
        lines = f1.readlines()
        nodes = self.getegdes(lines[0])
        edges = self.getegdes(lines[1])
        data = pd.read_csv(file2)

        G = BayesianModel()
        G.add_nodes_from(nodes)
        for i in range(int(len(edges) / 2)):
            G.add_edge(edges[2 * i], edges[2 * i + 1])

        output1 = []

        for i in range(int(len(edges) / 2)):
            mut = mr.mutual_info_score(data[edges[2 * i]],data[edges[2 * i + 1]])
            output1.append(mut)

        output2 = {}
        for node1 in G.nodes():
            d = {}
            for node2 in G.nodes():
                if node1 == node2:
                    continue
                mut = mr.mutual_info_score(data[node1], data[node2])

                d[node2] = mut
            output2[node1] = d

        print(output1)
        print(output2)

        with open('mutual_output.txt', 'w') as f:
            f.write(str(output1))
            f.write('\n')
            f.write(str(output2))


if __name__ == "__main__":
    file1 = ""
    file2 = ""
    if len(sys.argv) != 3:
        file1 = "mutual_input.txt"
        file2 = "dataset1.csv"
    else:
        file1 = sys.argv[1]
        file2 = sys.argv[2]
    b = MutualInfo()
    b.cal(file1, file2)
