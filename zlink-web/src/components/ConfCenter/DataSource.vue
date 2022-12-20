<template>
  <div>
    <!-- 面包屑导航区域-->
    <el-breadcrumb separator-class='el-icon-arrow-right'>
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>配置中心</el-breadcrumb-item>
      <el-breadcrumb-item>数据源中心</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 卡片视图区 -->
    <el-card>
      <div class="datasource-title">
        数据源
        <el-button type="primary" class="add-btn"
                   @click="addDataSourceDialogVisible = true, dataSourceFormTil='创建数据源'">
          创建数据源
        </el-button>
      </div>
      <!-- 数据源列表区域 -->
      <el-table :data='dataSourceList' border stripe>
        <el-table-column label='编号' type='index'></el-table-column>
        <el-table-column label='数据源类型' prop='databaseType'></el-table-column>
        <el-table-column label='数据源名称' prop='databaseName'></el-table-column>
        <el-table-column label='用户名' prop='userName'></el-table-column>
        <el-table-column label='jdbc 地址' prop='jdbcUrl'></el-table-column>
        <el-table-column label='是否内网'>
          <template slot-scope="scope">
            <i class="el-icon-success" v-if="scope.row.isInner === true" style="color: lightgreen;"></i>
            <i class="el-icon-message-solid" v-else style="color: orange;"></i>
          </template>
        </el-table-column>
        <el-table-column label='操作' width='180px'>
          <template slot-scope='scope'>
            <!--修改用户-->
            <el-button type='primary' icon='el-icon-edit' size='mini'
                       @click="getDataSourceById(scope.row.id), dataSourceFormTil='编辑数据源'"></el-button>
            <!--删除用户-->
            <el-button type='danger' icon='el-icon-delete' size='mini' @click='delDataSource(scope.row.id)'>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加数据源对话框 -->
    <el-dialog :title=dataSourceFormTil :visible.sync="addDataSourceDialogVisible" width="50%"
               @close="addDataSourceDialogClosed">
      <!-- 添加数据源表单 -->
      <el-form :model="addDataSourceForm" ref="addDataSourceFormRef"
               label-width="140px">
        <el-form-item label="数据源类型：">
          <el-select v-model="addDataSourceForm.databaseType" placeholder="请选择" @change="selectBtn">
            <el-option
              v-for="item in dataSourceType"
              :key="item.databaseType"
              :label="item.databaseType"
              :value="item.databaseType"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="数据源名称：">
          <el-input v-model="addDataSourceForm.databaseName"></el-input>
        </el-form-item>
        <el-form-item label="用户名：">
          <el-input v-model="addDataSourceForm.userName"></el-input>
        </el-form-item>
        <el-form-item label="密码：">
          <el-input v-model="addDataSourceForm.password"></el-input>
        </el-form-item>
        <el-form-item label="jdbc url：">
          <el-input v-model="addDataSourceForm.jdbcUrl"></el-input>
        </el-form-item>
        <el-form-item label="jdbc driver class：">
          <el-input v-model="addDataSourceForm.jdbcDriverClass" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="注释：">
          <el-input v-model="addDataSourceForm.comments"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDataSourceDialogVisible = false">取 消</el-button>
        <el-button type="success" @click="testJdbc">测试连接</el-button>
        <el-button type="primary" @click="addDataSource">保 存</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
export default {
  name: 'DataSource',
  data() {
    return {
      // 数据源列表的参数对象
      queryInfo: {
        // 当前的页
        pageNo: 1,
        // 当前每页显示多少条数据
        pageSize: 20
      },
      dataSourceList: [],
      total: 0,
      addDataSourceDialogVisible: false,
      // 添加数据源表单对象
      addDataSourceForm: {
        databaseType: '',
        databaseName: '',
        userName: '',
        password: '',
        jdbcUrl: '',
        jdbcDriverClass: '',
        comments: ''
      },
      dataSourceType: [],
      // dataSourceForm 标题
      dataSourceFormTil: '创建数据源'
    }
  },
  // 刚进入界面首先调用这里的函数
  created() {
    this.getDataSourceType()
    this.getDataSourceList()
  },
  methods: {
    selectBtn(value) {
      this.dataSourceType.forEach(item => {
        if (item.databaseType === value) this.addDataSourceForm.jdbcDriverClass = item.jdbcDriverClass
      })
    },
    async getDataSourceList() {
      const {data: res} = await this.$http.get('datasource/listDataSource', {params: this.queryInfo})
      if (res.code !== 200) {
        return this.$message.error('获取数据源列表失败！')
      }
      this.dataSourceList = res.data.records
      this.total = res.data.total
      console.log(this.dataSourceList)
    },
    async getDataSourceById(id) {
      const {data: res} = await this.$http.get('datasource/listDataSource', {params: {id: id}})
      if (res.code !== 200) {
        return this.$message.error('获取数据失败!')
      }
      if (res.data.records.length > 0) {
        this.addDataSourceForm = res.data.records[0]
        this.addDataSourceDialogVisible = true
        console.log(this.addDataSourceForm)
      }
    },
    async getDataSourceType() {
      const {data: res} = await this.$http.get('datasource/dataSourceType')
      console.log(res)
      if (res.code !== 200) {
        return this.$message.error('获取数据源类型失败！')
      }
      this.dataSourceType = res.data
      console.log(this.dataSourceType);
    },
    // 监听 pagesize 改变的事件
    handleSizeChange(newSize) {
      console.log(newSize)
      this.queryInfo.pageSize = newSize
      this.getDataSourceList()
    },
    // 监听页码值改变事件
    handleCurrentChange(newPage) {
      console.log(newPage)
      this.queryInfo.pageNo = newPage
      this.getDataSourceList()
    },
    // 添加数据源
    async addDataSource() {
      // console.log(this.addDataSourceForm)
      const {data: res} = await this.$http.post('datasource/addDataSource', this.addDataSourceForm)
      this.testJdbc().then(r => {
        if (r === true) this.addDataSourceDialogVisible = false
      })
      await this.getDataSourceList()
    },
    // 删除数据源
    async delDataSource(id) {
      console.log(id)
      const {data: res} = await this.$http.post('datasource/delDataSource', {'id': id})
      console.log(res)
      if (res.data !== true) {
        return this.$message.error('删除数据源失败！')
      }
      this.$message.success('删除数据源成功！')
      await this.getDataSourceList()
    },
    // 关闭数据源对话框
    addDataSourceDialogClosed() {
      this.$refs.addDataSourceFormRef.resetFields()
      Object.keys(this.addDataSourceForm).forEach(key => {
        this.addDataSourceForm[key] = ''
      })
    },
    // 测试数据源
    async testJdbc() {
      const {data: res} = await this.$http.post('datasource/testJdbc', this.addDataSourceForm)
      console.log(res)
      if (res.data !== '1') {
        this.$message.error('数据源测试连接失败！')
        return false
      }
      this.$message.success('数据源测试连接成功！')
      return true
    }
  }
}
</script>

<!--scoped 只在当前组件中生效，去掉在全局生效。一个组件中的样式不应该影响其他地方-->
<style lang='less' scoped>
.datasource-title {
  position: relative;
  height: 40px;

  .add-btn {
    position: absolute;
    right: 10px;
    bottom: 15px;
  }
}

</style>
