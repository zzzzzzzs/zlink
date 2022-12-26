<template>
  <div>
    <!-- 面包屑导航区域-->
    <el-breadcrumb separator-class='el-icon-arrow-right'>
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>配置中心</el-breadcrumb-item>
      <el-breadcrumb-item>flink 配置中心</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card>
      <div class="flinkconf-title">
        flink 配置
        <el-button type="primary" class="add-btn"
                   @click="addFlinkConfDialogVis = true, flinkConfFormTil='创建 flink 环境'">
          创建 flink 环境
        </el-button>
      </div>
      <!-- 数据源列表区域 -->
      <el-table :data='flinkConfList' border stripe>
        <el-table-column label='编号' type='index'></el-table-column>
        <el-table-column label='集群名字' prop='name'></el-table-column>
        <el-table-column label='flink 集群模式' prop='model'></el-table-column>
        <el-table-column label='ip' prop='ip'></el-table-column>
        <el-table-column label='port' prop='port'></el-table-column>
        <el-table-column label='操作' width='180px'>
          <template slot-scope='scope'>
            <el-button type='primary' icon='el-icon-edit' size='mini'
                       @click="getFlinkConfById(scope.row.id), flinkConfFormTil='编辑 flink 环境'"></el-button>
            <el-button type='danger' icon='el-icon-delete' size='mini' @click='delFlinkConf(scope.row.id)'>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title=flinkConfFormTil :visible.sync="addFlinkConfDialogVis" width="50%"
               @close="addFlinkConfDialogClosed">
      <!-- 添加数据源表单 -->
      <el-form :model="addFlinkConfForm" ref="addFlinkConfFormRef"
               label-width="140px">
        <el-form-item label="集群名字">
          <el-input v-model="addFlinkConfForm.name"></el-input>
        </el-form-item>
        <el-form-item label="flink 模式：">
          <el-select v-model="addFlinkConfForm.model" placeholder="请选择" @change="selectBtn">
            <el-option
              v-for="item in flinkModel"
              :key="item.flinkModel"
              :label="item.flinkModel"
              :value="item.flinkModel"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <div>
          <div v-if="addFlinkConfForm.model == 'standalone'">
            <el-form-item label="ip：">
              <el-input v-model="addFlinkConfForm.ip"></el-input>
            </el-form-item>
            <el-form-item label="port：">
              <el-input v-model="addFlinkConfForm.port"></el-input>
            </el-form-item>
          </div>
          <div v-else-if="addFlinkConfForm.model == 'yarn'">
            <el-form-item label="yarn 网址：">
              <el-input v-model="addFlinkConfForm.yarnUrl"></el-input>
            </el-form-item>
          </div>
        </div>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="addFlinkConfDialogVis = false">取 消</el-button>
        <el-button type="success" @click="testIp">测试连接</el-button>
        <el-button type="primary" @click="addFlinkConf">保 存</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
export default {
  name: 'FlinkConf',
  data() {
    return {
      queryInfo: {
        // 当前的页
        pageNo: 1,
        // 当前每页显示多少条数据
        pageSize: 20
      },
      flinkConfList: [],
      total: 0,
      addFlinkConfForm: {
        model: '',
        ip: '',
        port: '',
      },
      flinkModel: [],
      flinkConf: [],
      addFlinkConfDialogVis: false,
      flinkConfFormTil: '创建 flink 环境'
    }
  },
  // 刚进入界面首先调用这里的函数
  created() {
    this.listFlinkModel()
    this.pageFlinkConf()
  },
  methods: {
    async getFlinkConfById(id) {
      const {data: res} = await this.$http.get('flinkconf/pageFlinkConf', {params: {id: id}})
      if (res.code !== 200) {
        return this.$message.error('获取数据失败!')
      }
      if (res.data.records.length > 0) {
        this.addFlinkConfForm = res.data.records[0]
        this.addFlinkConfDialogVis = true
        console.log(this.addFlinkConfForm)
      }
    },
    // 删除数据源
    async delFlinkConf(id) {
      console.log(id)
      const {data: res} = await this.$http.post('flinkconf/delFlinkConf', {'id': id})
      console.log(res)
      if (res.data !== true) {
        return this.$message.error('删除数据源失败！')
      }
      this.$message.success('删除数据源成功！')
      await this.pageFlinkConf()
    },
    async pageFlinkConf() {
      const {data: res} = await this.$http.get('flinkconf/pageFlinkConf', {params: this.queryInfo})
      if (res.code !== 200) return this.$message.error('获取数据源列表失败！')
      this.flinkConfList = res.data.records
      this.total = res.data.total
      console.log(this.flinkConfList)
    },
    async addFlinkConf() {
      console.log(this.addFlinkConfForm)
      const {data: res} = await this.$http.post('flinkconf/addFlinkConf', this.addFlinkConfForm)
      if (res.code !== 200) return this.$message.error('保存 flink 配置失败！')
      this.testIp().then(r => {
        if (r === true) this.addFlinkConfDialogVis = false
      })
      this.pageFlinkConf()
      this.$message.success('保存 flink 配置成功！')
      this.addFlinkConfDialogVis = false
    },
    addFlinkConfDialogClosed() {
    },
    selectBtn(value) {
      this.flinkModel.forEach(item => {
        if (item.flinkModel === value) this.addFlinkConfForm.model = value
      })
    },
    async listFlinkModel() {
      const {data: res} = await this.$http.get('flink-model/listFlinkModel')
      if (res.code !== 200) return this.$message.error("获取 flink model 失败！")
      this.$message.success("获取 flink model 成功！")
      // console.log(res.data)
      this.flinkModel = res.data
    },
    // 测试数据源
    async testIp() {
      if(this.addFlinkConfForm.model === 'standalone'){
        const {data: res} = await this.$http.post('flinkconf/testIp', this.addFlinkConfForm)
        console.log(res)
        if (res.data !== true) {
          this.$message.error('数据源测试连接失败！')
          return false
        }
        this.$message.success('数据源测试连接成功！')
        return true
      }
    },
  }
}
</script>

<!--scoped 只在当前组件中生效，去掉在全局生效。一个组件中的样式不应该影响其他地方-->
<style lang='less' scoped>
.flinkconf-title {
  position: relative;
  height: 40px;

  .add-btn {
    position: absolute;
    right: 10px;
    bottom: 15px;
  }
}

</style>
