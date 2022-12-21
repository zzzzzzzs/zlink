<template>
  <div>
    <!-- 面包屑导航区域-->
    <el-breadcrumb separator-class='el-icon-arrow-right'>
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>cdc</el-breadcrumb-item>
      <el-breadcrumb-item>cdc</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card>
      <el-row :gutter="24">
        <el-col :span="5">
          <div class="grid-content bg-purple">
            源端数据库
            <el-select v-model="sourceId" filterable placeholder="请选择" @change="getSchemaAndTable('source')">
              <el-option v-for="item in dataSourceList" :key="item.id" :label="item.databaseName"
                         :value="item.id"></el-option>
            </el-select>
          </div>
          <el-row :gutter="24">
            <el-col :span="14">
              <el-input placeholder="输入关键字进行过滤" v-model="sourceFilterText" label-width="140px"></el-input>
              <el-tree
                class="filter-tree"
                :data="sourceMetaDataList"
                show-checkbox
                :props="defaultProps"
                :filter-node-method="sourceFilterNode"
                ref="sourceTree"
                node-key="name"
                @check="sourceHandleCheckChange"
              >
              </el-tree>
            </el-col>
          </el-row>
        </el-col>
        <el-col :span="5">
          <div class="grid-content bg-purple">
            目标数据库
            <el-select v-model="targetId" filterable placeholder="请选择" @change="getSchemaAndTable('target')">
              <el-option v-for="item in dataSourceList" :key="item.id" :label="item.databaseName"
                         :value="item.id"></el-option>
            </el-select>
          </div>
          <el-row :gutter="24">
            <el-col :span="14">
              <el-input placeholder="输入关键字进行过滤" v-model="targetFilterText" label-width="140px"></el-input>
              <el-tree
                class="filter-tree"
                :data="targetMetaDataList"
                show-checkbox
                :props="defaultProps"
                :filter-node-method="targetFilterNode"
                ref="targetTree"
                node-key="name"
                @check="targetHandleCheckChange"
              >
              </el-tree>
            </el-col>
          </el-row>
        </el-col>
        <el-col :span="4">
          <el-button @click="localFlinkCDC" type="primary" style="margin-left: 16px;">生成 cdc 任务</el-button>
        </el-col>
        <el-col :span="5">
          源端数据库表
          <!--使用draggable组件-->
          <div class="itxst">
            <div class="col">
              <draggable v-model="sourceArr" :disabled="disabled" animation="300" @start="onStart" @end="onEnd">
                <transition-group>
                  <div class="item" v-for="item in sourceArr" :key="item.id">{{ item.name }}</div>
                </transition-group>
              </draggable>
            </div>
          </div>
        </el-col>
        <el-col :span="5">
          目标数据库表
          <!--使用draggable组件-->
          <div class="itxst">
            <div class="col">
              <draggable v-model="targetArr" :disabled="disabled" animation="300" @start="onStart" @end="onEnd">
                <transition-group>
                  <div class="item" v-for="item in targetArr" :key="item.id">{{ item.name }}</div>
                </transition-group>
              </draggable>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

  </div>
</template>

<script>
import draggable from "vuedraggable";

