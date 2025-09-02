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
        { value: 120, name: '本科' },
        { value: 80, name: '硕士' },
        { value: 60, name: '博士' },
        { value: 40, name: '其他' }
      ]
      this.chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [
          {
            type: 'category',
            data: data.map(item => item.name),
            axisTick: { alignWithLabel: true }
          }
        ],
        yAxis: [
          {
            type: 'value'
          }
        ],
        series: [
          {
            name: '学历分布',
            type: 'bar',
            barWidth: '60%',
            data: data.map(item => item.value)
          }
        ]
      })
    }
  }
}
</script>
