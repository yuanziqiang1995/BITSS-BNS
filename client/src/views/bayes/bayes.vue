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
          >功能说明</el-button>
          <el-button
            icon="el-icon-menu"
            style="z-index:1000;"
            title="查看原始数据"
            size="small"
            @click='dataVisible=true;'
            type="success"
          >数据</el-button>
        </el-button-group>
      </div>
    </bayesLayout>
    <el-dialog :visible.sync="helpVisible">
      <el-tabs slot="title" v-model="currentHelpTab" @tab-click="handleClick">
        <el-tab-pane label="添加先验知识" name="c0">
          <ul style="padding-inline-start:0;">
            <ol>
              <h4>1) 编辑先验知识</h4>
              <p>可以手动编辑节点间的连线：将鼠标移至某节点边缘，当指针变为手指时进行按住左键并移到另一个变量完成连接</p>
              <p>
                完成先验知识编辑后，点击
                <span class="emphasize">下一步</span>
                进行网络初始化
              </p>
            </ol>
            <ol>
              <h4>2) 查看节点信息</h4>
              <p>点击右边的节点列表以查看节点样本数据分布</p>
            </ol>
          </ul>
        </el-tab-pane>
        <el-tab-pane label="生成初始网络" name="c1">
          <ul style="padding-inline-start:0;">
            <ol>
              <h4>1) 查看网络结构</h4>
              <p>
                本页网络
                <span class="emphasize">只读</span>
                ，可以进行节点与连线的拖拽
              </p>
              <p>
                查看完成后，点击
                <span class="emphasize">下一步</span>
                进行网络优化
              </p>
            </ol>
            <ol>
              <h4>2) 查看节点信息</h4>
              <p>在右边选择变量后可以查看其样本数据分布与条件概率表</p>
            </ol>
          </ul>
        </el-tab-pane>
        <el-tab-pane label="网络优化" name="c2">
          <ul style="padding-inline-start:0;">
            <ol>
              <h4>1) 优化网络结构</h4>
              <p>
                点击
                <span class="emphasize">应用建议</span>
                会删除红色边，连接绿色边
              </p>
              <p>点击网络中的某个节点可以查看与其他节点的互信息</p>
              <p>
                点击
                <span class="emphasize">提交评估</span>
                会得到三种评分与优化删除的建议，以表格形式显示在下方
              </p>
              <p>
                点击
                <span class="emphasize">历史操作的载入</span>
                会将对应的网络结构载入为当前网络
              </p>
              <p>
                点击
                <span class="emphasize">下一步</span>
                进入网络推理页面
              </p>
            </ol>
            <ol>
              <h4>2) 查看节点信息</h4>
              <p>在右边选择变量后可以查看其样本数据分布与条件概率表</p>
            </ol>
          </ul>
        </el-tab-pane>
        <el-tab-pane label="网络推理" name="c3">
          <ul style="padding-inline-start:0;">
            <ol>
              <h4>1) 网络推理</h4>
              <p>
                在右边设置推理目标与证据集后，点击
                <span class="emphasize">推理</span>
                进行网络推理，结果会以条形图显示在网络图中
              </p>
            </ol>
            <ol>
              <h4>2) 条件概率可视化</h4>
              <p>
                点击
                <span class="emphasize">条件概率可视化</span>
                打开弹窗，填写表达进行条件概率可视化
              </p>
            </ol>
            <ol>
              <h4>3) 保存网络</h4>
              <p>
                点击
                <span class="emphasize">保存网络</span>
                在弹窗进行网络结构的保存
              </p>
            </ol>
          </ul>
        </el-tab-pane>
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="helpVisible = false">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog :visible.sync="dataVisible" id='dataDialog' title="查看数据集">
      <div>
      <el-table
        :data="list"
        v-loading="listLoading"
        element-loading-text="给我一点时间"
        border
        size="mini"
      >
        <el-table-column v-for='(item,index) in dataheader' align="center" :label='item' :prop='item' :key='index'/>
      </el-table>
      </div>
      <div slot="footer" align="left">
        <el-pagination
          background
          pager-count='4'
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="page"
          :page-sizes="[10,20,30, 50]"
          small
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        ></el-pagination>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import bayesLayout from "./bayesLayout";
import first from "./first";
import second from "./second";
import third from "./third";
import fourth from "./fourth";

export default {
  name: "bayes",
  components: {
    bayesLayout,
    first,
    second,
    third,
    fourth
  },
  data() {
    return {
      dataVisible: false,
      dataheader: [],
      listLoading: false,
      list:[],
      page: 1,
      pageSize: 10,
      total: 0,
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
    this.queryData(1)
    if (modelId) {
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
              let ttt = [];
              for (let i = 0; i < x.values.length; i++) {
                ttt.push({
                  id: x.values[i],
                  name: x.values[i],
                  probability: x.probability[i],
                  count: x.counts[i]
                });
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
          loading.close();
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
    handleSizeChange(val) {
      this.pageSize = val;
      this.queryData(1);
    },
    handleCurrentChange(val) {
      this.page = val;
      this.queryData(val);
    },
    queryData(page) {
      this.page = page;
      this.listLoading = true;
      this.$request
        .get(
          `/bayes/network/data?datasetId=${this.$route.query.datasetId}&page=${page}&pageSize=${this.pageSize}`
        )
        .then(res => {
          this.listLoading = false;
          this.total = +res.data.data[0];
          this.dataheader = res.data.data[1].split(",");
          let t = [];
          for (let i = 2; i < res.data.data.length; i++) {
            let v = res.data.data[i].split(",")
            let o = {}
            for(let j = 0;j<v.length;j++){
              o[this.dataheader[j]] = v[j]
            }
            t.push(o);
          }
          console.log(t)
          this.list = t;
        });
    },
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
ul > ol {
  margin: 0;
  padding-left: 0px;
}
ul > ol > h4 {
  color: #444;
  font-weight: 600;
}
.emphasize {
  color: orange;
}
ul > ol > p {
  padding: 0;
  margin: 10px;
}
.fit {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
}
</style>
<style>
#dataDialog > .el-dialog{
  min-width:550px;
}
#dataDialog .el-dialog__body{
  padding:0px 20px;
}
</style>
