$(window).on('load', function () {
    // 页面加载完成后，隐藏加载提示元素
    $(".loading").fadeOut();
});

$(function () {
    // 初始化各个图表
    initEchart1();
    initEchart2();
    initEchart3();
    initEchart4();
    initEchart5();
    // initEchart6();

    initZb1();
    initZb2();
    initZb3();
    initWordCloud(); // 初始化词云
});

function initEchart1() {
    var a = echarts.init(document.getElementById("echart1"));
    var option = {
        tooltip: {
            // 鼠标悬停时触发提示框
            trigger: "item",
            // 提示框内容格式化
            formatter: "{b} : {c} ({d}%)"
        },
        legend: {
            // 图例位置在右侧，距离顶部30像素
            right: 0,
            top: 30,
            height: 160,
            itemWidth: 10,
            itemHeight: 10,
            itemGap: 10,
            textStyle: {
                color: "rgba(255, 255, 255, 0.6)",
                fontSize: 12
            },
            orient: "vertical",
            // 中医案例类型相关图例
            data: ["风寒感冒案例占比", "风热感冒案例占比", "脾胃虚弱案例占比", "肝郁气滞案例占比", "肾虚案例占比"]
        },
        calculable: true,
        series: [{
            name: "中医案例类型占比",
            color: ["#62c98d", "#2f89cf", "#4cb9cf", "#53b666", "#62c98d"],
            type: "pie",
            // 饼图半径范围
            radius: [30, 70],
            // 饼图中心位置
            center: ["35%", "50%"],
            roseType: "radius",
            label: {
                normal: {
                    show: true
                },
                emphasis: {
                    show: true
                }
            },
            labelLine: {
                normal: {
                    show: true
                },
                emphasis: {
                    show: true
                }
            },
            // 模拟的案例类型占比数据，可根据实际情况替换
            data: [{
                value: 10,
                name: "风寒感冒案例占比"
            }, {
                value: 5,
                name: "风热感冒案例占比"
            }, {
                value: 15,
                name: "脾胃虚弱案例占比"
            }, {
                value: 25,
                name: "肝郁气滞案例占比"
            }, {
                value: 20,
                name: "肾虚案例占比"
            }]
        }]
    };
    a.setOption(option);
    window.addEventListener("resize", function () {
        // 窗口大小改变时，重新调整图表尺寸
        a.resize();
    });
    a.on('click', function () {
        // 点击图表时，打开指定链接
        window.open("https://gitee.com/iGaoWei/big-data-view");
    });
}

function initEchart2() {
    var a = echarts.init(document.getElementById("echart2"));
    var option = {
        tooltip: {
            trigger: "item",
            formatter: "{b} : {c} ({d}%)"
        },
        legend: {
            // 图例位于顶部15%位置
            top: "15%",
            // 中医四诊及综合分析相关图例
            data: ["望诊分析占比", "闻诊分析占比", "问诊分析占比", "切诊分析占比", "综合分析占比"],
            icon: "circle",
            textStyle: {
                color: "rgba(255, 255, 255, 0.6)",
            }
        },
        calculable: true,
        series: [{
            name: "中医四诊分析占比",
            color: ["#62c98d", "#2f89cf", "#4cb9cf", "#53b666", "#62c98d"],
            type: "pie",
            startAngle: 0,
            radius: [51, 100],
            center: ["50%", "45%"],
            roseType: "area",
            avoidLabelOverlap: false,
            label: {
                normal: {
                    show: true,
                },
                emphasis: {
                    show: true
                }
            },
            labelLine: {
                normal: {
                    show: true,
                    length2: 1
                },
                emphasis: {
                    show: true
                }
            },
            // 模拟的四诊及综合分析占比数据，可替换
            data: [{
                value: 1,
                name: "望诊分析占比",
            }, {
                value: 4,
                name: "闻诊分析占比",
            }, {
                value: 5,
                name: "问诊分析占比",
            }, {
                value: 6,
                name: "切诊分析占比",
            }, {
                value: 9,
                name: "综合分析占比",
            }, {
                value: 0,
                name: "",
                label: {
                    show: false
                },
                labelLine: {
                    show: false
                }
            }, {
                value: 0,
                name: "",
                label: {
                    show: false
                },
                labelLine: {
                    show: false
                }
            }, {
                value: 0,
                name: "",
                label: {
                    show: false
                },
                labelLine: {
                    show: false
                }
            }, {
                value: 0,
                name: "",
                label: {
                    show: false
                },
                labelLine: {
                    show: false
                }
            }, {
                value: 0,
                name: "",
                label: {
                    show: false
                },
                labelLine: {
                    show: false
                }
            }]
        }]
    };
    a.setOption(option);
    window.addEventListener("resize", function () {
        a.resize();
    });
    a.on('click', function () {
        window.open("https://gitee.com/iGaoWei/big-data-view");
    });
}

