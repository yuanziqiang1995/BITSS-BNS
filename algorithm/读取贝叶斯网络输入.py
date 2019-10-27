# coding: utf-8
from pgmpy.models import BayesianModel
from pgmpy.factors.discrete import TabularCPD
from pgmpy.inference import VariableElimination
import numpy as np
import math
import re
def getegdes(str1):
    edges=str1.replace(' ','').replace('[','').replace(']','').replace('(','').replace(')','').replace("'",'').replace('\n','').split(',')
    return edges
def getcpbvar(str1):
    str1=str1.replace(' ','').replace('\n','')
    #获取variable
    pattern_variable1=re.compile(r"variable='")
    pattern_variable2=re.compile(r"variable='.*?'")
    n1=pattern_variable1.search(str1).span()[1]
    n2=pattern_variable2.search(str1).span()[1]-1
    variable=str1[n1:n2]
    #获取variable_card
    pattern_variable_card1=re.compile(r"variable_card=")
    pattern_variable_card2=re.compile(r"variable_card=\d")
    n1=pattern_variable_card1.search(str1).span()[1]
    n2=pattern_variable_card2.search(str1).span()[1]
    variable_card=str1[n1:n2]
    #获取evidence
    if re.search(r"evidence",str1)==None:
        evidence=None
    else:
        pattern_evidence1=re.compile(r"evidence=")
        pattern_evidence2=re.compile(r"evidence=.*?]")
        n1=pattern_evidence1.search(str1).span()[1]+1
        n2=pattern_evidence2.search(str1).span()[1]-1
        evidence=str1[n1:n2]
        evidence=evidence.replace("'",'').split(',')
    #获取evidence_card
    if re.search(r"evidence",str1)==None:
        evidence_card=None
    else:    
        pattern_evidence_card1=re.compile(r"evidence_card=")
        pattern_evidence_card2=re.compile(r"evidence_card=.*?]")
        n1=pattern_evidence_card1.search(str1).span()[1]+1
        n2=pattern_evidence_card2.search(str1).span()[1]-1
        evidence_card=str1[n1:n2]
        evidence_card=evidence_card.split(',')
        evidence_card=list(map(int, evidence_card))
    #获取values
    pattern_values1=re.compile(r"values=")
    pattern_values2=re.compile(r"values=.*?]]")
    n1=pattern_values1.search(str1).span()[1]+1
    n2=pattern_values2.search(str1).span()[1]-1
    values=str1[n1:n2]
    values=values.replace(' ','').replace('[','').replace(']','').split(',')
    values=list(map(float, values))
    if evidence_card!=None:
        values1=np.array(values).reshape(int(variable_card),np.prod(evidence_card))
    else: 
        values1=np.array(values)
    return variable,int(variable_card),evidence,evidence_card,values



#str0="variable='D', variable_card=2, values=[[0.6, 0.4]]"
#str1="variable='G', variable_card=3,values=[[0.3, 0.05, 0.9,  0.5],[0.4, 0.25, 0.08, 0.3],[0.3, 0.7,  0.02, 0.2]],evidence=['I', 'D'],evidence_card=[2, 2]"
file=open("bayesnetwork.txt") 
lines=file.readlines()
model=BayesianModel() 
edges=getegdes(lines[0])
for i in range(int(len(edges)/2)):
    model.add_edge(edges[2*i], edges[2*i+1])
for line in lines[1:]:   
    variable,variable_card,evidence,evidence_card,values=getcpbvar(line)
    cpb=TabularCPD(variable=variable, variable_card=variable_card,evidence=evidence,evidence_card=evidence_card,values=[values])
    model.add_cpds(cpb)
model.check_model()