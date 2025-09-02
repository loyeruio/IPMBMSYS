import Vue from 'vue'
import App from './App'
import store from './store'
import plugins from './plugins'
import './permission'
import { getDicts } from '@/api/system/dict/data'
import { parseTime, resetForm, selectDictLabel, selectDictLabels, handleTree } from '@/utils/ruoyi'

// 全局方法挂载
Vue.prototype.getDicts = getDicts
Vue.prototype.parseTime = parseTime
Vue.prototype.resetForm = resetForm
Vue.prototype.selectDictLabel = selectDictLabel
Vue.prototype.selectDictLabels = selectDictLabels
Vue.prototype.handleTree = handleTree

Vue.config.productionTip = false
Vue.use(plugins)

App.mpType = 'app'

const app = new Vue({
  store,
  ...App
})
app.$mount()
