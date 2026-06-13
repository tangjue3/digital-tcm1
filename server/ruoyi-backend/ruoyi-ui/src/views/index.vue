<template>
  <div class="body">
    <div class="head clearfix">
      <!-- <h1 class="pulll_left">AI智能辅助面试系统 - 数据监控大屏</h1> -->
      <div class="menu menu2 pulll_left">
        <ul>      
          <li><a href="#">面试概览</a></li>
          <li><a href="#">候选人管理</a></li>
          <li><a href="#">AI评估</a></li>
          <li><a href="#">数据分析</a></li>
          <li><a href="#">系统设置</a></li>
          <li><a href="#">帮助中心</a></li>
        </ul>
      </div>
      <div class="time" id="showTime">{{ currentTime }}</div>
    </div>
    <div class="mainbox">
      <ul class="clearfix nav1">
        <li style="width: 22%">
          <div class="box">
            <div class="tit">今日面试统计</div>
            <div class="boxnav" style="height: 330px;">
              <div class="yqlist">
                <ul class="clearfix">
                  <li>
                    <div class="yq" id="yq">{{ todayInterviews }}</div>
                    <span>今日面试总数</span>
                  </li>
                  <li>
                    <div class="yq">{{ completedInterviews }}</div>
                    <span>已完成面试</span>
                  </li>
                  <li>
                    <div class="yq">{{ pendingInterviews }}</div>
                    <span>待面试人数</span>
                  </li>
                  <li>
                    <div class="yq">{{ passedCandidates }}</div>
                    <span>通过人数</span>
                  </li>
                </ul>
              </div>
            </div>
          </div>
          <div class="box">
            <div class="tit">热门职位面试排行</div>
            <div class="boxnav" style="height: 406px;">
              <div class="" style="height: 406px" id="echart2"></div>
            </div>
          </div>
        </li>
        <li style="width: 56%">
          <div class="aa-center">
          <div class="vlc-top-realtime-data">
            <span>AI智能面试系统控制中心</span>
          </div>
    <div class="aac-animation-imgs">
      <img class="aacai-mv" :src="getImageUrl('main-view.png')" alt="主视图" />
      <img class="aacaim-light" :src="getImageUrl('light.png')" alt="灯光效果" />
      <img class="aacaim-building" :src="getImageUrl('building.png')" alt="建筑背景" />
      <div class="aaca-circle">
        <div v-for="(item, index) in circleItems" :key="index" class="circle" :class="'circle' + (index + 1)">
          <img class="circle-img-pop" :src="getImageUrl('base1.png')" alt="背景装饰" />
          <img class="circle-img-bg" :src="getImageUrl('NO1.icon.png')" alt="图标背景" />
          <img class="circle-img-bg-icon" :src="getImageUrl(item.icon)" alt="功能图标" />
          <span class="circle-text-main">{{ item.title }}</span>
        </div>
      </div>
    </div>
  </div>

          
        </li>
        <li style="width: 22%">
          <div class="box">
            <div class="tit">AI评估维度</div>
            <div class="boxnav" id="echart4" style="height: 200px;"></div>
          </div>
          <div class="box">
            <div class="tit">候选人来源分布</div>
            <div class="boxnav" style="height: 250px;" id="echart5"></div>
          </div>
          <div class="box">
            <div class="tit">综合能力评估</div>
            <div class="boxnav" style="height: 250px;" id="echart6"></div>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: 'Index',
  data() {
    return {
      currentTime: new Date().toLocaleTimeString(),
      todayInterviews: 156,
      completedInterviews: 89,
      pendingInterviews: 67,
      passedCandidates: 45,
      // 定义圆形菜单项数据
      circleItems: [
        { title: '面试题库管理', icon: 'szhsfjs.png' },
        { title: '候选人管理', icon: 'jcfwzq.png' },
        { title: 'AI面试官系统', icon: 'pragpyycj.png' },
        { title: '面试数据分析', icon: 'dsjyy.png' },
        { title: '智能评估平台', icon: 'szhnlkfpt.png' },
        { title: '面试报告中心', icon: 'bbzx.png' },
        { title: '人才推荐引擎', icon: 'qnsclmtd.png' }
      ]
    };
  },
  mounted() {
    // 每秒更新一次时间
    this.timeInterval = setInterval(() => {
      this.currentTime = new Date().toLocaleTimeString();
    }, 1000);

    this.echarts_2();
    this.echarts_4();
    this.echarts_5();
    this.echarts_6();
  },
  beforeDestroy() {
    // 组件销毁前清除定时器
    clearInterval(this.timeInterval);
  },
  methods: {
    // 获取图片URL的函数
    getImageUrl(imageName) {
      try {
        return require(`@/assets/achievement-aggregation/${imageName}`)
      } catch (e) {
        // 如果图片不存在，返回一个占位符或空字符串
        console.warn(`图片 ${imageName} 不存在`)
        return ''
      }
    },
    echarts_2() {
      // 热门职位面试排行榜
      var myChart = echarts.init(document.getElementById('echart2'));
      var data = [156, 142, 128, 115, 98, 87, 76, 65, 58, 45, 38, 32, 28];
      var titlename = ['前端工程师', 'Java开发工程师', '产品经理', 'UI设计师', 'Python开发工程师', '测试工程师', '运营专员', '数据分析师', '项目经理', '人事专员', '市场专员', '客服专员', '财务专员'];
      var option = {
        grid: {
          left: '0',
          top: '0',
          right: '0',
          bottom: '0%',
          containLabel: true
        },
        xAxis: {
          show: false
        },
        yAxis: [
          {
            show: true,
            data: titlename,
            inverse: true,
            axisLine: { show: false },
            splitLine: { show: false },
            axisTick: { show: false },
            axisLabel: {
              textStyle: {
                color: '#fff'
              }
            }
          },
          {
            show: false,
            inverse: true,
            data: data,
            axisLabel: { textStyle: { color: '#fff' } },
            axisLine: { show: false },
            splitLine: { show: false },
            axisTick: { show: false }
          }
        ],
        series: [
          {
            name: '面试人数',
            type: 'bar',
            yAxisIndex: 0,
            data: data,
            barWidth: 15,
            itemStyle: {
              normal: {
                barBorderRadius: 50,
                color: '#1089E7'
              }
            },
            label: {
              normal: {
                show: true,
                position: 'right',
                formatter: '{c}',
                textStyle: { color: 'rgba(255,255,255,.5)' }
              }
            }
          }
        ]
      };
      myChart.setOption(option);
      window.addEventListener('resize', function () {
        myChart.resize();
      });
    },
    echarts_3() {
      // 月度面试趋势
      var myChart = echarts.init(document.getElementById('echart3'));
      var option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            lineStyle: {
              color: '#dddc6b'
            }
          }
        },
        grid: {
          left: '10',
          top: '20',
          right: '30',
          bottom: '10',
          containLabel: true
        },
        xAxis: [
          {
            type: 'category',
            boundaryGap: false,
            axisLabel: {
              textStyle: {
                color: 'rgba(255,255,255,.6)',
                fontSize: 14
              }
            },
            axisLine: {
              lineStyle: {
                color: 'rgba(255,255,255,.2)'
              }
            },
            data: [
              '1月',
              '2月',
              '3月',
              '4月',
              '5月',
              '6月',
              '7月',
              '8月',
              '9月',
              '10月',
              '11月',
              '12月'
            ]
          }
        ],
        yAxis: [
          {
            type: 'value',
            axisTick: { show: false },
            splitNumber: 4,
            axisLine: {
              lineStyle: {
                color: 'rgba(255,255,255,.1)'
              }
            },
            axisLabel: {
              textStyle: {
                color: 'rgba(255,255,255,.6)',
                fontSize: 16
              }
            },
            splitLine: {
              lineStyle: {
                color: 'rgba(255,255,255,.1)',
                type: 'dotted'
              }
            }
          }
        ],
        series: [
          {
            name: '面试通过率',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 5,
            showSymbol: false,
            lineStyle: {
              normal: {
                color: 'rgba(31, 174, 234, 1)',
                width: 2
              }
            },
            areaStyle: {
              normal: {
                color: new echarts.graphic.LinearGradient(
                  0,
                  0,
                  0,
                  1,
                  [
                    {
                      offset: 0,
                      color: 'rgba(31, 174, 234, 0.4)'
                    },
                    {
                      offset: 0.8,
                      color: 'rgba(31, 174, 234, 0.1)'
                    }
                  ],
                  false
                ),
                shadowColor: 'rgba(0, 0, 0, 0.1)'
              }
            },
            itemStyle: {
              normal: {
                color: '#1f7eea',
                borderColor: 'rgba(31, 174, 234, .1)',
                borderWidth: 5
              }
            },
            data: [65, 72, 68, 75, 82, 88, 85, 90, 87, 92, 89, 95]
          }
        ]
      };
      myChart.setOption(option);
      window.addEventListener('resize', function () {
        myChart.resize();
      });
    },
    echarts_4() {
      // AI评估维度对比
      var myChart = echarts.init(document.getElementById('echart4'));
      var option = {
        grid: {
          left: '0',
          top: '30',
          right: '0',
          bottom: '10',
          containLabel: true
        },
        legend: {
          top: 0,
          textStyle: {
            color: '#fff'
          },
          itemWidth: 10,
          itemHeight: 10
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        xAxis: {
          type: 'category',
          data: ['技术能力', '沟通表达', '逻辑思维'],
          axisTick: {
            show: true
          },
          axisLine: {
            show: true,
            lineStyle: {
              color: 'rgba(255,255,255,.1)',
              width: 1,
              type: 'dotted'
            }
          },
          axisLabel: {
            textStyle: {
              fontSize: 12,
              color: '#fff'
            }
          }
        },
        yAxis: {
          type: 'value',
          splitLine: {
            show: true,
            lineStyle: {
              color: 'rgba(255,255,255,.1)',
              width: 1,
              type: 'dotted'
            }
          },
          axisLabel: {
            formatter: '{value}',
            textStyle: {
              fontSize: 12,
              color: '#fff'
            }
          },
          axisLine: {
            show: false
          }
        },
        series: [
          {
            name: '平均分',
            type: 'bar',
            data: [85, 78, 82],
            barWidth: 15,
            barGap: 1,
            itemStyle: {
              normal: {
                barBorderRadius: 50,
                color: '#446ACF'
              }
            }
          },
          {
            name: '优秀分',
            type: 'bar',
            data: [92, 88, 90],
            barWidth: 15,
            itemStyle: {
              normal: {
                barBorderRadius: 50,
                color: '#4fb69d'
              }
            }
          }
        ]
      };
      myChart.setOption(option);
      window.addEventListener('resize', function () {
        myChart.resize();
      });
    },
    echarts_5() {
      // 候选人来源分布
      var myChart = echarts.init(document.getElementById('echart5'));
      var option = {
        legend: {
          orient: 'vertical',
          itemWidth: 10,
          itemHeight: 10,
          textStyle: {
            color: 'rgba(255,255,255,.5)'
          },
          top: '20%',
          right: 30,
          data: ['招聘网站', '内推', '校园招聘', '猎头推荐']
        },
        color: [
          '#37a2da',
          '#32c5e9',
          '#9fe6b8',
          '#ffdb5c'
        ],
        tooltip: {
          trigger: 'item',
          formatter: '{b} : {c} ({d}%)'
        },
        calculable: true,
        series: [
          {
            type: 'pie',
            radius: [20, 70],
            center: ['35%', '50%'],
            roseType: 'area',
            data: [
              { value: 450, name: '招聘网站' },
              { value: 280, name: '内推' },
              { value: 180, name: '校园招聘' },
              { value: 120, name: '猎头推荐' }
            ],
            label: {
              normal: {
                formatter: function (param) {
                  return param.name + ':\n' + param.value + '\n';
                }
              }
            },
            labelLine: {
              normal: {
                length: 5,
                length2: 15,
                lineStyle: { width: 1 }
              }
            },
            itemStyle: {
              normal: {
                shadowBlur: 30,
                shadowColor: 'rgba(0, 0, 0, 0.4)'
              }
            }
          }
        ]
      };
      myChart.setOption(option);
      window.addEventListener('resize', function () {
        myChart.resize();
      });
    },
    echarts_6() {
      // 综合能力评估雷达图
      var myChart = echarts.init(document.getElementById('echart6'));
      var option = {
        tooltip: {
          trigger: 'axis'
        },
        radar: [
          {
            indicator: [
              {
                text: '专业技能',
                max: 100
              },
              {
                text: '沟通能力',
                max: 100
              },
              {
                text: '学习能力',
                max: 100
              },
              {
                text: '团队协作',
                max: 100
              },
              {
                text: '抗压能力',
                max: 100
              }
            ],
            center: ['50%', '50%'],
            radius: '70%',
            startAngle: 90,
            splitNumber: 4,
            shape: 'circle',
            name: {
              padding: -5,
              formatter: '{value}',
              textStyle: {
                color: 'rgba(255,255,255,.5)'
              }
            },
            splitArea: {
              areaStyle: {
                color: 'rgba(255,255,255,.05)'
              }
            },
            axisLine: {
              lineStyle: {
                color: 'rgba(255,255,255,.05)'
              }
            },
            splitLine: {
              lineStyle: {
                color: 'rgba(255,255,255,.05)'
              }
            }
          }
        ],
        series: [
          {
            name: '能力评估',
            type: 'radar',
            tooltip: {
              trigger: 'item'
            },
            data: [
              {
                name: '行业平均值',
                value: [75, 70, 65, 72, 68],
                lineStyle: {
                  normal: {
                    color: '#03b48e',
                    width: 2
                  }
                },
                areaStyle: {
                  normal: {
                    color: '#03b48e',
                    opacity: 0.4
                  }
                },
                symbolSize: 0
              },
              {
                name: '当前候选人',
                value: [88, 85, 92, 78, 82],
                symbolSize: 0,
                lineStyle: {
                  normal: {
                    color: '#3893e5',
                    width: 2
                  }
                },
                areaStyle: {
                  normal: {
                    color: 'rgba(19, 173, 255, 0.5)'
                  }
                }
              }
            ]
          }
        ]
      };
      myChart.setOption(option);
      window.addEventListener('resize', function () {
        myChart.resize();
      });
    }
  }
};
</script>

