<template>
  <el-container class='home-container'>
    <!-- 头部区域-->
    <el-header>
      <div>
        <img src='../assets/logo.svg' height='100' width='100' alt=''>
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
          background-color='#001529'
          text-color='#fff'
          active-text-color='#ffd04b'
          unique-opened
          :collapse='isCollapse'
          :collapse-transition='false'
          router
          :default-active='activePath'>
          <!-- TODO 一级菜单也可以点击-->
          <div v-for="item in menulist" :key="item.id">
            <el-menu-item :index="'/'+item.path"
                          :key='item.id'
                          v-if="item.children == null"
                          @click="saveNavState('/'+ item.path)">
              <!-- 一级菜单的模板区-->
              <template slot='title'>
                <!-- 图标-->
                <i :class='iconsDict[item.id]'></i>
                <!-- 文本-->
                <span>{{ item.name }}</span>
              </template>
            </el-menu-item>

            <!--显示二级菜单-->
            <el-submenu :index="item.id + ''"
                        :key='item.id'
                        v-else>
              <!-- 一级菜单的模板区-->
              <template slot='title'>
                <!-- 图标-->
                <i :class='iconsDict[item.id]'></i>
                <!-- 文本-->
                <span>{{ item.name }}</span>
              </template>
              <!-- 二级菜单, 开启路由模式使用 index 配置路由-->
              <el-menu-item :index="'/'+ subItem.path" v-for='subItem in item.children' :key='subItem.id'
                            @click="saveNavState('/'+ subItem.path)"
              >
                <!-- 一级菜单的模板区-->
                <template slot='title'>
                  <!-- 图标-->
                  <i class='el-icon-menu'></i>
                  <!-- 文本-->
                  <span>{{ subItem.name }}</span>
                </template>
              </el-menu-item>
            </el-submenu>
          </div>
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
        '3': 'iconfont icon-shangpin',
        '4': 'iconfont icon-shangpin',
        '5': 'iconfont icon-shangpin',
        '6': 'iconfont icon-shangpin',
        '7': 'iconfont icon-shangpin',
        '8': 'iconfont icon-shangpin',
        '9': 'iconfont icon-shangpin',
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
      console.log('菜单', this.menulist)
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
  background-color: #fff;
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
  background-color: #001529;
}

.el-main {
  background-color: #f3f2f2;

  .el-menu {
    border-right: none;
  }
}

.el-menu-item.is-active {
  background-color: #224593 !important;
  color: #fff;
  span {
    color: #fff !important;
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