function initEchart3() {
    var a = echarts.init(document.getElementById("echart3"));
    var option = {
        tooltip: {
            trigger: "axis",
            axisPointer: {
                lineStyle: {
                    color: "#57617B"
                }
            }
        },
        legend: {
            // 显示学习人数和案例练习完成数的图例
            data: ["学习人数", "案例练习完成数"],
            top: "0",
            textStyle: {
                color: "#fff"
            },
            itemGap: 200,
        },
        grid: {
            left: "0",
            right: "20",
            top: "10",
            bottom: "20",
            containLabel: true
        },
        xAxis: [{
            type: "category",
            boundaryGap: false,
            axisLabel: {
                show: true,
                textStyle: {
                    color: "rgba(255, 255, 255, 0.6)"
                }
            },
            axisLine: {
                lineStyle: {
                    color: "rgba(255, 255, 255, 0.1)"
                }
            },
            // 以月份为时间周期
            data: ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"]
        }, {}],
        yAxis: [{
            axisLabel: {
                show: true,
                textStyle: {
                    color: "rgba(255, 255, 255, 0.6)"
                }
            },
            axisLine: {
                lineStyle: {
                    color: "rgba(255, 255, 255, 0.1)"
                }
            },
            splitLine: {
                lineStyle: {
                    color: "rgba(255, 255, 255, 0.1)"
                }
            }
        }],
        series: [{
            name: "学习人数",
            type: "line",
            smooth: true,
            symbol: "circle",
            symbolSize: 5,
            showSymbol: false,
            lineStyle: {
                normal: {
                    width: 2
                }
            },
            areaStyle: {
                normal: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                        offset: 0,
                        color: "rgba(24, 163, 64, 0.3)"
                    }, {
                        offset: 0.8,
                        color: "rgba(24, 163, 64, 0)"
                    }], false),
                    shadowColor: "rgba(0, 0, 0, 0.1)",
                    shadowBlur: 10
                }
            },
            itemStyle: {
                normal: {
                    color: "#cdba00",
                    borderColor: "rgba(137, 189, 2, 0.27)",
                    borderWidth: 12
                }
            },
            // 模拟的每月学习人数数据，可替换
            data: [220, 182, 191, 134, 150, 120, 110, 125, 145, 122, 165, 122]
        }, {
            name: "案例练习完成数",
            type: "line",
            smooth: true,
            symbol: "circle",
            symbolSize: 5,
            showSymbol: false,
            lineStyle: {
                normal: {
                    width: 2
                }
            },
            areaStyle: {
                normal: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                        offset: 0,
                        color: "rgba(39, 122, 206, 0.3)"
                    }, {
                        offset: 0.8,
                        color: "rgba(39, 122, 206, 0)"
                    }], false),
                    shadowColor: "rgba(0, 0, 0, 0.1)",
                    shadowBlur: 10
                }
            },
            itemStyle: {
                normal: {
                    color: "#277ace",
                    borderColor: "rgba(0, 136, 212, 0.2)",
                    borderWidth: 12
                }
            },
            // 模拟的每月案例练习完成数数据，可替换
            data: [120, 110, 125, 145, 122, 165, 122, 220, 182, 191, 134, 150]
        }]
    };
    a.setOption(option);
    window.addEventListener("resize", function () {
        a.resize();
    });
    a.on('click', function () {
        window.open("https://gitee.com/iGaoWei/big-data-view");
    });
}