export default {
  name: 'Cdc',
  //注册draggable组件
  components: {
    draggable,
  },
  watch: {
    sourceFilterText(val) {
      console.log(val)
      this.$refs.sourceTree.filter(val);
    },
    targetFilterText(val) {
      console.log(val)
      this.$refs.targetTree.filter(val);
    }
  },
  data() {
    return {
      sourceFilterText: '',
      targetFilterText: '',
      sourceId: '',
      targetId: '',
      dataSourceList: [],
      sourceMetaDataList: [],
      targetMetaDataList: [],
      defaultProps: {
        children: 'tables',
        label: 'name'
      },
      disabled: false,
      //定义要被拖拽对象的数组
      sourceArr: [],
      targetArr: [],
      flinkInfos: [],
    }
  },
  // 刚进入界面首先调用这里的函数
  created() {
    this.getDataSourceList()
  },
  methods: {
    async localFlinkCDC() {
      if (this.sourceArr.length == 0) return this.$message.error("源端数据库表不能为空")
      if (this.targetArr.length == 0) return this.$message.error("目标数据库表不能为空")
      if (this.sourceArr.length !== this.targetArr.length) return this.$message.error("两端表数据不一致")

      var sourceJson = JSON.parse(JSON.stringify(this.sourceArr));
      for (var i = 0; i < sourceJson.length; i++) {
        delete sourceJson[i].id
      }

      var targetJson = JSON.parse(JSON.stringify(this.targetArr));
      for (var i = 0; i < targetJson.length; i++) {
        delete targetJson[i].id
      }

      const para = {
        "sourceId": this.sourceId,
        "sourceTables": sourceJson,
        "targetId": this.targetId,
        "targetTables": targetJson,
        "remote": false,
      }

      console.log(para)
      const {data: res} = await this.$http.post('cdc/localFlinkCDC', para)
      if (res.code !== 200) return this.$message.error('生成失败')
      return this.$message.success("生成 flink 任务成功")
    },
    sourceHandleCheckChange(data, checked) {
      this.sourceArr = []
      this.$refs.sourceTree.getCheckedNodes(true).forEach(it => {
        this.sourceArr.push({id: it.schema + it.name, schema: it.schema, name: it.name})
      })
    }
    ,
    targetHandleCheckChange(data, checked) {
      this.targetArr = []
      this.$refs.targetTree.getCheckedNodes(true).forEach(it => {
        this.targetArr.push({id: it.schema + it.name, schema: it.schema, name: it.name})
      })
    }
    ,
    sourceFilterNode(value, sourceMetaDataList) {
      console.log('sourceFilterNode')
      if (!value) return true;
      return sourceMetaDataList.name.indexOf(value) !== -1;
    }
    ,
    targetFilterNode(value, targetMetaDataList) {
      console.log('targetFilterNode')
      if (!value) return true;
      return targetMetaDataList.name.indexOf(value) !== -1;
    }
    ,
    async getSchemaAndTable(val) {
      if (val === 'source') {
        const {data: res} = await this.$http.get('metadata/getSchemaAndTable', {params: {id: this.sourceId}})
        if (res.code !== 200) return this.$message.error('获取元数据失败！')
        this.sourceMetaDataList = res.data
      } else if (val === 'target') {
        const {data: res} = await this.$http.get('metadata/getSchemaAndTable', {params: {id: this.targetId}})
        if (res.code !== 200) return this.$message.error('获取元数据失败！')
        this.targetMetaDataList = res.data
      }
    }
    ,
    async getDataSourceList() {
      const {data: res} = await this.$http.get('datasource/listDataSource')
      if (res.code !== 200) {
        return this.$message.error('获取数据源列表失败！')
      }
      this.dataSourceList = res.data.records
      console.log(this.dataSourceList)
    }
    ,
    //设置禁止拖拽
    setJY() {
      this.disabled = true;
    }
    ,
    //设置启用拖拽
    setQY() {
      this.disabled = false;
    }
    ,
    //开始拖拽事件
    onStart() {
      this.drag = true;
    }
    ,
    //拖拽结束事件
    onEnd() {
      this.drag = false;
    }
    ,
  }
}
</script>

<!--scoped 只在当前组件中生效，去掉在全局生效。一个组件中的样式不应该影响其他地方-->
<style lang='less' scoped>
/*定义要拖拽元素的样式*/
.itxst {
  margin: 10px;
}

.col {
  width: 80%;
  flex: 1;
  padding: 10px;
  border: solid 1px #eee;
  border-radius: 5px;
  float: left;
}

.col + .col {
  margin-left: 10px;
}

.item {
  padding: 6px 12px;
  margin: 0px 10px 0px 10px;
  border: solid 1px #eee;
  background-color: #f1f1f1;
}

.item:hover {
  background-color: #fdfdfd;
  cursor: move;
}

.item + .item {
  border-top: none;
  margin-top: 6px;
}
</style>
