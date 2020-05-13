<template>
  <thirdLayout>
    <mynetwork id="ccc" slot="left" :treeLayout="true" :model="thirdModel" ref="thirdNetwork"></mynetwork>
    <div slot="buttons" style="z-index:1000;">
      <el-button-group>
        <el-button type="primary" style="z-index:1000;" size="small" @click="applySuggest">使用全部建议</el-button>
        <el-button type="primary" style="z-index:1000;" size="small" @click="submitNetwork">评估网络</el-button>
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
          <el-table :data="addList" size="small" style="width: 100%">
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
    <div slot="b2" style="height:100%;display:flex;flex-direction:column;">
      <div
        style="background:#f5f7fa;padding:0 5px;line-height:39px;flex-grow:0;display:block;width:100%;border-bottom:1px solid rgba(0,0,0,0.13)"
      >
        <span
          style="color:rgb(144,147,153);cursor:default;font-weight:700; line-height: 23px;
  "
        >当前评分</span>
        Bic: {{this.bic}} Bdeu: {{this.bdeu}} K2: {{this.k2}}
      </div>
      <div style="overflow-y:auto;width:100%;flex-grow:1;">
        <el-table :data="models" size="small" style="width: 100%">
          <el-table-column prop="time" label="时间"></el-table-column>
          <el-table-column prop="Bic" label="Bic"></el-table-column>
          <el-table-column prop="Bdeu" label="Bdeu"></el-table-column>
          <el-table-column prop="K2" label="K2"></el-table-column>

          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button size="small" @click="loadModel(scope.row)">载入</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </thirdLayout>
</template>
<script>
import echarts from "echarts";
import thirdLayout from "./thirdLayout";
import mynetwork from "./mynetwork";
import CellMap from "@/components/visual/CellMap";
// import {mapState} from 'vuex'

export default {
  name: "third",
  components: {
    thirdLayout,
    mynetwork,
    CellMap
  },
  data() {
    return {
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
      default () {
        return {}
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
      let link2 = links.filter(x => x.color !== "green");
      this.$request
        .post("/bayes/network/opt", {
          datasetId: this.$route.query.datasetId,
          edges: link2.reduce((a, b) => {
            a.push(this.idMap[b.sourceId].nodeName);
            a.push(this.idMap[b.targetId].nodeName);
            return a;
          }, []),
          nodes: this.nodeInfo.map(x => {
            return x.nodeName;
          })
        })
        .then(res => {
          console.log(res);
          let r = res.data.data;
          let info = r[0].split(",");
          let tempModel = JSON.parse(JSON.stringify(this.network.secondModel));
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
              sourceId: this.network.nameIdMap[t[0]],
              targetId: this.network.nameIdMap[t[1]],
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
              sourceId: this.network.nameIdMap[t[0]],
              targetId: this.network.nameIdMap[t[1]],
              mutualInf: (+t[2]).toFixed(3)
            };
          });

          let time = new Date();

          tempModel.time =
            time.getHours() + ":" + time.getMinutes() + ":" + time.getSeconds();
          this.network.thirdModels.push(tempModel);
          that.waiting = 0;
          that.activeStep = 2;
          that.currentHelpTab = "c" + that.activeStep;
        });

      this.$axios
        .post("/network/optimize", {
          datasetId: this.$route.query.datasetId,
          linkList: link2
        })
        .then(res => {
          res.data.data.link = link2.map(x => {
            return {
              sourceId: x.from,
              targetId: x.to
            };
          });
          let time = new Date();
          res.data.data.time =
            time.getHours() + ":" + time.getMinutes() + ":" + time.getSeconds();
          this.models.push(JSON.parse(JSON.stringify(res.data.data)));
        });
    },
    applySuggest() {
      let tempModel = this.$refs.thirdNetwork.getModel();
      console.log(tempModel);
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
          if (j.color === "green") {
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
          source: this.idMap[x.sourceId].nodeName,
          target: this.idMap[x.targetId].nodeName,
          mutualInf: x.mutualInf
        };
      });
      let deleteList = currentModel.delete.map(x => {
        return {
          sourceId: x.sourceId,
          targetId: x.targetId,
          source: this.idMap[x.sourceId].nodeName,
          target: this.idMap[x.targetId].nodeName,
          mutualInf: x.mutualInf
        };
      });
      link = link.map(i => {
        return {
          from: i.sourceId,
          to: i.targetId
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
          color: "green",
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
          source: this.idMap[x.sourceId].nodeName,
          target: this.idMap[x.targetId].nodeName,
          mutualInf: x.mutualInf
        };
      });
      this.deleteList = currentModel.delete.map(x => {
        return {
          sourceId: x.sourceId,
          targetId: x.targetId,
          source: this.idMap[x.sourceId].nodeName,
          target: this.idMap[x.targetId].nodeName,
          mutualInf: x.mutualInf
        };
      });
      this.thirdModel = this.computeModel(currentModel);
      console.log(this.thirdModel);
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
              percent: y.probability,
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
      console.log(v);
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
          console.log(this.namedCpt);
          break;
        }
      }
      //   this.currentVariable = to
    }
  }
};
</script>
<style scoped>
.echart {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
}
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