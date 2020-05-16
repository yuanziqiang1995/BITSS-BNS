<template>
  <div class="fit" id="bayesContainer" style="height:calc(100vh - 50px)">
    <bayesLayout>
      <div slot="top" style="display:flex;flex-direction:row">
        <div style="display:inline-block;flex-grow:1;width:0px;">
          <el-steps :active="activeStep" simple>
            <el-step v-for="(item,index) of steps" :key="index" :title="item">
              <div slot="icon">{{index+1}}</div>
            </el-step>
          </el-steps>
        </div>
        <div style="display:inline-block;flex-grow:0;margin:0 10px;line-height:40px;">
          <el-button-group>
            <el-button
              size="small"
              @click="lastStep"
              type="primary"
              :loading="waiting===2"
              icon="el-icon-arrow-left"
              :disabled="lastStepDisabled"
            >上一步</el-button>
            <el-button
              size="small"
              :loading="waiting === 1"
              :disabled="nextStepDisabled"
              type="primary"
              icon="el-icon-arrow-right"
              @click="nextStep()"
            >下一步</el-button>
          </el-button-group>
        </div>
      </div>
      <div class="fit" slot="bottom">
        <first v-if="activeStep===0" :nodeInfo="network.nodeInfo" ref="firstStep"></first>
        <second
          v-else-if="activeStep===1"
          :nodeInfo="network.nodeInfo"
          :model="network.secondModel"
          :idMap="network.idMap"
        ></second>
        <third
          v-else-if="activeStep===2"
          :nodeInfo="network.nodeInfo"
          :models="network.thirdModels"
          :nameIdMap="network.nameIdMap"
          :idMap="network.idMap"
        ></third>
        <fourth
          v-else-if="activeStep === 3"
          :idMap="network.idMap"
          :nodeInfo="network.nodeInfo"
          :model="network.fourthModel"
          :nameIdMap="network.nameIdMap"
        ></fourth>
        <!-- <testgo></testgo> -->
        <!-- <mynetwork></mynetwork> -->
      </div>
      <div slot="icons" style="z-index:1000;">
        <el-button-group>
          <el-button
            icon="el-icon-warning"
            style="z-index:1000;"
            title="查看帮助"
            size="small"
            type="success"
            @click="helpVisible = true;"
          >说明</el-button>
          <el-button
            icon="el-icon-menu"
            style="z-index:1000;"
            title="查看原始数据"
            size="small"
            type="success"
          >数据</el-button>
        </el-button-group>
      </div>
    </bayesLayout>
    <el-dialog :visible.sync="helpVisible">
      <el-tabs slot="title" v-model="currentHelpTab" @tab-click="handleClick">
        <el-tab-pane label="添加先验知识" name="c0">
          本页面进行网络图先验知识的编辑，用户可在该页面
          <span style="color:orange">连接变量</span>作为先验知识。
          连接说明：
          <ul>
            <li>1. 左键点击图中一个变量</li>
            <li>2. 鼠标移到该变量的边缘</li>
            <li>3. 当指针变为手指时进行按住左键并移到另一个变量完成连接</li>
          </ul>
        </el-tab-pane>
        <el-tab-pane label="生成初始网络" name="c1">
          本页面
          <span style="color:orange">只读</span>，
        </el-tab-pane>
        <el-tab-pane label="网络优化" name="c2">角色管理</el-tab-pane>
        <el-tab-pane label="网络推理" name="c3">定时任务补偿</el-tab-pane>
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="helpVisible = false">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import echarts from "echarts";
import bayesLayout from "./bayesLayout";
import first from "./first";
import second from "./second";
import third from "./third";
import fourth from "./fourth";
import mynetwork from "./mynetwork";
import testgo from "./testgo";
// import {mapState} from 'vuex'

