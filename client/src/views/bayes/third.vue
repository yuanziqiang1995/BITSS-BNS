<template>
  <thirdLayout>
    <thirdNetwork id="ccc" slot="left" :treeLayout="true" :model="thirdModel" ref="thirdNetwork"></thirdNetwork>
    <div slot="buttons" style="z-index:1000;">
      <el-button-group>
        <el-button type="primary" style="z-index:1000;" size="small" @click="applySuggest">使用全部建议</el-button>
        <el-button
          type="primary"
          style="z-index:1000;"
          size="small"
          @click="submitNetwork"
          :loading="judging"
        >评估网络</el-button>
      </el-button-group>
    </div>

    <div slot="right" style="width:100%;height:100%;display:flex;flex-direction:column;">
      <el-tabs type="border-card">
        <el-tab-pane label="节点信息">
          <div>
            变量：
            <el-select v-model="currentVariable" placeholder="请选择">
              <el-option
                v-for="(item,index) in variables"
                :key="index"
                :label="item.name"
                :value="index"
              ></el-option>
            </el-select>
          </div>
          <el-table :data="currentValues" size="small" style="width: 100%">
            <el-table-column prop="value" label="取值"></el-table-column>
            <el-table-column prop="count" label="数量"></el-table-column>
            <el-table-column prop="percent" label="概率"></el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="条件概率">
          <div>
            变量：
            <el-select v-model="currentVariable" placeholder="请选择">
              <el-option
                v-for="(item,index) in variables"
                :key="index"
                :label="item.name"
                :value="index"
              ></el-option>
            </el-select>
          </div>
          <CellMap style="padding-top:20px;" id="thirdPixelMap" :values="namedCpt" />
        </el-tab-pane>
      </el-tabs>
    </div>
    <div slot="b1" class="fit">
      <el-tabs type="border-card">
        <el-tab-pane label="建议添加">
          <el-table :data="addList" size="mini" style="width: 100%">
            <el-table-column prop="source" label="前驱"></el-table-column>
            <el-table-column prop="target" label="后继"></el-table-column>
            <el-table-column prop="mutualInf" label="互信息"></el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="建议删除">
          <el-table :data="deleteList" size="small" style="width: 100%">
            <el-table-column prop="source" label="前驱"></el-table-column>
            <el-table-column prop="target" label="后继"></el-table-column>
            <el-table-column prop="mutualInf" label="互信息"></el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>
    <div slot="b2" class='fit' style="position:relative;">
      <div
        style="position:absolute;right:0;top:-39px;padding:0 5px;line-height:39px;flex-grow:0;display:block;width:166%;overflow:none;"
      >
        <span
          style="color:rgb(144,147,153);cursor:default;font-weight:700; line-height: 23px;"
        >当前评分</span>
        Bic: {{this.bic}} Bdeu: {{this.bdeu}} K2: {{this.k2}}
      </div>
      <el-tabs type="border-card">
        <el-tab-pane label="操作记录">
         <div style="overflow-y:auto;width:100%;flex-grow:1;">
        <el-table :data="models" size="mini" style="width: 100%">
          <el-table-column prop="time" label="时间"></el-table-column>
          <el-table-column prop="Bic" label="Bic"></el-table-column>
          <el-table-column prop="Bdeu" label="Bdeu"></el-table-column>
          <el-table-column prop="K2" label="K2"></el-table-column>

          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button size="mini" @click="loadModel(scope.row)">载入</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
        </el-tab-pane>
      </el-tabs>
    
      
    </div>
  </thirdLayout>
</template>
<script>
import thirdLayout from "./thirdLayout";
import thirdNetwork from "./thirdNetwork";
import CellMap from "@/components/visual/CellMap";

