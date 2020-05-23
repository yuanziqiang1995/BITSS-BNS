<template>
  <div class="app-container calendar-list-container">
    <div>
      <div style="margin-bottom: 10px;margin-top: 0px">
        <el-upload
          class="upload-demo"
          ref="upload"
          :multiple="false"
          :auto-upload="false"
          :on-change="onFileChange"
          :show-file-list="false"
          :before-remove="beforeRemove"
          :http-request="submitUpload"
          action="/upload/teacherInfo"
          :limit="1"
        >
          <el-button icon="el-icon-upload" slot="trigger" size="small" type="primary">上传数据集</el-button>
        </el-upload>
      </div>
      <el-table
        :data="list"
        v-loading="listLoading"
        element-loading-text="给我一点时间"
        border
        size='mini'
      >
        <el-table-column type="selection" width="40" />
        <el-table-column align="center" label width="65" type="index" :index="indexMethod" />
        <el-table-column align="center" label="数据集名称" prop="name" />
        <el-table-column align="center" label="数据集描述" prop="description" />
        <el-table-column align="center" label="数据集格式" prop="format" />
        <el-table-column align="center" label="上传时间" prop="updateTime" />
        <el-table-column
          align="center"
          label="操作"
          width="300"
          class-name="small-padding fixed-width"
        >
          <template slot-scope="scope">
            <el-button
              size="small"
              type="primary"
              @click="handleEdit(scope.row)"
            >编辑</el-button>
            <el-button
              size="small"
              type="primary"
              @click="handleDownload(scope.row)"
              style="margin-left:10px"
            >下载</el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
              style="margin-left:10px"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="page"
          :page-sizes="[10,20,30, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        ></el-pagination>
      </div>
    </div>
    <el-dialog title="编辑数据集" :visible.sync="editFormVisible">
      <el-form
        ref="dataForm"
        :model="editDatasets"
        label-position="right"
        label-width="110px"
      >
        <el-form-item label="数据名称 :">
          <el-input v-model="editDatasets.name"  style="max-width:400px"></el-input>
        </el-form-item>
        <el-form-item label="数据格式 :">
          <el-input v-model="editDatasets.format" style="max-width:400px"></el-input>
        </el-form-item>
        <el-form-item label="数据描述 :">
          <el-input
            v-model="editDatasets.description"
            type="textarea"
            :rows="2"
            style="max-width:400px"
            maxlength="250"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitEdit">提交修改</el-button>
      </div>
    </el-dialog>

    <el-dialog title="上传数据集" :visible.sync="uploadFormVisible" v-dialogDrag>
      <el-form
        ref="uploadForm"
        :model="uploadDatasets"
        label-position="right"
        label-width="90px"
      >
        <el-form-item label="文件 :">
          <el-input v-model="fileName" disabled style="max-width:400px"></el-input>
        </el-form-item>
        <el-form-item label="数据名称 :">
          <el-input v-model="uploadDatasets.name" style="max-width:400px"></el-input>
        </el-form-item>
        <el-form-item label="数据格式 :">
          <el-input v-model="uploadDatasets.format" style="max-width:400px"></el-input>
        </el-form-item>
        <el-form-item label="数据描述 :">
          <el-input
            v-model="uploadDatasets.description"
            type="textarea"
            :rows="2"
            style="max-width:400px"
            maxlength="250"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleUpload()">上传</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
//import { fetchDataList,fetchDataTypeList , fetchDataFormatList,download} from '@/api/data'
import waves from "@/directive/waves"; // 水波纹指令
import { parseTime, arrayToTree1 } from "@/utils";
//import { fetchFileInfo,downloadFile} from '@/api/file'