<style scoped>
@charset "utf-8";
/* CSS Document */
* {
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
}
*,
.body {
  padding: 0px;
  margin: 0px;
  font-family: "微软雅黑";
}
.body {
  color: #fff;
  font-size: 16px;
  background: url('~@/assets/achievement-aggregation/ac-bj.jpg') no-repeat;
  background-size: 100% 100%;
}
/*
@font-face {
  font-family: electronicFont;
  src: url(../font/DS-DIGIT.TTF);
}
*/
html,
body {
  min-height: 100%;
}
li {
  list-style-type: none;
}
table {}
i {
  margin: 0px;
  padding: 0px;
  text-indent: 0px;
}
img {
  border: none;
  max-width: 100%;
}
a {
  text-decoration: none;
  color: #fff;
}
a.active,
a:focus {
  outline: none!important;
  text-decoration: none;
}
ol,
ul,
p,
h1,
h2,
h3,
h4,
h5,
h6 {
  padding: 0;
  margin: 0;
}
a:hover {
  color: #06c;
  text-decoration: none!important;
}
.clearfix:after,
.clearfix:before {
  display: table;
  content: " ";
}
.clearfix:after {
  clear: both;
}
.pulll_left {
  float: left;
}
.pulll_right {
  float: right;
}
i {
  font-style: normal;
}
.text-w {
  color: #ffe400;
}
.text-d {
  color: #ff6316;
}
.text-s {
  color: #14e144;
}
.text-b {
  color: #07e5ff;
}

