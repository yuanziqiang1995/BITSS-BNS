<template>
  <drawLayout style="height:calc(100vh - 50px)">
    <drawNetwork
      @selection-changed="selectionChanged"
      id="drawNetwork"
      ref="drawNetwork"
      slot="left"
      :model="model"
    ></drawNetwork>
    <div slot="buttons" style="z-index:1000;">
      <el-button-group>
        <el-button type="primary" style="z-index:1000;" size="small" @click="resetNetwork">新的网络</el-button>
        <el-button type="primary" style="z-index:1000;" size="small" @click="onSave">保存网络</el-button>
      </el-button-group>
      <el-dialog title="保存网络" :visible.sync="dialogFormVisible" v-dialogDrag>
        <el-form ref="dataForm" :model="uploadData" label-position="right" label-width="110px">
          <el-form-item label="节点数 :">{{uploadData.nodeCount}}</el-form-item>
          <el-form-item label="连线数 :">{{uploadData.linkCount}}</el-form-item>
          <el-form-item label="网络名称 :">
            <el-input v-model="uploadData.name" style="max-width:400px"></el-input>
          </el-form-item>
          <el-form-item label="网络描述 :">
            <el-input
              v-model="uploadData.description"
              type="textarea"
              :rows="3"
              style="max-width:400px"
              maxlength="250"
            ></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" :loading="saveLoading" @click="handleSave()">提交保存</el-button>
        </div>
      </el-dialog>
    </div>
    <div slot="infos" style="z-index:1000;">
      <el-button-group>
        <el-button type="success" style="z-index:1000;" size="small" @click="openHelpDialog">操作说明</el-button>
      </el-button-group>
    </div>
    <div slot="right" style="width:100%;height:100%;display:flex;flex-direction:column;">
      <div
        style="flex-grow:0;color:rgb(144,147,153);cursor:default;font-weight:700"
        class="item"
      >节点信息</div>
      <div style="flex-grow:1;overflow-y:scroll;width:100%;height:0;margin-top:20px;">
        <div
          style="color:red;margin-left:10px;font-size:14px;"
          v-if="!currentNode && !linkInfo"
        >未选择节点</div>
        <el-form
          size="mini"
          v-if="currentNode"
          ref="dataForm"
          :model="currentNode"
          label-position="right"
          label-width="90px"
        >
          <el-form-item label="节点id">
            <el-input v-model="nodeId" disabled></el-input>
          </el-form-item>
          <el-form-item label="节点名称">
            <el-input v-model="nodeName" disabled></el-input>
          </el-form-item>
          <!-- <el-form-item label="节点描述">
            <el-input v-model="currentNode.description"></el-input>
          </el-form-item>-->
          <el-form-item label="取值个数">
            <el-input-number v-model="currentNode.valueNum" :min="1" :step="1" :precision="0"></el-input-number>
            <!-- <el-input v-model="currentNode.valueNum" :min='1' @keyup.native='onNumInput' type='number' :step='1'></el-input> -->
          </el-form-item>
          <el-form-item label="cpt">
            <el-input
              v-model="currentNode.cpt"
              placeholder="二维数组，例如:
             [ [0.2,0.4,0.4],
              [0.5,0.3,0.2] ]"
              type="textarea"
              :rows="4"
              maxlength="250"
            ></el-input>
          </el-form-item>
        </el-form>
        <el-form
          size="mini"
          v-if="linkInfo"
          ref="linkInfo"
          :model="linkInfo"
          label-position="right"
          label-width="90px"
        >
          <el-form-item label="源ID">
            <el-input v-model="linkInfo.from" disabled></el-input>
          </el-form-item>
          <el-form-item label="目标ID">
            <el-input v-model="linkInfo.to" disabled></el-input>
          </el-form-item>
          <el-form-item label="连线信息">
            <el-input v-model="linkInfo.info" disabled></el-input>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </drawLayout>
</template>
<script>
import drawLayout from "./drawLayout";
import drawNetwork from "./drawNetwork";