function initEchart4() {
    var a = echarts.init(document.getElementById("echart4"));
    var option = {
        tooltip: {
            trigger: "axis",
            axisPointer: {
                lineStyle: {
                    color: "#57617B"
                }
            }
        },
        legend: {
            // 显示学生A、学生B的案例练习次数及练习完成率的图例
            data: [{
                name: "学生A案例练习次数"
            }, {
                name: "学生B案例练习次数"
            }, {
                name: "练习完成率"
            }],
            top: "0%",
            textStyle: {
                color: "rgba(255, 255, 255, 0.9)"
            }
        },
        xAxis: [{
            type: "category",
            data: ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],
            axisLine: {
                lineStyle: {
                    color: "rgba(255, 255, 255, 0.1)"
                }
            },
            axisLabel: {
                textStyle: {
                    color: "rgba(255, 255, 255, 0.6)",
                    fontSize: "14",
                },
            },
        }],
        yAxis: [{
            type: "value",
            name: "练习次数",
            min: 0,
            max: 50,
            interval: 10,
            axisLabel: {
                show: true,
            },
            axisLine: {
                lineStyle: {
                    color: "rgba(255, 255, 255, 0.4)"
                }
            },
        }, {
            type: "value",
            name: "完成率",
            show: true,
            axisLabel: {
                show: true,
            },
            axisLine: {
                lineStyle: {
                    color: "rgba(255, 255, 255, 0.4)"
                }
            },
            splitLine: {
                show: true,
                lineStyle: {
                    color: "#001e94"
                }
            },
        }],
        grid: {
            top: "10%",
            right: "30",
            bottom: "30",
            left: "30",
        },
        series: [{
            name: "学生A案例练习次数",
            type: "bar",
            data: [4, 6, 36, 6, 8, 6, 4, 6, 30, 6, 8, 12],
            barWidth: "auto",
            itemStyle: {
                normal: {
                    color: {
                        type: "linear",
                        x: 0,
                        y: 0,
                        x2: 0,
                        y2: 1,
                        colorStops: [{
                            offset: 0,
                            color: "#609db8"
                        }, {
                            offset: 1,
                            color: "#609db8"
                        }],
                        globalCoord: false
                    }
                }
            }
        }, {
            name: "学生B案例练习次数",
            type: "bar",
            data: [4, 2, 34, 6, 8, 6, 4, 2, 32, 6, 8, 18],
            barWidth: "auto",
            itemStyle: {
                normal: {
                    color: {
                        type: "linear",
                        x: 0,
                        y: 0,
                        x2: 0,
                        y2: 1,
                        colorStops: [{
                            offset: 0,
                            color: "#66b8a7"
                        }, {
                            offset: 1,
                            color: "#66b8a7"
                        }],
                        globalCoord: false
                    }
                }
            },
            barGap: "0"
        }, {
            name: "练习完成率",
            type: "line",
            yAxisIndex: 1,
            data: [100, 50, 80, 30, 90, 40, 70, 33, 100, 40, 80, 20],
            lineStyle: {
                normal: {
                    width: 2
                },
            },
            itemStyle: {
                normal: {
                    color: "#ff9900",
                    borderColor: "rgba(255, 153, 0, 0.2)",
                    borderWidth: 8
                }
            }
        }]
    };
    a.setOption(option);
    window.addEventListener("resize", function () {
        a.resize();
    });
    a.on('click', function () {
        window.open("https://gitee.com/iGaoWei/big-data-view");
    });
}

