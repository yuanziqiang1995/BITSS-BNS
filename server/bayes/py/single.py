import sys
import pandas as pd
import networkx as nx
import numpy as np
from pgmpy.models import BayesianModel
from pgmpy.factors.discrete import TabularCPD
from pgmpy.inference import BeliefPropagation, VariableElimination


class SingleVis:

    def getnode(self, str):
        node = str.replace("\n", "")
        return node

    def getegdes(self, str):
        edges = str.replace(' ', '').replace('[', '').replace(']', '').replace('(', '').\
            replace(')', '').replace("'",'').replace('\n', '').split(',')
        return edges

    def parseCpd(self, str, n, card):
        l = []
        l.append(n)
        if card[0] != '':
            l = l+card
        cpd_str = str.replace(' ', '').replace('[', '').replace(']', '').replace('(', '').\
            replace(')', '').replace("'",'').replace('\n', '').split(',')
        for i in range(len(cpd_str)):
            if cpd_str[i] != '':
                cpd_str[i] = float(cpd_str[i])
        l2 = cpd_str.copy()
        for i in l[:-1]:
            l1 = []
            k = 0
            while k < len(l2):
                l3 = []
                for j in range(i):
                    l3.append(l2[k+j])
                k += i
                l1.append(l3)
            l2 = l1.copy()
        cpd_num = np.array(l2)
        cpd = np.transpose(cpd_num)
        cpd = cpd.reshape((n, len(cpd_str)//n))

        return cpd

    def getList(self, str):
        sequence = str.replace(' ', '').replace('[', '').replace(']', '').replace('(', '').\
            replace(')', '').replace("'",'').replace('\n', '').split(',')
        return sequence

    def getCard(self, str):
        sequence = str.replace(' ', '').replace('[', '').replace(']', '').replace('(', ''). \
            replace(')', '').replace("'", '').replace('\n', '').split(',')
        for i in range(len(sequence)):
            if sequence[i] != '':
                sequence[i] = int(sequence[i])
        return sequence


    def learn(self, file1):
        f1 = open(file1)
        lines = f1.readlines()
        i = 0
        G = BayesianModel()
        nodeList = {}
        while i < len(lines):
            if lines[i] == '\n':
                break
            nodeName = self.getnode(lines[i])
            valueNum = int(lines[i+1])
            cpd_str = lines[i+2]
            sequence = self.getList(lines[i+3])
            card = self.getCard(lines[i+4])
            cpd = self.parseCpd(cpd_str, valueNum, card)
            l = {}
            l['nodeName'] = nodeName
            l['valueNum'] = valueNum
            l['cpd'] = cpd
            l['sequence'] = sequence
            l['card'] = card
            # l = [nodeName,valueNum,cpd,sequence,card]

            nodeList[nodeName]=l
            i += 5
        edges = self.getegdes(lines[i+1])
        con = self.getList(lines[i+3])
        noncon = self.getList(lines[i+5])
        # print(nodeList)
        for i in range(int(len(edges) / 2)):
            G.add_edge(edges[2 * i], edges[2 * i + 1])

        for (this, node) in nodeList.items():
            if node['sequence'][0] == '':
                cpt = TabularCPD(variable=node['nodeName'], variable_card=node['valueNum'], values=node['cpd'])
            else:
                cpt = TabularCPD(variable=node['nodeName'], variable_card=node['valueNum'], evidence=node['sequence'],
                                 evidence_card=node['card'],values=node['cpd'])
            G.add_cpds(cpt)

        if G.check_model():
            # print('1')
            # belief_propagation = BeliefPropagation(G)
            inference = VariableElimination(G)
            result = []
            for c in con:
                for non in noncon:
                    if c == non:
                        continue
                    cnode = nodeList[c]
                    nnode = nodeList[non]
                    cname = cnode['nodeName']
                    cnum = cnode['valueNum']
                    nname = nnode['nodeName']
                    namelist = [nname]
                    this = []
                    for i in range(cnum):
                        l = {}
                        l[cname] = i
                        phi_query = inference.query(variables=namelist, evidence=l,show_progress=False).values
                        this.append(phi_query.tolist())
                    result.append(this)
            print(result)



if __name__ == "__main__":
    file1 = sys.argv[1]
    b = SingleVis()
    b.learn(file1)
