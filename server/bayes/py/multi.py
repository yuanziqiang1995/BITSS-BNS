import sys
import numpy as np
from pgmpy.models import BayesianModel
from pgmpy.factors.discrete import TabularCPD
from pgmpy.inference import BeliefPropagation, VariableElimination


class MultiVis:

    all_node = []
    all_link = []

    def getnode(self, str):
        node = str.replace("\n", "")
        return node

    def getegdes(self, str):
        edges = str.replace(' ', '').replace('[', '').replace(']', '').replace('(', ''). \
            replace(')', '').replace("'", '').replace('\n', '').split(',')
        return edges

    def parseCpd(self, str, n, card):
        l = []
        l.append(n)
        if card[0] != '':
            l = l + card
        cpd_str = str.replace(' ', '').replace('[', '').replace(']', '').replace('(', ''). \
            replace(')', '').replace("'", '').replace('\n', '').split(',')
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
                    l3.append(l2[k + j])
                k += i
                l1.append(l3)
            l2 = l1.copy()
        cpd_num = np.array(l2)
        cpd = np.transpose(cpd_num)
        cpd = cpd.reshape((n, len(cpd_str) // n))

        return cpd

    def getList(self, str):
        sequence = str.replace(' ', '').replace('[', '').replace(']', '').replace('(', ''). \
            replace(')', '').replace("'", '').replace('\n', '').split(',')
        return sequence

    def getCard(self, str):
        sequence = str.replace(' ', '').replace('[', '').replace(']', '').replace('(', ''). \
            replace(')', '').replace("'", '').replace('\n', '').split(',')
        for i in range(len(sequence)):
            if sequence[i] != '':
                sequence[i] = int(sequence[i])
        return sequence

    def getCompose(self, l):
        N = 1
        for node1 in l:
            n = self.all_node[node1]
            v = n['valueNum']
            N *= v
        l_value = np.ones((N, len(l)))
        time = N
        j = 0
        for node1 in l:
            n = self.all_node[node1]
            value = n['valueNum']
            time /= value
            i = 0
            for v in range(value):
                l_value[int(i * time): int((i + 1) * time), j] = v
                i += 1
            # print(np.tile(l_value[0:int(i * time), j], (1, int(N / time / value))))
            l_value[:, j] = np.tile(l_value[0:int(i * time), j], (1, int(N / time / value)))
            j += 1
        return l_value.astype(int).tolist()

    def changeSequence(self, x, y, value, N, M):

        dict = {}
        for i, item in enumerate(x):
            dict[item] = i
        l = []
        for item in y:
            v = dict[item]
            l.append(v)

        for i in range(len(l) - 1):
            for j in range(len(l) - i - 1):
                if l[j] > l[j + 1]:
                    l[j], l[j + 1] = l[j + 1], l[j]
                    value = value.swapaxes(j, j + 1)
        value = value.reshape((N, M))
        return value


    def learn(self, file1):
        f1 = open(file1,encoding="utf8")
        lines = f1.readlines()
        i = 0
        G = BayesianModel()
        nodeList = {}
        while i < len(lines):
            if lines[i] == '\n':
                break
            nodeName = self.getnode(lines[i])
            valueNum = int(lines[i + 1])
            cpd_str = lines[i + 2]
            sequence = self.getList(lines[i + 3])
            card = self.getCard(lines[i + 4])
            cpd = self.parseCpd(cpd_str, valueNum, card)
            l = {}
            l['nodeName'] = nodeName
            l['valueNum'] = valueNum
            l['cpd'] = cpd
            l['sequence'] = sequence
            l['card'] = card
            # l = [nodeName,valueNum,cpd,sequence,card]

            nodeList[nodeName] = l
            i += 5

        edges = self.getegdes(lines[i + 1])
        left = self.getList(lines[i + 3])
        top = self.getList(lines[i + 5])
        bottom = self.getList(lines[i+7])
        right = self.getList(lines[i+9])
        self.all_node = nodeList
        self.all_link = edges

        con = left + top
        non = bottom + right
        if len(set(con+non))!=len(con+non):
            print("元素不能有重复")
            return

        for i in range(int(len(edges) / 2)):
            G.add_edge(edges[2 * i], edges[2 * i + 1])

        for node in nodeList.values():
            if node['sequence'][0] == '':
                cpt = TabularCPD(variable=node['nodeName'], variable_card=node['valueNum'], values=node['cpd'])
            else:
                cpt = TabularCPD(variable=node['nodeName'], variable_card=node['valueNum'], evidence=node['sequence'],
                                 evidence_card=node['card'], values=node['cpd'])
            G.add_cpds(cpt)

        result = []

        if G.check_model():
            left_v = self.getCompose(left)
            top_v = self.getCompose(top)
            bottom_v = self.getCompose(bottom)
            right_v = self.getCompose(right)
            # evidence = {}
            inference = VariableElimination(G)
            # for list1 in left_v:
            #     for list2 in top_v:
            #         for i, evi in enumerate(list1):
            #             evidence[left[i]]=evi
            #         for j, evi in enumerate(list2):
            #             evidence[top[j]] = evi

            N1 = 1
            M1 = 1
            for node in left + right:
                n = self.all_node[node]
                v = n['valueNum']
                N1 *= v
            for node in top + bottom:
                n = self.all_node[node]
                v = n['valueNum']
                M1 *= v

            N2 = 1
            M2 = 1
            for node in left:
                n = self.all_node[node]
                v = n['valueNum']
                N2 *= v
            for node in top:
                n = self.all_node[node]
                v = n['valueNum']
                M2 *= v
            x1 = left + right + top + bottom
            x2 = left + top
            query1 = inference.query(variables=x1, show_progress=False)
            query2 = inference.query(variables=x2, show_progress=False)

            value1 = self.changeSequence(x1, query1.variables, query1.values, N1, M1)
            value2 = self.changeSequence(x2, query2.variables, query2.values, N2, M2)

            # repeat = int(N1*M1/N2/M2)
            aug_x = int(N1 / N2)
            aug_y = int(M1 / M2)
            aug = np.ones((N1, M1))
            for i in range(0, aug.shape[0]):
                for j in range(0, aug.shape[1]):
                    aug[i, j] = value2[int(i / aug_x), int(j / aug_y)]


            # value2 = np.repeat(value2, repeat).reshape(N1, M1)
            # result = np.true_divide(value1, value2)
            result = np.true_divide(value1, aug)
            result = result.tolist()

            print(result)
            with open('multi_output.txt', 'w') as f:
                f.write(str(result))


if __name__ == "__main__":
    file1 = ""
    if len(sys.argv) != 2:
        file1 = "multi_input.txt"
    else:
        file1 = sys.argv[1]
    b = MultiVis()
    try:
        b.learn(file1)
    except Exception as e:
        print(e)