function initEchart5() {
    const cases = ['风寒感冒诊疗案例', '风热感冒诊疗案例', '脾胃虚弱诊疗案例', '肝郁气滞诊疗案例'];
    const caseTop10 = {
        '风寒感冒诊疗案例': [
            { student: '学生1', score: 95 },
            { student: '学生2', score: 92 },
            { student: '学生3', score: 90 },
            { student: '学生4', score: 88 },
            { student: '学生5', score: 85 },
            { student: '学生6', score: 82 },
            { student: '学生7', score: 80 },
            { student: '学生8', score: 78 },
            { student: '学生9', score: 75 },
            { student: '学生10', score: 72 }
        ],
        '风热感冒诊疗案例': [
            { student: '学生11', score: 93 },
            { student: '学生12', score: 91 },
            { student: '学生13', score: 89 },
            { student: '学生14', score: 87 },
            { student: '学生15', score: 84 },
            { student: '学生16', score: 81 },
            { student: '学生17', score: 79 },
            { student: '学生18', score: 77 },
            { student: '学生19', score: 74 },
            { student: '学生20', score: 71 }
        ],
        '脾胃虚弱诊疗案例': [
            { student: '学生21', score: 94 },
            { student: '学生22', score: 90 },
            { student: '学生23', score: 88 },
            { student: '学生24', score: 86 },
            { student: '学生25', score: 83 },
            { student: '学生26', score: 80 },
            { student: '学生27', score: 78 },
            { student: '学生28', score: 76 },
            { student: '学生29', score: 73 },
            { student: '学生30', score: 70 }
        ],
        '肝郁气滞诊疗案例': [
            { student: '学生31', score: 96 },
            { student: '学生32', score: 93 },
            { student: '学生33', score: 91 },
            { student: '学生34', score: 89 },
            { student: '学生35', score: 86 },
            { student: '学生36', score: 83 },
            { student: '学生37', score: 81 },
            { student: '学生38', score: 79 },
            { student: '学生39', score: 76 },
            { student: '学生40', score: 73 }
        ]
    };

    const chart = echarts.init(document.getElementById('echart5'));
    let currentCase = cases[0];

    function updateChart() {
        const option = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                },
                formatter: function (params) {
                    let str = `${params[0].axisValue}<br/>`;
                    params.forEach(item => {
                        str += `${item.seriesName}: ${item.data.student} (${item.data.score})<br/>`;
                    });
                    return str;
                }
            },
            legend: {
                data: [currentCase + ' 排名'],
                textStyle: {
                    color: 'rgba(255, 255, 255, 0.6)'
                }
            },
            xAxis: {
                type: 'category',
                data: Array.from({ length: 10 }, (_, i) => `第 ${i + 1} 名`),
                axisLabel: {
                    textStyle: {
                        color: 'rgba(255, 255, 255, 0.6)'
                    }
                },
                axisLine: {
                    lineStyle: {
                        color: 'rgba(255, 255, 255, 0.1)'
                    }
                }
            },
            yAxis: {
                type: 'value',
                name: '分数',
                axisLabel: {
                    textStyle: {
                        color: 'rgba(255, 255, 255, 0.6)'
                    }
                },
                axisLine: {
                    lineStyle: {
                        color: 'rgba(255, 255, 255, 0.1)'
                    }
                },
                splitLine: {
                    lineStyle: {
                        color: 'rgba(255, 255, 255, 0.1)'
                    }
                }
            },
            series: [{
                name: `${currentCase} 排名`,
                type: 'bar',
                data: caseTop10[currentCase].map(item => ({
                    value: item.score,
                    student: item.student
                })),
                itemStyle: {
                    normal: {
                        color: getRandomColor()
                    }
                }
            }]
        };
        chart.setOption(option);
    }

    function createSelect() {
        const select = document.getElementById('case-select');
        cases.forEach(caseName => {
            const option = document.createElement('option');
            option.value = caseName;
            option.textContent = caseName;
            select.appendChild(option);
        });
        select.addEventListener('change', () => {
            currentCase = select.value;
            updateChart();
        });
    }

    function getRandomColor() {
        const letters = '0123456789ABCDEF';
        let color = '#';
        for (let i = 0; i < 6; i++) {
            color += letters[Math.floor(Math.random() * 16)];
        }
        return color;
    }

    updateChart();
    createSelect();
    window.addEventListener('resize', () => chart.resize());
    chart.on('click', () => {
        window.open('https://gitee.com/iGaoWei/big-data-view');
    });
}

// 

function initZb1() {
    const totalStudents = 200;
    const completedStudents = 150;

    const a = echarts.init(document.getElementById("zb1"));
    const option = {
        series: [
            {
                type: "pie",
                radius: ["60%", "70%"],
                color: "#49bcf7",
                label: {
                    normal: {
                        position: 'center',
                        formatter: () => `${(completedStudents / totalStudents * 100).toFixed(2)}%`,
                        textStyle: {
                            fontSize: 30,
                            color: "#fff"
                        },
                        // 确保标签布局不被其他因素影响
                        align: 'center',
                        verticalAlign: 'middle'
                    },
                    // 强制使用正常状态的标签样式
                    emphasis: {
                        position: 'center'
                    }
                },
                data: [
                    {
                        value: completedStudents,
                        name: "学生实验完成率"
                    },
                    {
                        value: totalStudents - completedStudents,
                        label: { show: false },
                        itemStyle: { color: 'transparent' }
                    }
                ]
            }
        ]
    };
    a.setOption(option);
    window.addEventListener("resize", () => a.resize());
    a.on('click', () => {
        alert("更多模板关注公众号【DreamCoders】，回复‘BigDataView’即可获取更多\n或关注我的Gitee,网址：https://gitee.com/iGaoWei/big-data-view");
    });
}