.head {
  position: relative;
  height: 90px;
  margin: 0 15px;
  padding-right: 60px;
}
.head h1 {
  font-size: 30px;
  letter-spacing: -2px;
  text-align: center;
  line-height: 90px;
  padding-right: 55px;
  color: #daf9ff;
}
.head .menu {}
.head .menu ul {
  font-size: 0;
}

.head .menu li {
  display: inline-block;
  position: relative;
  margin: 30px 15px;
}
.head .menu li a {
  display: block;
  font-size: 18px;
  color: #fff;
  line-height: 30px;
  padding: 0 10px;
}
.time {
  position: absolute;
  right: 0;
  line-height: 90px;
  top: 0;
}

.menu li:before,
.menu li:after {
  position: absolute;
  width: 10px;
  height: 5px;
  opacity: .4;
  content: "";
  border-top: 2px solid #02a6b5;
  top: -1px;
  border-radius: 2px;
}
.menu li:before,
.menu li a:before {
  border-left: 2px solid #02a6b5;
  left: -1px;
}
.menu li:after,
.menu li a:after {
  border-right: 2px solid #02a6b5;
  right: -1px;
}
.menu li a {
  position: relative;
}
.menu li a:before,
.menu li a:after {
  position: absolute;
  width: 10px;
  height: 5px;
  opacity: .4;
  content: "";
  border-bottom: 2px solid #02a6b5;
  bottom: -1px;
  border-radius: 2px;
}

