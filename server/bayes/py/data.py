import sys
import pandas as pd
import networkx as nx
import numpy as np


class ReadData:

    def learn(self, file,page,pageSize):
        data = pd.read_csv(file)
        nodes = data.columns
        print(len(data.values))
        print(",".join(data.columns.array))
        for i in range(page*pageSize,(page+1)*pageSize):
            print(",".join(data.values[i]))
        return 0



if __name__ == "__main__":
    file1 = sys.argv[1]
    page = int(sys.argv[2])
    pageSize = int(sys.argv[3])
    b = ReadData()
    b.learn(file1,page,pageSize)
