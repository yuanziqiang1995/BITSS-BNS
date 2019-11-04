# coding: utf-8
import sys

from pgmpy.models import BayesianModel
from pgmpy.factors.discrete import TabularCPD
from pgmpy.inference import VariableElimination
import numpy as np
import math
import re
import warnings
# warnings.filterwarnings("ignore")
class Bayes:
    def getegdes(self, str1):
        edges = str1.replace(' ', '').replace('[', '').replace(']', '').replace('(', '').replace(')', '').replace("'",
                                                                                                                  '').replace(
            '\n', '').split(',')
        return edges

    def getcpbvar(self,str1):
        str1 = str1.replace(' ', '').replace('\n', '')
        # 获取variable
        pattern_variable1 = re.compile(r"variable='")
        pattern_variable2 = re.compile(r"variable='.*?'")
        n1 = pattern_variable1.search(str1).span()[1]
        n2 = pattern_variable2.search(str1).span()[1] - 1
        variable = str1[n1:n2]
        # 获取variable_card
        pattern_variable_card1 = re.compile(r"variable_card=")
        pattern_variable_card2 = re.compile(r"variable_card=\d")
        n1 = pattern_variable_card1.search(str1).span()[1]
        n2 = pattern_variable_card2.search(str1).span()[1]
        variable_card = str1[n1:n2]
        # 获取evidence
        if re.search(r"evidence", str1) == None:
            evidence = None
        else:
            pattern_evidence1 = re.compile(r"evidence=")
            pattern_evidence2 = re.compile(r"evidence=.*?]")
            n1 = pattern_evidence1.search(str1).span()[1] + 1
            n2 = pattern_evidence2.search(str1).span()[1] - 1
            evidence = str1[n1:n2]
            evidence = evidence.replace("'", '').split(',')
        # 获取evidence_card
        if re.search(r"evidence", str1) == None:
            evidence_card = None
        else:
            pattern_evidence_card1 = re.compile(r"evidence_card=")
            pattern_evidence_card2 = re.compile(r"evidence_card=.*?]")
            n1 = pattern_evidence_card1.search(str1).span()[1] + 1
            n2 = pattern_evidence_card2.search(str1).span()[1] - 1
            evidence_card = str1[n1:n2]
            evidence_card = evidence_card.split(',')
            evidence_card = list(map(int, evidence_card))
        # 获取values
        pattern_values1 = re.compile(r"values=")
        pattern_values2 = re.compile(r"values=.*?]]")
        n1 = pattern_values1.search(str1).span()[1] + 1
        n2 = pattern_values2.search(str1).span()[1] - 1
        values = str1[n1:n2]
        values = values.replace(' ', '').replace('[', '').replace(']', '').split(',')
        values = list(map(float, values))
        if evidence_card != None:
            values1 = np.array(values).reshape(int(variable_card), np.prod(evidence_card))
        else:
            values1 = np.array(values)
        return variable, int(variable_card), evidence, evidence_card, values

    def infer_query(self,str1):
        str0 = str1.replace('infer.query', '').replace('evidence=', '').replace(' ', '').replace('[', '').replace(']',
                                                                                                                  '').replace(
            '(', '').replace(')', '').replace("'", '').replace('\n', '').replace('{', '').replace('}', '').replace(':',
                                                                                                                   ',').split(
            ',')
        n = len(str0)
        dic = {}
        for i in range(int((n - 1) / 2)):
            dic[str0[2 * i + 1]] = int(str0[2 * i + 2])
        return str0[0], dic

    def query(self, networkFile, queryFile):
        file1 = open(networkFile)
        lines = file1.readlines()
        model = BayesianModel()
        edges = self.getegdes(lines[0])
        for i in range(int(len(edges) / 2)):
            model.add_edge(edges[2 * i], edges[2 * i + 1])
        for line in lines[1:]:
            variable, variable_card, evidence, evidence_card, values = self.getcpbvar(line)
            cpb = TabularCPD(variable=variable, variable_card=variable_card, evidence=evidence,
                             evidence_card=evidence_card,
                             values=[values])
            model.add_cpds(cpb)
        model.check_model()

        infer = VariableElimination(model)
        # infer.query(['G'], evidence={'S': 0, 'D':1})
        file2 = open(queryFile)
        lines = file2.readlines()
        for line in lines:
            node, evidence2 = self.infer_query(line)
            print(infer.query([node], evidence=evidence2)[node].values)
if __name__ == "__main__":
    file1 = ""
    file2 = ""
    if len(sys.argv) != 3:
        file1 = "model/StaticDiscreteNetwork-bfee5691-c48f-e674-c066-e4780a54233e.txt"
        file2 = "inputs/StaticDiscreteNetwork-bfee5691-c48f-e674-c066-e4780a54233e.txt"
    else:
        file1 = sys.argv[1]
        file2 = sys.argv[2]
    b = Bayes()
    b.query(file1, file2)