.head .menu li a:hover {
  color: #f4e925;
}
.menu li a:hover:before,
.menu li a:hover:after,
.menu li:hover:before,
.menu li:hover:after {
  border-color: #f4e925;
  opacity: 1;
}

.mainbox {
  padding: 0px 10px;
}
.nav1 {
  margin-left: -6px;
  margin-right: -6px;
}
.nav1>li {
  padding: 0 6px;
  float: left;
}

.box {
  border: 1px solid rgba(7, 118, 181, .5);
  box-shadow: inset 0 0 10px rgba(7, 118, 181, .4);
  margin-bottom: 12px;
  position: relative;
}
.tit {
  padding: 10px 10px 10px 25px;
  border-bottom: 1px solid rgba(7, 118, 181, .7);
  font-size: 16px;
  font-weight: 500;
  position: relative;
}
.tit:before,
.tit01:before {
  position: absolute;
  content: "";
  width: 6px;
  height: 6px;
  background: rgba(22, 214, 255, .9);
  box-shadow: 0 0 5px rgba(22, 214, 255, .9);
  border-radius: 10px;
  left: 10px;
  top: 18px;
}

.tit:after,
.box:before {
  width: 100%;
  height: 1px;
  content: "";
  position: absolute;
  left: 0;
  bottom: -1px;
  background: linear-gradient(to right, #076ead, #4ba6e0, #076ead);
  box-shadow: 0 0 5px rgba(131, 189, 227, 1);
  opacity: .6;
}
.box:before {
  top: -1px;
}

.boxnav {
  padding: 10px;
}
.tit01 {
  font-size: 16px;
  font-weight: 500;
  position: relative;
  padding-left: 15px;
}
.tit01:before {
  left: 3px;
  top: 8px;
}

.mapc {
  background: url(./images/bg3.png) no-repeat center center;
  background-size: 100% 100%;
}
.map {
  position: relative;
  height: 100%;
}
.map img {}
.mapnav {
  position: absolute;
  z-index: 10;
}


.yqlist li {
  float: left;
  width: 50%;
  padding: 10px 0;
  text-align: center;
}
.yq {
  width: 100px;
  height: 100px;
  margin: 0 auto 5px auto;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 30px;
  font-family: electronicFont;
  color: #fff32b;
}
.yqlist li span {
  opacity: .6;
  font-size: 14px;
}

.yq:before {
  position: absolute;
  width: 100%;
  height: 100%;
  content: "";
  background: url(./images/img1.png) center center;
  border-radius: 100px;
  background-size: 100% 100%;
  opacity: .3;
  left: 0;
  top: 0;
  animation: myfirst2 15s infinite linear;
}

.yq:after {
  position: absolute;
  width: 86%;
  background: url(./images/img2.png) center center;
  border-radius: 100px;
  background-size: 100% 100%;
  opacity: .3;
  height: 86%;
  content: "";
  left: 7%;
  top: 7%;
  animation: myfirst 15s infinite linear;
}


@keyframes myfirst {
  to {
    transform: rotate(-360deg);
  }
}
@keyframes myfirst2 {
  to {
    transform: rotate(360deg);
  }
}

/* 新增面试监控样式 */
.interview-monitor {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  padding: 20px;
  height: 100%;
  align-items: center;
  justify-content: center;
}

.monitor-item {
  background: rgba(7, 118, 181, 0.3);
  border: 1px solid rgba(7, 118, 181, 0.7);
  border-radius: 8px;
  padding: 20px;
  width: 200px;
  text-align: center;
  transition: all 0.3s ease;
}

.monitor-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(7, 118, 181, 0.5);
}