export default {
  directives: {
    waves
  },
  data() {
    return {
      file: null,
      editFormVisible: false,
      fileName: "",
      fileList: [],
      page: 1,
      uploadFormVisible: false,
      pageSize: 20,
      tableKey: 0,
      fullHeight: document.documentElement.clientHeight,
      list: [],
      total: 0,
      uploadDatasets: {
        name: "",
        format: "",
        description: ""
      },
      editDatasets: {
        name: "",
        format: "",
        description:""
      },
      listLoading: true,
      listQuery: {
        pages: 1,
        rows: 10,
        category: "",
        name: "",
        format: "",
        formatId: "",
        categoryId: ""
      },
      dataTypeData: [],
      defaultProps: {
        children: "children",
        label: "label"
      },
      dataformatOptions: [],
      temp: {
        format: "",
        category: "",
        dataset: {
          name: "",
          description: ""
        }
      },
      tempDown: {
        datasetId: "",
        formatId: "",
        categoryId: "",
        name: ""
      },
      dialogFormVisible: false,
      dataTypeDialogFormVisible: false,
      dialogStatus: "",
      multipleSelection: [],
      downloadLoading: false,
      r: ""
    };
  },
  filters: {
    formatTimes(updateDate) {
      const y = new Date(updateDate).getFullYear();
      const m = new Date(updateDate).getMonth() + 1;
      const d = new Date(updateDate).getDate();
      const h = new Date(updateDate).getHours();
      const i = new Date(updateDate).getMinutes();
      const s = new Date(updateDate).getSeconds();
      var t = y + "-" + m + "-" + d + " " + h + ":" + i + ":" + s;
      // var t = Y + '-' + m + '-' + d;
      return t;
    }
  },
  mounted() {
    this.queryData(1);
    const that = this;
    window.onresize = () => {
      return (() => {
        window.clientHeight = document.body.clientHeight;
        that.fullHeight = window.clientHeight;
      })();
    };
  },
  watch: {
    fileList(to, from) {
      if (to.length > 0) {
        this.uploadFormVisible = true;
      }
    },
    fullHeight(val) {
      // if(!this.timer) {
      this.fullHeight = val;
      //   this.timer = true
      //   let that = this
      //   setTimeout(function (){
      //     that.timer = false
      //   },400)
      // }
    }
  },
  methods: {
    onFileChange(file, fileList) {
      if (fileList.length > 0) {
        this.fileList = fileList;
        this.file = fileList[0];
        this.fileName = fileList[0].name;
        this.uploadDatasets.name = this.fileName;
        this.uploadDatasets.format = this.fileName.substr(
          this.fileName.lastIndexOf(".") + 1
        );
        this.uploadFormVisible = true;
      }
    },
    queryData(page) {
      this.page = page
      this.listLoading = true;
      this.$request
        .get(`/bayes/datasets/list?page=${page}&pageSize=${this.pageSize}`)
        .then(res => {
          this.listLoading = false;
          this.total = res.data.data.count;
          this.list = res.data.data.list;
        });
    },
    indexMethod(index) {
      if (this.listQuery.pages == 1) {
        return index + 1;
      } else {
        return index + (this.listQuery.pages - 1) * this.listQuery.rows + 1;
      }
    },
    submitEdit(){
      this.$request.post('/bayes/datasets/update',{
        name: this.editDatasets.name,
        description: this.editDatasets.description,
        format: this.editDatasets.format,
        id: this.editDatasets.id
      }).then(res => {
        this.queryData(this.page)
        this.editFormVisible=false
      })
    },
    handleUpload() {
      this.$refs.upload.submit();
    },
    submitUpload(param) {
   
      this.$request
        .upload("/bayes/datasets/upload", {
          file: param.file,
          name: this.uploadDatasets.name,
          description: this.uploadDatasets.description,
          format: this.uploadDatasets.format
        })
        .then(res => {
          this.$refs.upload.clearFiles();
          this.uploadFormVisible = false;
          this.queryData(1)
        });
    },
    getList() {
      this.listLoading = false;
    },
    handleFilter() {
      this.listQuery.pages = 1;
      this.getList();
    },
    handleNodeClick(data) {
      this.listQuery.category = data.label;
      this.listQuery.categoryId = data.id;
      this.getList();
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.queryData(1)
    },
    handleCurrentChange(val) {
      this.page = val;
     this.queryData(val)
    },

    resetListQuery() {
      (this.listQuery = {
        rows: 10,
        category: ""
      }),
        this.handleFilter();
    },
    // handleDownloads(val) {
    //   const arr = val
    //   this.multipleSelection = []
    //   for(const v of arr){
    //     this.multipleSelection.push(v.position)
    //   }
    // },
    // downloadInBatches(){
    //   for(var p of this.multipliteSelection){
    //     window.open('http://10.4.20.151:8101/govern-spark/hdfs/downloadFile?filePath='+p)
    //   }
    // },

    handleEdit(row) {
      this.editDatasets = {
        name: row.name,
        description: row.description,
        format: row.format,
        id: row.id
      }
      this.editFormVisible = true
      //跳转到编辑网络
    },

    handleInference(row) {
      //跳转到网络推理
    },
    handleDelete(row){
      this.$request.post('/bayes/datasets/delete',{
        id: row.id
      }).then(res => {
        this.queryData(1)
      })
    },
    handleDownload(row) {
      window.open('/myapi/bayes/datasets/download?id=' + row.id)
    },
    formatJson(filterVal, jsonData) {
      return jsonData.map(v =>
        filterVal.map(j => {
          if (j === "timestamp") {
            return parseTime(v[j]);
          } else {
            return v[j];
          }
        })
      );
    }
  }
};
</script>
