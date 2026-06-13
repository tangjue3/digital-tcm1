const mockUsers = [
  { id: 1, username: 'admin', password: 'admin123' },
  { id: 2, username: 'doctor', password: 'doctor123' },
]

const mockRecords = {
  code: 200,
  rows: [
    {
      remark: '中医诊断模拟',
      userId: '张三',
      score: 95,
      time: '2026-04-27 09:30:00',
      conclusion: '肝郁脾虚证',
      treatmentMethods: '疏肝健脾，理气和胃',
      caseNo: 'CASE001',
      questionNo: 'Q001'
    },
    {
      remark: '望闻问切练习',
      userId: '李四',
      score: 88,
      time: '2026-04-26 14:20:00',
      conclusion: '气血两虚证',
      treatmentMethods: '益气养血',
      caseNo: 'CASE002',
      questionNo: 'Q002'
    },
    {
      remark: '四诊综合分析',
      userId: '王五',
      score: 92,
      time: '2026-04-25 10:15:00',
      conclusion: '痰湿内阻证',
      treatmentMethods: '燥湿化痰，理气健脾',
      caseNo: 'CASE003',
      questionNo: 'Q003'
    }
  ]
}

const mockCaptcha = {
  captchaEnabled: false,
  img: '',
  uuid: 'mock-uuid-12345'
}

const mockLoginResponse = {
  code: 200,
  token: 'mock-jwt-token-for-testing-only-12345',
  msg: '登录成功'
}

export function mockRequest(url, method = 'GET', data = {}) {
  return new Promise((resolve) => {
    setTimeout(() => {
      if (url === '/captchaImage') {
        resolve(mockCaptcha)
      } else if (url === '/login' && method === 'POST') {
        const user = mockUsers.find(u => u.username === data.username && u.password === data.password)
        if (user) {
          resolve(mockLoginResponse)
        } else {
          resolve({ code: 500, msg: '用户名或密码错误' })
        }
      } else if (url === '/system/records/list') {
        resolve(mockRecords)
      } else {
        resolve({ code: 200, data: {}, msg: 'success' })
      }
    }, 300)
  })
}

export default {
  mockRequest,
  mockUsers,
  mockRecords,
  mockCaptcha,
  mockLoginResponse
}