function initZb2() {
    const completedStudents = 150;
    const totalScore = 12000;
    const fullScore = completedStudents * 100;

    const a = echarts.init(document.getElementById("zb2"));
    const option = {
        series: [
            {
                type: "pie",
                radius: ["60%", "70%"],
                color: "#cdba00",
                label: {
                    normal: {
                        position: 'center',
                        formatter: () => `${(totalScore / fullScore * 100).toFixed(2)}%`,
                        textStyle: {
                            fontSize: 30,
                            color: "#fff"
                        },
                        align: 'center',
                        verticalAlign: 'middle'
                    },
                    emphasis: {
                        position: 'center'
                    }
                },
                data: [
                    {
                        value: totalScore,
                        name: "学生得分率"
                    },
                    {
                        value: fullScore - totalScore,
                        label: { show: false },
                        itemStyle: { color: 'transparent' }
                    }
                ]
            }
        ]
    };
    a.setOption(option);
    window.addEventListener("resize", () => a.resize());
}

function initZb3() {
    const videoViews = 800;
    const targetViews = 1000;

    const a = echarts.init(document.getElementById("zb3"));
    const option = {
        series: [
            {
                type: "pie",
                radius: ["60%", "70%"],
                color: "#62c98d",
                label: {
                    normal: {
                        position: 'center',
                        formatter: () => `${(videoViews / targetViews * 100).toFixed(2)}%`,
                        textStyle: {
                            fontSize: 30,
                            color: "#fff"
                        },
                        align: 'center',
                        verticalAlign: 'middle'
                    },
                    emphasis: {
                        position: 'center'
                    }
                },
                data: [
                    {
                        value: videoViews,
                        name: "视频播放量占比"
                    },
                    {
                        value: targetViews - videoViews,
                        label: { show: false },
                        itemStyle: { color: 'transparent' }
                    }
                ]
            }
        ]
    };
    a.setOption(option);
    window.addEventListener("resize", () => a.resize());
    a.on('click', () => {
        alert("更多模板关注公众号【DreamCoders】，回复‘BigDataView’即可获取更多\n或关注我的Gitee,网址：https://gitee.com/iGaoWei/big-data-view");
    });
}

function initWordCloud() {
    var myChart = echarts.init(document.getElementById('wordcloud'));

    var option = {
        series: [{
            type: 'wordCloud',
            left: 0,
            right: 0,
            top: 0,
            right: 0,
            width: '100%',
            height: '100%',
            gridSize: 44,
            sizeRange: [10, 60],
            rotationRange: [-90, 90],
            shape: 'circle',
            textStyle: {
                normal: {
                    color: function () {
                        return 'rgb(' + [
                            Math.round(Math.random() * 160),
                            Math.round(Math.random() * 160),
                            Math.round(Math.random() * 160)
                        ].join(',') + ')';
                    }
                },
                emphasis: {
                    shadowBlur: 10,
                    shadowColor: '#333'
                }
            },
            data: [
                { name: '伤寒论', value: 100 },
                { name: '中医四诊', value: 80 },
                { name: '中药', value: 60 },
                { name: '针灸', value: 50 },
                { name: '养生', value: 40 },
                { name: '辨证论治', value: 30 },
                { name: '脾胃虚弱', value: 20 },
                { name: '肝郁气滞', value: 20 },
                { name: '肾虚', value: 20 },
                { name: '风寒感冒', value: 20 },
                { name: '风热感冒', value: 20 },
                { name: '温病学', value: 70 },
                { name: '黄帝内经', value: 75 },
                { name: '本草纲目', value: 72 },
                { name: '推拿', value: 45 },
                { name: '拔罐', value: 35 },
                { name: '艾灸', value: 42 },
                { name: '气血不足', value: 25 },
                { name: '湿气重', value: 25 },
                { name: '心火旺盛', value: 22 },
                { name: '肺阴虚', value: 22 },
                { name: '肾阴虚', value: 22 },
                { name: '肾阳虚', value: 22 },
                { name: '中药方剂', value: 55 },
                { name: '中医诊断学', value: 65 },
                { name: '中医内科学', value: 62 },
                { name: '中医外科学', value: 58 },
                { name: '中医妇科学', value: 56 },
                { name: '中医儿科学', value: 54 }
            ]
        }]
    };

    myChart.setOption(option);
    window.addEventListener("resize", function () {
        myChart.resize();
    });
}
    