export default {
  name: "third",
  components: {
    thirdLayout,
    thirdNetwork,
    CellMap
  },
  data() {
    return {
      judging: false,
      namedCpt: [],
      values: [
        [0.4, 0.1, 0.2],
        [0.1, 0.2, 0.3],
        [0.2, 0.4, 0.5]
      ],

      currentValues: [],
      currentVariable: null,
      evaluate: [
        {
          bic: 1,
          bdeu: 2,
          k2: 3,
          time: "12:12:12"
        },
        {
          bic: 1,
          bdeu: 2,
          k2: 3,
          time: "12:12:12"
        },
        {
          bic: 1,
          bdeu: 2,
          k2: 3,
          time: "12:12:12"
        }
      ],
      toAdd: [
        {
          from: "gender",
          to: "class",
          info: 3
        },
        {
          from: "gender",
          to: "student",
          info: 4
        }
      ],
      toDelete: [
        {
          from: "class",
          to: "gender",
          info: 3
        },
        {
          from: "student",
          to: "gender",
          info: 4
        }
      ],
      thirdModel: {
        node: [],
        link: []
      },
      nodeList: [],
      bic: 0,
      bdeu: 0,
      k2: 0,
      addList: [],
      deleteList: []
    };
  },
  props: {
    datasetId: {
      default: null
    },
    nameIdMap: {
      type: Object,
      default() {
        return {};
      }
    },
    idMap: {
      type: Object,
      default() {
        return {};
      }
    },
    nodeInfo: {
      type: Array,
      default() {
        return [];
      }
    },
    models: {
      type: Array,
      default() {
        return [];
      }
    }
  },
  methods: {
    loadModel(row) {
      this.models.push(JSON.parse(JSON.stringify(row)));
    },
    submitNetwork() {
      let links = this.$refs.thirdNetwork.getModel().link;
      let link2 = links.filter(x => x.color !== "#00FF7F");
      let that = this;
      this.judging = true;

      this.$request
        .post("/bayes/network/opt", {
          datasetId: this.$route.query.datasetId,
          edges: link2.reduce((a, b) => {
            a.push(b.from);
            a.push(b.to);
            return a;
          }, []),
          nodes: this.nodeInfo.map(x => {
            return x.nodeName;
          })
        })
        .then(res => {
          let r = res.data.data;
          let info = r[0].split(",");
          let tempModel = {
            linkList: link2.map(x => {
              return {
                sourceId: x.from,
                targetId: x.to
              };
            })
          };
          tempModel.K2 = (+info[0]).toFixed(3);
          tempModel.Bic = (+info[1]).toFixed(3);
          tempModel.Bdeu = (+info[2]).toFixed(3);
          let adds = [];
          let deletes = [];
          let index = 2;
          let size = r.length;
          for (; index < size; index++) {
            if (r[index] === "delete") {
              index++;
              break;
            }
            adds.push(r[index]);
          }
          for (; index < size; index++) {
            if (r[index] === "cpt") {
              index++;
              break;
            }
            deletes.push(r[index]);
          }
          tempModel.add = adds.map(x => {
            let t = JSON.parse(
              "[" +
                x
                  .replace(/\(/g, "")
                  .replace(/\)/g, "")
                  .replace(/'/g, '"') +
                "]"
            );
            return {
              sourceId: t[0],
              targetId: t[1],
              mutualInf: (+t[2]).toFixed(3)
            };
          });

          tempModel["delete"] = deletes.map(x => {
            let t = x
              .replace(/'/g, "")
              .replace(/\(/g, "")
              .replace(" ", "")
              .replace(/\)/g, "")
              .split(",");
            return {
              sourceId: t[0],
              targetId: t[1],
              mutualInf: (+t[2]).toFixed(3)
            };
          });
          let cpts = [];
          let nodeList = [];
          for (; index < size; index++) {
            if (r[index] === "mutual") {
              index++;
              break;
            }
            cpts.push(r[index]);
          }
           let mutuals = JSON.parse(r[index]);
              for (let i = 0; i < mutuals.length; i++) {
                tempModel.linkList[i].mutual = mutuals[i];
              }
          let nodeMutual = JSON.parse(r[index + 1].replace(/'/g, '"'));

          for (let i of cpts) {
            let t = i.split("\t");
            let sequence = JSON.parse(t[3].replace(/'/g, '"'));
            let stringCPT = t[2].replace(/"/g, "");
            let cpt = JSON.parse(stringCPT);
            if (sequence.length === 0) {
              cpt = [cpt];
            } else {
              for (let j = 1; j < sequence.length; j++) {
                cpt = cpt.flatMap(x => x);
              }
            }

            nodeList.push({
              nodeId: t[0],
              nodeName: t[0],
              valueNum: +t[1],
              mutual: nodeMutual[t[0]],
              CPT: cpt,
              sequence,
              stringCPT
            });
          }
          tempModel.nodeList = nodeList;
          let time = new Date();
          tempModel.time =
            time.getHours() + ":" + time.getMinutes() + ":" + time.getSeconds();
          this.models.push(tempModel);
          this.judging = false;
        });
    },
    applySuggest() {
      let tempModel = this.$refs.thirdNetwork.getModel();
      let tempLink = tempModel.link;
      let link2 = [];
      for (let j of tempLink) {
        let flag = true;
        for (let i of this.deleteList) {
          if (j.from === i.sourceId && j.to === i.targetId) {
            flag = false;
          }
        }
        if (flag) {
          if (j.color === "#00FF7F") {
            flag = false;
          }
        }
        if (flag) {
          link2.push(j);
        }
      }
      for (let i of this.addList) {
        let flag = true;
        for (let j of link2) {
          if (j.from === i.sourceId && j.to === i.targetId) {
            flag = false;
          }
        }
        if (flag) {
          link2.push({
            from: i.sourceId,
            to: i.targetId
          });
        }
      }

      this.thirdModel = {
        node: tempModel.node,
        link: link2
      };
    },
    computeModel(currentModel) {
      let link = currentModel.linkList;
      let addList = currentModel.add.map(x => {
        return {
          sourceId: x.sourceId,
          targetId: x.targetId,
          source: x.sourceId,
          target: x.targetId,
          mutualInf: x.mutualInf
        };
      });
      let deleteList = currentModel.delete.map(x => {
        return {
          sourceId: x.sourceId,
          targetId: x.targetId,
          source: x.sourceId,
          target: x.targetId,
          mutualInf: x.mutualInf
        };
      });
      link = link.map(i => {
        return {
          from: i.sourceId,
          to: i.targetId,
          mutual: i.mutual
        };
      });
      for (let i of deleteList) {
        for (let j of link) {
          if (j.from === i.sourceId && j.to === i.targetId) {
            j.color = "red";
          }
        }
      }
      for (let i of addList) {
        link.push({
          from: i.sourceId,
          to: i.targetId,
          color: "#00FF7F",
          dash: [3]
        });
      }
      return {
        node: currentModel.nodeList,
        link
      };
    },
    transData(currentModel) {
      this.bic = currentModel.Bic;
      this.k2 = currentModel.K2;
      this.bdeu = currentModel.Bdeu;
      this.nodeList = currentModel.nodeList;
      this.addList = currentModel.add.map(x => {
        return {
          sourceId: x.sourceId,
          targetId: x.targetId,
          source: x.sourceId,
          target: x.targetId,
          mutualInf: x.mutualInf
        };
      });
      this.deleteList = currentModel.delete.map(x => {
        return {
          sourceId: x.sourceId,
          targetId: x.targetId,
          source: x.sourceId,
          target: x.targetId,
          mutualInf: x.mutualInf
        };
      });
      this.thirdModel = this.computeModel(currentModel);
    }
  },
  computed: {
    variables() {
      return this.nodeInfo.map(x => {
        return {
          name: x.nodeName,
          id: x.nodeId,
          valueNum: x.valueNum,
          values: x.value.map(y => {
            return {
              value: y.name,
              count: y.count,
              percent: y.probability.toFixed(3),
              id: y.id
            };
          })
        };
      });
    }
  },
  mounted() {
    this.transData(this.models[this.models.length - 1]);
  },
  watch: {
    models(to, from) {
      this.transData(to[to.length - 1]);
    },
    currentVariable(to, from) {
      if (to != null) {
        this.currentValues = this.variables[to].values;
      } else {
        this.currentValues = [];
      }
      let v = this.variables[to];
      for (let i of this.nodeList) {
        if (i.nodeId === v.id) {
          let parent = i.sequence;
          let cpt = i.CPT;
          let nodes = parent.map(x => this.idMap[x]);
          let infos = [""];
          if (nodes !== null) {
            for (let j of nodes) {
              let newInfos = [];
              for (let k of infos) {
                for (let z of j.value) {
                  newInfos.push(k + "<br>" + j.nodeName + "：" + z.name);
                }
              }
              infos = newInfos;
            }
          }
          let namedCpt = [];
          for (let i = 0; i < cpt.length; i++) {
            let temp = [];
            for (let j = 0; j < cpt[i].length; j++) {
              temp.push({
                info: v.name + "：" + v.values[j].value + infos[i],
                value: cpt[i][j]
              });
            }
            namedCpt.push(temp);
          }
          this.namedCpt = namedCpt;
          break;
        }
      }
      //   this.currentVariable = to
    }
  }
};
</script>
<style scoped>
.fit {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
}
.el-select {
  width: 200px;
}
.item {
  line-height: 23px;
  font-size: 12px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.13);
  padding: 9px 10px;
}
.variable {
  color: rgb(96, 98, 102);
  cursor: pointer;
}
.variable:hover {
  transition: background-color 0.25s ease;
  background-color: #ecf5ff;
}
</style>