.monitor-item h3 {
  color: #fff;
  margin-bottom: 10px;
  font-size: 16px;
}

.monitor-item p {
  color: rgba(255, 255, 255, 0.8);
  margin: 5px 0;
  font-size: 12px;
}

.status {
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 12px;
  margin-top: 10px;
  font-weight: bold;
}

.status.running {
  background: #4CAF50;
  color: white;
}

.status.completed {
  background: #2196F3;
  color: white;
}

.status.waiting {
  background: #FF9800;
  color: white;
}

.monitor-item.active {
  border-color: #4CAF50;
  box-shadow: 0 0 10px rgba(76, 175, 80, 0.5);
}
.aa-center {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  position: relative;
  overflow: hidden;
  background-image: url('~@/assets/achievement-aggregation/ac-bj.jpg');
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
}

.vlc-top-realtime-data {
  position: absolute;
  top: 30px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 36px;
  color: #fff;
  font-weight: bold;
  z-index: 10;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.5);
}

.aac-animation-imgs {
  width: 100%;
  height: 100%;
  position: relative;
}

.aacai-mv {
  width: 1000px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
}

.aacaim-light {
  width: 800px;
  position: absolute;
  top: 45%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 2;
  animation: aacaimlight 2s infinite;
}

.aacaim-building {
  width: 400px;
  position: absolute;
  top: 45%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 3;
  animation: hover-animation1 3s infinite linear;
}

