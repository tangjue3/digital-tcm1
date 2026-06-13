import { get, post } from '@/utils/request'

export function login(username, password, code, uuid) {
  return post('/login', {
    username,
    password,
    code,
    uuid
  }, {
    headers: {
      isToken: false
    }
  })
}

export function getCodeImg() {
  return get('/captchaImage', {}, {
    headers: {
      isToken: false
    },
    timeout: 20000
  })
}

export function getInfo() {
  return get('/getInfo')
}

export function logout() {
  return post('/logout')
}
