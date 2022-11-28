<template>
  <el-container class='home-container'>
    <!-- 头部区域-->
    <el-header>
      <div>
        <img src='../assets/dolphin.png' height='100' width='100' alt=''>
        <span>zlink</span>
      </div>
      <el-button type='info' @click='logout'>退出</el-button>
    </el-header>
    <!-- 页面主题区域-->
    <el-container>
      <!-- 侧边栏-->
      <el-aside :width="isCollapse ? '64px':'200px'">
        <div class='toggle-button' @click='toggleCollapse'>|||</div>
        <!-- 侧边栏菜单区域-->
        <!-- router， 开启路由模式， 通过二级菜单的 index 值设置-->
        <el-menu
          background-color='#545c64'
          text-color='#fff'
          active-text-color='#ffd04b'
          unique-opened
          :collapse='isCollapse'
          :collapse-transition='false'
          router
          :default-active='activePath'>
          <!-- 一级菜单, 每一个 v-for 都尽量绑定一个唯一的 key 属性-->
          <el-menu-item :index="'/'+item.path" v-for='item in menulist' :key='item.id'
                        @click="saveNavState('/'+ item.path)">
            <!-- 一级菜单的模板区-->
            <template slot='title'>
              <!-- 图标-->
              <i :class='iconsDict[item.id]'></i>
              <!-- 文本-->
              <span>{{ item.name }}</span>
            </template>
            <!-- 二级菜单, 开启路由模式使用 index 配置路由-->
            <!--<el-menu-item :index="'/'+ subItem.path" v-for='subItem in item.children' :key='subItem.id'-->
            <!--              @click="saveNavState('/'+ subItem.path)">-->
            <!--  &lt;!&ndash; 一级菜单的模板区&ndash;&gt;-->
            <!--  <template slot='title'>-->
            <!--    &lt;!&ndash; 图标&ndash;&gt;-->
            <!--    <i class='el-icon-menu'></i>-->
            <!--    &lt;!&ndash; 文本&ndash;&gt;-->
            <!--    <span>{{ subItem.authName }}</span>-->
            <!--  </template>-->
            <!--</el-menu-item>-->
          </el-menu-item>
        </el-menu>
      </el-aside>
      <!-- 右侧内容主题-->
      <el-main>
        <!-- 路由占位符-->
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      // 左侧菜单数据
      menulist: [],
      // 图标绑定字典
      iconsDict: {
        '1': 'iconfont icon-user',
        '2': 'iconfont icon-tijikongjian',
        '101': 'iconfont icon-shangpin',
        '102': 'iconfont icon-danju',
        '145': 'iconfont icon-baobiao'
      },
      // 是否折叠
      isCollapse: false,
      // 被激活的链接地址, 主要是用来高亮显示
      activePath: ''
    }
  },
  // 刚进入界面首先调用这里的函数
  created() {
    this.getMenuList()
    // this.activePath = window.sessionStorage.getItem('activePath')
  },
  methods: {
    // TODO 退出登录
    // 基于token 的方式实现退出比较简单，只需要销毁本地的 token即可。这样，后续的请求就不会携带token，必须里新登录生成一个新的 token之后才可以访问页面。
    logout() {
      window.sessionStorage.clear()
      // 跳转登录页
      this.$router.push('/login')
    },
    // 获取所有菜单
    async getMenuList() {
      const {data: res} = await this.$http.get('menus/listMenus')
      if (res.code !== 200) return this.$message.error(res.meta.msg)
      this.menulist = res.data
      console.log(this.menulist)
    },
    // 点击按钮, 切换
    toggleCollapse() {
      this.isCollapse = !this.isCollapse
    },
    // 保存链接的菜单状态
    saveNavState(activePath) {
      window.sessionStorage.setItem('activePath', activePath)
      this.activePath = activePath
    }
  }
}
</script>

<!--scoped 只在当前组件中生效，去掉在全局生效。一个组件中的样式不应该影响其他地方-->
<style lang='less' scoped>
.home-container {
  height: 100%;
}


.el-header {
  background-color: #5a27a8;
  display: flex;
  justify-content: space-between;
  padding: 0;
  align-items: center;
  color: #fff;
  font-size: 20px;

  > div {
    display: flex;
    align-items: center;

    span {
      margin-left: 15px;
    }
  }
}

.el-aside {
  background-color: #1eacda;
}

.el-main {
  background-color: #df5000;

  .el-menu {
    border-right: none;
  }
}

.iconfont {
  margin-right: 10px;
}

.toggle-button {
  background-color: #fda252;
  font-size: 10px;
  line-height: 24px;
  color: #55a532;
  text-align: center;
}
</style>