.aaca-circle {
  /* position: absolute; */
  top: 121px;
  left: 62px;
  width: 100%;
  height: 50%;
  border-radius: 50%;
  z-index: 4;
}

.circle {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  position: absolute;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.circle:hover {
  transform: scale(1.1) !important;
}

.circle-img-pop {
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.circle-img-bg {
  width: 80%;
  height: 80%;
  position: absolute;
  top: 10%;
  left: 10%;
  border-radius: 50%;
}

.circle-img-bg-icon {
  width: 40%;
  height: 40%;
  position: absolute;
  top: 30%;
  left: 30%;
}

.circle-text-main {
  position: absolute;
  top: 110%;
  left: 50%;
  transform: translateX(-50%);
  font-size: 16px;
  color: #fff;
  font-weight: bold;
  white-space: nowrap;
  text-align: center;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.7);
}

/* 原来的2D平面旋转动画 */
.circle1 {
  animation: rotate-circle 25s linear infinite;
  top: 50%;
  left: 50%;
  transform: translateX(-50%) translateY(-50%) rotate(0deg) translateX(300px) rotate(0deg);
}

.circle2 {
  animation: rotate-circle 25s linear infinite;
  animation-delay: -3.57s;
  top: 50%;
  left: 50%;
  transform: translateX(-50%) translateY(-50%) rotate(51.43deg) translateX(300px) rotate(-51.43deg);
}

.circle3 {
  animation: rotate-circle 25s linear infinite;
  animation-delay: -7.14s;
  top: 50%;
  left: 50%;
  transform: translateX(-50%) translateY(-50%) rotate(102.86deg) translateX(300px) rotate(-102.86deg);
}

.circle4 {
  animation: rotate-circle 25s linear infinite;
  animation-delay: -10.71s;
  top: 50%;
  left: 50%;
  transform: translateX(-50%) translateY(-50%) rotate(154.29deg) translateX(300px) rotate(-154.29deg);
}

.circle5 {
  animation: rotate-circle 25s linear infinite;
  animation-delay: -14.29s;
  top: 50%;
  left: 50%;
  transform: translateX(-50%) translateY(-50%) rotate(205.71deg) translateX(300px) rotate(-205.71deg);
}

.circle6 {
  animation: rotate-circle 25s linear infinite;
  animation-delay: -17.86s;
  top: 50%;
  left: 50%;
  transform: translateX(-50%) translateY(-50%) rotate(257.14deg) translateX(300px) rotate(-257.14deg);
}

.circle7 {
  animation: rotate-circle 25s linear infinite;
  animation-delay: -21.43s;
  top: 50%;
  left: 50%;
  transform: translateX(-50%) translateY(-50%) rotate(308.57deg) translateX(300px) rotate(-308.57deg);
}

@keyframes rotate-circle {
  0% {
    transform: translateX(-50%) translateY(-50%) scaleY(0.7) rotate(0deg) translateX(300px) rotate(0deg);
  }
  100% {
    transform: translateX(-50%) translateY(-50%) scaleY(0.7) rotate(360deg) translateX(300px) rotate(-360deg);
  }
}

@keyframes aacaimlight {
  0%, 100% {
    opacity: 0.3;
  }
  50% {
    opacity: 1;
  }
}

@keyframes hover-animation1 {
  0%, 100% {
    transform: translate(-50%, -50%) translateY(0px);
  }
  50% {
    transform: translate(-50%, -50%) translateY(-10px);
  }
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .aacai-mv {
    width: 1000px;
  }
  
  .aacaim-light {
    width: 600px;
  }
  
  @keyframes rotate-circle {
    0% {
      transform: translateX(-50%) translateY(-50%) scaleY(0.7) rotate(0deg) translateX(250px) rotate(0deg);
    }
    100% {
      transform: translateX(-50%) translateY(-50%) scaleY(0.7) rotate(360deg) translateX(250px) rotate(-360deg);
    }
  }
}

</style>