export default {
  name: "bayes",
  components: {
    testgo,
    bayesLayout,
    first,
    second,
    third,
    mynetwork,
    fourth
  },
  data() {
    return {
      steps: ["添加先验知识", "生成初始网络", "网络优化", "网络推理"],
      activeStep: 0,
      waiting: 0,
      helpVisible: false,
      currentHelpTab: "c0",
      network: {
        nodeInfo: [],
        secondModel: {
          nodeList: [],
          linkList: []
        },
        firstModel: {},
        thirdModels: [],
        idMap: {},
        nameIdMap: {},
        fourthModel: {
          node: [],
          link: []
        }
      }
    };
  },
  mounted() {
    const loading = this.$loading({
      lock: true,
      text: "Loading",
      spinner: "el-icon-loading",
      background: "rgba(0, 0, 0, 0.7)",
      target: document.querySelector("#bayesContainer")
    });
    let datasetId = this.$route.query.datasetId;
    let modelId = this.$route.query.modelId;
    if(modelId){
      this.$request
        .get("/bayes/static/discrete/loadModel?modelId=" + modelId)
        .then(res => {
          this.activeStep = 1;
          this.currentHelpTab = "c" + 1;
          let temp = JSON.parse(res.data.data);
          this.datasetId = temp.datasetId;
          this.network.secondModel = {
            linkList: temp.linkList,
            nodeList: temp.nodeList.map(x => {
              let ttt = []
              for(let i =0;i<x.values.length;i++){
                ttt.push({
                  id: x.values[i],
                  name: x.values[i],
                  probability: x.probability[i],
                  count: x.counts[i]
                })
              }
              this.network.nodeInfo.push({
                nodeId: x.id,
                nodeName: x.nodeName,
                valueNum: +x.valueNum,
                value: ttt
              });
              return {
                nodeId: x.id,
                nodeName: x.nodeName,
                sequence: x.sequence,
                CPT: x.cPT,
                stringCPT: x.stringCPT,
                valueNum: +x.valueNum
              };
            })
          };
          loading.close()
          console.log(this.network.idMap,this.network.secondModel)
          for (let i of this.network.nodeInfo) {
            this.network.idMap[i.nodeId] = i;
          }
        });
    } else {
    this.$request
      .get("/bayes/network/init?datasetId=" + datasetId)
      .then(res => {
        res.data.data;
        let info = "";
        let vals = [];
        let flag = true;
        let nodeList = [];
        for (let i of res.data.data) {
          if (flag) {
            info = i;
            flag = false;
            vals = [];
          } else {
            if (i !== "") {
              vals.push(i);
            } else {
              flag = true;
              let infoSplit = info.split("\t");
              let o = {
                nodeId: infoSplit[1],
                nodeName: infoSplit[1],
                valueNum: +infoSplit[2],
                value: vals.map(x => {
                  let t = x.split("\t");
                  return {
                    id: t[0],
                    name: t[1],
                    count: +t[2],
                    probability: +t[3]
                  };
                })
              };
              nodeList.push(o);
            }
          }
        }
        this.network.nodeInfo = nodeList;
        let idMap = {};
        let nameIdMap = {};
        for (let i of nodeList) {
          idMap[i.nodeId] = i;
          nameIdMap[i.nodeName] = i.nodeId;
        }
        // this.network.nameIdMap = nameIdMap;
        this.network.idMap = idMap;
        loading.close();
      });
    }
  },
  methods: {
    nextStep() {
      this.waiting = 1;
      let that = this;
      switch (that.activeStep) {
        case 0: {
          let tempModel = this.$refs.firstStep.getModel();
          this.network.firstModel = tempModel;
          this.$request
            .post("/bayes/network/learn", {
              datasetId: this.$route.query.datasetId,
              edges: tempModel.link.reduce((a, b) => {
                a.push(b.from);
                a.push(b.to);
                return a;
              }, [])
            })
            .then(res => {
              let d = res.data.data;
              let edges = JSON.parse(
                d[0]
                  .replace(/"\(/g, "")
                  .replace(/\)"/g, "")
                  .replace(/'/g, '"')
              );
              let nodeList = [];

              for (let i = 1; i < d.length; i++) {
                let t = d[i].split("\t");
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
                  CPT: cpt,
                  sequence,
                  stringCPT
                });
              }
              let linkList = [];
              for (let i = 0; i < edges.length; i += 2) {
                linkList.push({
                  linkId: i,
                  sourceId: edges[i],
                  targetId: edges[i + 1]
                });
              }
              that.waiting = 0;
              that.network.secondModel = {
                linkList,
                nodeList
              };
              console.log(that.network.secondModel);
              that.activeStep = 1;
              that.currentHelpTab = "c" + that.activeStep;
            });
          break;
        }
        case 1: {
          let links = this.network.secondModel.linkList;
          this.$request
            .post("/bayes/network/opt", {
              datasetId: this.$route.query.datasetId,
              edges: links.reduce((a, b) => {
                a.push(b.sourceId);
                a.push(b.targetId);
                return a;
              }, []),
              nodes: this.network.secondModel.nodeList.map(x => {
                return x.nodeName;
              })
            })
            .then(res => {
              console.log(res);
              let r = res.data.data;
              let info = r[0].split(",");
              let tempModel = JSON.parse(
                JSON.stringify(this.network.secondModel)
              );
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
              console.log(tempModel, "model");
              let time = new Date();

              tempModel.time =
                time.getHours() +
                ":" +
                time.getMinutes() +
                ":" +
                time.getSeconds();
              this.network.thirdModels.push(tempModel);
              that.waiting = 0;
              that.activeStep = 2;
              that.currentHelpTab = "c" + that.activeStep;
            });
          break;
        }
        case 2: {
          that.network.fourthModel =
            that.network.thirdModels[that.network.thirdModels.length - 1];
          that.activeStep = 3;
          that.waiting = 0;
          that.currentHelpTab = "c" + that.activeStep;
          break;
        }
        case 3: {
          alert("推出");
          break;
        }
        default: {
          alert("错误");
          break;
        }
      }
    },
    lastStep() {
      this.waiting = 2;
      let that = this;
      setTimeout(() => {
        that.waiting = 0;
        switch (that.activeStep) {
          case 0: {
            break;
          }
          case 1: {
            that.activeStep = 0;
            break;
          }
          case 2: {
            that.activeStep = 1;
            break;
          }
          case 3: {
            that.activeStep = 2;
            break;
          }
          default: {
            alert("错误");
            break;
          }
        }
        that.currentHelpTab = "c" + that.activeStep;
      }, 0);
    }
  },
  computed: {
    lastStepDisabled() {
      return this.activeStep === 0 || this.waiting === 1;
    },
    nextStepDisabled() {
      return this.activeStep === 3 || this.waiting === 2;
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
</style>
<style>
.el-steps--simple {
  background: transparent;
}
.el-step.is-simple:not(:last-of-type) .el-step__title {
  max-width: none;
}
</style>
