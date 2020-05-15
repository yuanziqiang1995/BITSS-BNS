<template>
  <div class="fit">
    <fourthLayout>
      <testgo :inference="inference" id="ddd" slot="left" :model="model" :idMap="idMap"></testgo>

      <div slot="right" style="display:flex;flex-direction:column;height:100%;">
        <div
          style="flex-grow:0;width:100%;color:rgb(144,147,153);cursor:pointer;font-weight:700"
          class="item"
        >
          <el-popover placement="left" title="说明" trigger="click">
            在下方设置证据后点击 底部的
            <span style="color:orange">进行推理</span> 以进行推理
            <br />推理结果以
            <span style="color:orange">条形图</span> 方式显示在每个网络图节点上
            <div slot="reference">
              推理&nbsp;
              <i class="el-icon-warning"></i>
            </div>
            <br />
            <span></span>
          </el-popover>
        </div>

        <div style="padding:15px;width:100%;flex-grow:1;overflow-y:auto">
          <div style="font-size:14px;margin:5px 0;">选择变量</div>

          <el-select
            size="small"
            v-model="evidenceList"
            style="width:100%"
            placeholder="请选择"
            multiple
          >
            <el-option
              v-for="(item,index) in nodeInfo"
              :key="index"
              :label="item.nodeName"
              :value="item.nodeId"
            ></el-option>
          </el-select>
          <div style="font-size:14px;margin:20px 0 0 0;">设置证据</div>
          <div
            style="font-size:14px;color:red;margin:10px 0"
            v-if="evidenceList.length === 0"
          >请先选择变量</div>
          <el-form size="small" ref="form" :model="evidence">
            <el-form-item
              :label="idMap[item].nodeName"
              v-for="(item,index) in evidenceList"
              :key="index"
            >
              <el-select
                size="small"
                v-model="evidence[idMap[item].nodeId]"
                style="width:100%"
                placeholder="请选择"
              >
                <el-option
                  v-for="(item,index) in idMap[item].value"
                  :key="index"
                  :label="item.name"
                  :value="index"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-form>
          <el-button
            type="primary"
            size="small"
            :loading="inferenceLoading"
            :disabled="evidenceList.length===0"
            @click="doInference"
          >进行推理</el-button>
        </div>
      </div>
      <div slot="buttons" style="z-index:1000;">
        <el-button-group>
          <el-button
            type="primary"
            style="z-index:1000;"
            size="small"
            v-if="inference===null&&inferenceData!==null"
            @click="inference = inferenceData"
          >显示推理结果</el-button>
          <el-button
            type="primary"
            style="z-index:1000;"
            size="small"
            v-if="inference"
            @click="inference = null"
          >显示条件概率</el-button>
          <el-button type="primary" style="z-index:1000;" size="small">保存网络</el-button>

          <el-button
            type="primary"
            style="z-index:1000;"
            size="small"
            @click="visVisible=true;"
          >条件概率可视化</el-button>
        </el-button-group>
      </div>
    </fourthLayout>

    <el-dialog title="条件概率可视化选项" :visible.sync="visVisible" :before-close="handleClose">
      <el-form ref="form" :model="form" label-width="120px">
        <el-form-item label="条件变量">
          <el-select
            v-model="needV"
            style="width:100%"
            :disabled="visLoading"
            multiple
            placeholder="请选择"
          >
            <el-option
              v-for="(item,index) in nodeInfo"
              :key="index"
              :label="item.nodeName"
              :value="item.nodeName"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="非条件变量">
          <el-select
            v-model="judgeV"
            style="width:100%"
            :disabled="visLoading"
            multiple
            placeholder="请选择"
          >
            <el-option
              v-for="(item,index) in nodeInfo"
              :key="index"
              :label="item.nodeName"
              :value="item.nodeName"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="模式">
          <el-select v-model="visibleType" style="width:100%" placeholder="请选择">
            <el-option
              v-for="(item,index) in visibleTypes"
              :key="index"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitVis" :loading="visLoading">提交</el-button>
      </span>
    </el-dialog>
    <el-dialog
      title="两两条件概率可视化(滚轮缩放，左键拖拽像素图)"
      :visible.sync="twoVisible"
      :before-close="handleClose"
    >
      <el-form ref="form" :model="form" label-width="120px">
        <el-form-item label="条件变量">
          <el-select v-model="needV" style="width:100%" disabled multiple placeholder="请选择">
            <el-option
              v-for="(item,index) in nodeInfo"
              :key="index"
              :label="item.nodeName"
              :value="item.nodeName"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="非条件变量">
          <el-select v-model="judgeV" style="width:100%" disabled multiple placeholder="请选择">
            <el-option
              v-for="(item,index) in nodeInfo"
              :key="index"
              :label="item.nodeName"
              :value="item.nodeName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <PixelMap id="twoPixelMap" :param="twoParam" style="width:100%;height:300px;" />

      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="twoVisible = false">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="多变量条件概率可视化" :visible.sync="multiVisible" :before-close="handleClose">
      <MultiPixelMap id="multiPixelMap" />
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="multiVisible = false">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="推理结果" :visible.sync="inferenceVisible" :before-close="handleClose">
      <Bar id="bar" :param="barParam" />
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="doInference">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import fourthLayout from "./fourthLayout";
import mynetwork from "./mynetwork";
import testgo from "./testgo";
import PixelMap from "@/components/visual/PixelMap";
import MultiPixelMap from "@/components/echarts/MultiPixelMap";
import Bar from "@/components/echarts/Bar";
// import {mapState} from 'vuex'

