<template>
  <secondLayout>
    <mynetwork
      id="secondNetwork"
      :readOnly="true"
      slot="left"
      :model="secondModel"
      :treeLayout="true"
    ></mynetwork>

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
          <CellMap style="padding-top:20px;" id="secondPixelMap" :values="namedCpt" />
        </el-tab-pane>
      </el-tabs>
    </div>
  </secondLayout>
</template>
<script>
import secondLayout from "./secondLayout";
import mynetwork from "./mynetwork";
import _global from "./global.js";
import CellMap from "@/components/visual/CellMap";

export default {
  name: "second",
  components: {
    secondLayout,
    mynetwork,
    CellMap
  },
  props: {
    nodeInfo: {
      type: Array,
      default() {
        return [];
      }
    },
    idMap: {
      type: Object,
      default() {
        return {};
      }
    },
    model: {
      type: Object,
      default() {
        return {};
      }
    }
  },
  data() {
    return {
      namedCpt: [],
      currentValues: [],
      currentVariable: null,
      secondModel: {
        link: [],
        node: []
      }
    };
  },
  mounted() {
    this.modelChanged(this.model);
    if (_global.showReadonlyHint) {
      this.$msgbox({
        title: "提示",
        message: "本页面只读",
        showCancelButton: true,
        confirmButtonText: "本次不再提示",
        closeOnClickModal: false,
        cancelButtonText: "确定",
        beforeClose: (action, instance, done) => {
          if (action !== "confirm") {
            done();
          } else {
            _global.showReadonlyHint = false;
            done();
          }
        }
      });
    }
  },
  methods: {
    modelChanged(model) {
      this.secondModel = {
        link: model.linkList.map(x => {
          return {
            from: x.sourceId,
            to: x.targetId
          };
        }),
        node: model.nodeList
      };
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
  watch: {
    model(to, from) {
      this.modelChanged(to);
    },
    currentVariable(to, from) {
      if (to != null) {
        this.currentValues = this.variables[to].values;
      } else {
        this.currentValues = [];
      }
      let v = this.variables[to];
      for (let i of this.model.nodeList) {
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
