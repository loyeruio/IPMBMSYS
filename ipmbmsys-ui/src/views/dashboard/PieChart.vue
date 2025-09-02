<template>
  <div :class="className" :style="{height:height,width:width}" ref="chart" />
</template>

<script>
import * as echarts from 'echarts'
import resize from './mixins/resize'

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    }
  },
  data() {
    return {
      chart: null,
      chartData: []
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$refs.chart)
      this.setOption()
    },
    setData(data) {
      this.chartData = data
      this.chart && this.setOption()
    },
    setOption() {
      // 如果没有数据，使用默认数据进行展示
      const data = this.chartData.length > 0 ? this.chartData : [
        { value: 320, name: '学生党员' },
        { value: 240, name: '教师党员' },
        { value: 149, name: '离退休党员' },
        { value: 100, name: '其他党员' }
      ]
      
      this.chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: data.map(item => item.name)
        },
        series: [
          {
            name: '党员类别',
            type: 'pie',
            roseType: 'radius',
            radius: [15, 95],
            center: ['50%', '38%'],
            data: data,
            animationEasing: 'cubicInOut',
            animationDuration: 2600
          }
        ]
      })
    }
  }
}
</script>
