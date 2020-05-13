import sys
import pandas as pd
import networkx as nx
import numpy as np


class ReadData:

    def learn(self, file):
        data = pd.read_csv(file)
        nodes = data.columns
        a = data.describe().iloc[1]
        value = []
        id = 0
        NUM = data.shape[0]
        with open('read_output.txt', 'w') as f:
            for node in nodes:
                values = dict(data[node].value_counts())
                id = id + 1
                output = str(id)+'\t'+str(node)+'\t'+str(len(values))
                print(output)
                vid = 0
                for (k,v) in values.items():
                    vid = vid + 1
                    output = str(vid) + '\t' + str(k) + '\t' + str(v) + '\t' +str(v/NUM)
                    print(output)
                print('')
        return 0



if __name__ == "__main__":
    file1 = sys.argv[1]
    b = ReadData()
    b.learn(file1)