export default {
  name: "fourth",
  components: {
    fourthLayout,
    mynetwork,
    testgo,
    PixelMap,
    MultiPixelMap,
    Bar
  },
  props: {
    nodeInfo: {
      type: Array,
      default() {
        return [];
      }
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
    model: {
      type: Object,
      default: {
        node: [],
        link: []
      }
    }
  },
  data() {
    return {
      evidenceList: [],
      evidence: {},
      inference: null,
      inferenceData: null,
      barParam: {
        labels: [],
        series: []
      },
      inferenceLoading: false,
      inferenceVisible: false,
      multiVisible: false,
      twoParam: {},
      visLoading: false,
      visibleType: 0,
      visibleTypes: [
        {
          id: 0,
          name: "两两条件概率可视化"
        },
        {
          id: 1,
          name: "多变量条件概率可视化"
        }
      ],
      twoVisible: false,

      currentValues: [],
      currentVariable: null,
      judgeV: [],
      needV: [],
      visVisible: false,
      reason: {},
      form: {}
    };
  },
  mounted() {
    console.log(this.nodeInfo);
  },
  watch: {
    currentVariable(to, from) {
      console.log(to, this.currentVariable);
      if (to != null) {
        this.currentValues = this.variables[to].values;
      } else {
        this.currentValues = [];
      }
      //   this.currentVariable = to
    }
  },
  methods: {
    doInference() {
      let evidence = [];
      let nodeList = [];
      for (let i of this.evidenceList) {
        if (this.evidence[i]) {
          evidence.push(i);
          evidence.push(this.evidence[i]);
          let temp = this.idMap[i].value.map(x => 0.0);
          temp[this.evidence[i]] = 1.0;
          nodeList.push({
            nodeId: i,
            nodeName: i,
            probability: temp
          });
        }
      }
      if (evidence.length === 0) {
        const h = this.$createElement;
        this.$message({
          message: h("p", null, [h("span", null, "你没有设置证据")])
        });
        return;
      }
      this.inferenceLoading = true;
      this.$request
        .post("/bayes/network/inference", {
          evidence: evidence.join(","),
          edges: this.model.linkList
            .reduce((a, b) => {
              a.push(b.sourceId);
              a.push(b.targetId);
              return a;
            }, [])
            .join(","),
          nodes: this.model.nodeList.reduce((a, b) => {
            let item = this.idMap[b.nodeId];
            let temp = "";
            temp +=
              b.nodeId +
              "\t" +
              b.valueNum +
              "\t" +
              b.stringCPT +
              "\t" +
              b.sequence.join(",") +
              "\t";
            let first = true;
            for (let i of b.sequence) {
              if (first) {
                first = false;
              } else {
                temp += ",";
              }
              temp += this.idMap[i].valueNum;
            }
            temp += "\t";
            a += temp;
            return a;
          }, "")
        })
        .then(res => {
          for (let k = 0; k < res.data.data.length - 1; k++) {
            let i = res.data.data[k];
            let d = i
              .replace(/\[/g, "")
              .replace(/\]/g, "")
              .trim()
              .split(" ")
              .filter(x => {
                return x !== "";
              });
            let p = [];
            for (let j = 1; j < d.length; j++) {
              p.push(+d[j]);
            }
            nodeList.push({
              nodeId: d[0],
              nodeName: d[0],
              probability: p
            });
          }
          console.log(nodeList);
          let list = nodeList;
          this.inference = nodeList;
          this.inferenceData = this.inference;
          // let labels = list.map(x => x.nodeName);
          let len = list.reduce((a, b) => {
            return b.probability.length > a ? b.probability.length : a;
          }, 0);
          let series = [];
          for (let i = 0; i < len; i++) {
            series.push([]);
          }
          // console.log(this.inference)

          // for (let i of list) {
          //   let node = this.idMap[i.nodeId];
          //   for (let j = 0; j < len; j++) {
          //     if (j >= i.probability.length) {
          //       series[j].push({
          //         value: 0,
          //         info: ""
          //       });
          //     } else {
          //       series[j].push({
          //         value: i.probability[j],
          //         info: `<br>${node.value[j].name}\t：${i.probability[j]}`
          //       });
          //     }
          //   }
          // }
          // this.barParam = {
          //   labels: labels,
          //   series
          // }
          // console.log(series);

          // console.log(labels, len);
          // this.inferenceVisible = true
          this.inferenceLoading = false;
        });
    },
    submitVis() {
      let type = this.visibleType;
      let condition = ["1", "2"];
      let noncondition = ["3"];
            console.log(this.idMap)
      if (type === 0) {
        this.visLoading = true;

        let nonCondition = JSON.parse(JSON.stringify(this.judgeV));
        let condition = JSON.parse(JSON.stringify(this.needV));
        this.$request
          .postJSON("/bayes/network/single", {
            condition: this.needV,
            nonCondition: this.judgeV,
            linkList: this.model.linkList.map(x => {
              return {
                sourceId: this.idMap[x.sourceId].nodeName,
                targetId: this.idMap[x.targetId].nodeName
              };
            }),
            nodeList: this.model.nodeList.map(x => {
              return {
                nodeName: x.nodeName,
                valueNum: x.valueNum,
                id: x.stringCPT,
                sequence: x.sequence.map(y => {
                  return this.idMap[y].nodeName;
                })
              };
            })
          })
          .then(res => {
            let width = nonCondition.length;
            let data = [];
            let temp = [];
            let cnt = 0;
            for (let i of JSON.parse(res.data.data)) {
              if (cnt === width) {
                data.push(temp);
                temp = [];
                cnt = 0;
              }
              temp.push(i);
              cnt++;
            }
            data.push(temp);
            this.twoParam = {
              values: JSON.parse(res.data.data),
              condition: condition.map(x => {
                return {
                  name: x,
                  values: this.idMap[x].value.map(y => y.name)
                };
              }),
              noncondition: nonCondition.map(x => {
                return {
                  name: x,
                  values: this.idMap[x].value.map(y => y.name)
                };
              })
            };
            console.log(this.twoParam)
            this.twoVisible = true;
            this.visLoading = false;
          });
        // this.$axios
        //   .post("/network/vis", {
        //     condition: this.needV,
        //     nonCondition: this.judgeV,
        //     linkList:this.model.linkList.map(x => {
        //       return {
        //         sourceId: this.idMap[x.sourceId].nodeName,
        //         targetId: this.idMap[x.targetId].nodeName
        //       }
        //     }),
        //     nodeList:this.model.nodeList.map(x => {
        //       return {
        //         nodeName: x.nodeName,
        //         valueNum: x.valueNum,
        //         CPT: x.CPT,
        //         sequence: x.sequence.map(y => {
        //           return this.idMap[y].nodeName
        //         })
        //       }
        //     })
        //   })
        //   .then(res => {
        //     this.visLoading = false;
        //     let CPT = res.data.data.CPT;
        //     let values = [];
        //     for (let i = 0; i < condition.length; i++) {
        //       for (let j = 0; j < noncondition.length; j++) {
        //         let item = CPT[i][j];
        //         console.log(condition[i],this.idMap,this.idMap[condition[i]],'hh')
        //         values.push({
        //           x: j,
        //           y: i,
        //           w: item[0].length,
        //           h: item.length,
        //           vy: this.idMap[condition[i]].value.map(x => x.name),
        //           vx: this.idMap[noncondition[j]].value.map(x => x.name),
        //           p: item.flatMap(x => x)
        //         });
        //       }
        //     }
        //     let param = {
        //       condition: condition.map(x => this.idMap[x].nodeName),
        //       noncondition: noncondition.map(x => this.idMap[x].nodeName),
        //       values
        //     };
        //     this.twoParam = param;
        //     this.twoVisible = true;
        //   });
      } else {
        this.visLoading = true;
        this.multiVisible = true;
        this.visLoading = false;
      }
    },
    handleClose(done) {
      this.$confirm("确认关闭？")
        .then(_ => {
          done();
        })
        .catch(_ => {});
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