export default {
  name: "first",
  components: {
    drawLayout,
    drawNetwork
  },
  mounted() {
    this.networkId = this.$route.query.modelId;
    if (this.networkId) {
      this.pageLoading = true;
      this.$request
        .get("/bayes/static/discrete/loadModel?modelId=" + this.networkId)
        .then(res => {
          this.pageLoading = false;
          let temp = JSON.parse(res.data.data);
          this.model = {
            link: temp.linkList,
            node: temp.nodeList
          };
          this.uploadData.name = temp.modelName;
          this.uploadData.description = temp.description;
          for (let i of temp.nodeList) {
            this.nodeMap[i.id] = {
              cpt: i.stringCPT,
              valueNum: i.valueNum
            };
          }
          this.hasName = true;
        });
    }
    setTimeout(() => {
      this.openHelpDialog();
    }, 1000);
  },
  props: {
    nodeInfo: {
      type: Array,
      default() {
        return [];
      }
    }
  },
  data() {
    return {
      model: {},
      pageLoading: false,
      saveLoading: false,
      uploadData: {
        description: "",
        name: ""
      },
      dialogFormVisible: false,
      helpVisible: true,
      currentNode: null,
      nodeMap: {},
      nodeId: "",
      nodeName: "",
      network: null,
      networkId: null,
      linkInfo: null,
      modelId: null,
      hasName: false
    };
  },
  computed: {
    model() {
      return {
        node: this.nodeInfo,
        link: []
      };
    }
  },
  methods: {
    handleSave() {
      let id = 1;
      let param = {
        modelId: this.networkId,
        description: this.uploadData.description,
        modelName: this.uploadData.name,
        nodeList: this.network.nodeList,
        linkList: this.network.linkList.map(x => {
          return {
            sourceId: x.from,
            targetId: x.to,
            label: x.label,
            id: id++
          };
        })
      };
      this.saveLoading = true;
      this.$request.postJSON("/bayes/static/discrete/save", param).then(res => {
        this.networkId = res.data.data;
        this.saveLoading = false;
        this.dialogFormVisible = false;
      });
    },
    onSave() {
      let network = this.getNetwork();
      if (network.code === 0) {
        this.$alert(network.msg, "提示", {
          confirmButtonText: "确定",
          callback: action => {}
        });
        return;
      }
      this.network = network;
      this.dialogFormVisible = true;
      if (!this.hasName) {
        this.uploadData.name = "网络-" + this.getDate();
      }
      this.uploadData.nodeCount = network.nodeList.length;
      this.uploadData.linkCount = network.linkList.length;
    },
    resetNetwork() {
      this.$confirm("此操作将清除当前页面的网络, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          if (this.hasName) {
            this.$router.push({ path: "/draw/index" });
          }
          this.$refs.drawNetwork.reset();
          this.uploadData = {};
          this.currentNode = null;
          this.nodeMap = {};
          this.networkId = null;
          this.nodeId = "";
          this.nodeName = "";
          this.network = null;
        })
        .catch(() => {});
    },
    getDate() {
      let d = new Date();
      return (
        d.getFullYear() +
        "/" +
        d.getMonth() +
        "/" +
        d.getDay() +
        "-" +
        d.getHours() +
        ":" +
        d.getMinutes() +
        ":" +
        d.getSeconds()
      );
    },
    getNetwork() {
      let model = this.getModel();
      let nodeList = [];
      for (let i of model.node) {
        let info = this.nodeMap[i.nodeId];
        if (!info) {
          return {
            code: 0,
            msg: `节点${i.nodeId}-${i.nodeName}：未填写数据`
          };
        }
        if (!(info.valueNum && info.cpt)) {
          return {
            code: 0,
            msg: `节点${i.nodeId}-${i.nodeName}：未填写数据`
          };
        }
      }
      for (let i of model.node) {
        let info = this.nodeMap[i.nodeId];

        let sequence = [];
        let typeCount = 1;
        for (let j of model.link) {
          if (j.to === i.nodeId) {
            sequence.push(j.from);

            typeCount *= this.nodeMap[j.from].valueNum;
          }
        }

        let cpt;
        try {
          cpt = JSON.parse(info.cpt);
          if(sequence.length === 0){
            cpt = [cpt]
          } else {
            for(let i =0;i<sequence.length - 1;i++){
              cpt = cpt.flatMap(x=>x)
            }
          }
        } catch (e) {
          return {
            code: 0,
            msg: `节点${i.nodeId}-${i.nodeName}：cpt格式错误`
          };
        }
        if (cpt.length != typeCount) {
          return {
            code: 0,
            msg: `节点${i.nodeId}-${i.nodeName}：cpt数组长度与父节点不对应`
          };
        }
        for (let j of cpt) {
          if (j.length != info.valueNum) {
            return {
              code: 0,
              msg: `节点${i.nodeId}-${i.nodeName}：cpt数组长度与自身取值个数不对应`
            };
          }
        }
        let vs = []
        for(let i = 0;i<+info.valueNum;i++){
          vs.push("取值"+i)
        }
        nodeList.push({
          id: i.nodeName,
          t: i.nodeId,
          nodeName: i.nodeName,
          valueNum: info.valueNum,
          stringCPT: info.cpt,
          values: vs,
          CPT: cpt,
          sequence
        });
      }
      let idMap = {}
      for(let i of nodeList){
        idMap[i.t] = i
      }
      for(let i of nodeList){
        i.sequence = i.sequence.map(x => idMap[x].nodeName)
      }
      return {
        code: 1,
        nodeList,
        linkList: model.link.map(x => {
          return {
            from: idMap[x.from].nodeName,
            to: idMap[x.to].nodeName,
            label: x.label
          }
        })
      };
    },
    openHelpDialog() {
      const h = this.$createElement;
      this.$msgbox({
        title: "操作说明",
        message: h(
          "el-form",
          { props: { "label-width": "90px", size: "mini" } },
          [
            h("el-form-item", { props: { label: "创建节点：" } }, "双击空白处"),
            h(
              "el-form-item",
              { props: { label: "删除节点：" } },
              "选择节点按下 delete"
            ),
            h(
              "el-form-item",
              { props: { label: "节点连线：" } },
              "鼠标移至节点边缘（鼠标变为手），按下左键并移动进行连线"
            ),
            h(
              "el-form-item",
              { props: { label: "删除连线：" } },
              "选择连线按下 delete"
            ),
            h(
              "el-form-item",
              { props: { label: "节点命名：" } },
              "双击节点文本"
            ),
            h(
              "el-form-item",
              { props: { label: "连线命名：" } },
              "双击连线的中间"
            ),
            h(
              "el-form-item",
              { props: { label: "修改属性：" } },
              "选择节点，在右边区域修改属性"
            )
          ]
        ),
        confirmButtonText: "确定",
        beforeClose: (action, instance, done) => {
          done();
        }
      }).then(action => {});
    },
    selectionChanged(data) {
      if (data.hasOwnProperty("from")) {
        this.currentNode = null;
        this.linkInfo = {
          from: data.from,
          to: data.to,
          info: data.label
        };
        return;
      }
      this.linkInfo = null;
      if (this.nodeMap[data.key]) {
      } else {
        this.nodeMap[data.key] = {};
      }
      this.currentNode = this.nodeMap[data.key];
      this.nodeId = data.key;
      this.nodeName = data.text;

    },
    getModel() {
      return this.$refs.drawNetwork.getModel();
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
