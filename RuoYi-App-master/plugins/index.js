import tab from './tab'
import auth from './auth'
import modal from './modal'
import dict from '@/utils/dict'
import dictConverter from '@/utils/dict/DictConverter'

export default {
  install(Vue) {
    // 页签操作
    Vue.prototype.$tab = tab
    // 认证对象
    Vue.prototype.$auth = auth
    // 模态框对象
    Vue.prototype.$modal = modal
    // 字典插件
    Vue.use(dict, {
      metas: {
        '*': {
          request: (dictMeta) => {
            return Vue.prototype.getDicts(dictMeta.type).then(res => {
              return res.data
            })
          },
          responseConverter: (response, dictMeta) => {
            const dicts = response instanceof Array ? response : []
            return dicts.map(d => dictConverter(d, dictMeta))
          }
        }
      }
    })
  }
}
