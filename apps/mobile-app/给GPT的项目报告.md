# 缁?GPT 鐨勯」鐩姤鍛婏細app 涓?ISLab 鍚庡彴绠＄悊椤圭洰鎺ュ叆

## 1. 鑳屾櫙

鎴戞湁涓や釜鐜版垚椤圭洰锛屾兂鎶婁笟鍔＄ app 鍜屽悗鍙扮鐞嗛」鐩帴鍦ㄤ竴璧枫€?
app 椤圭洰璺緞锛?
```text
D:\瀛︽牎姣旇禌椤圭洰\浜掕仈缃?\鏅ㄥ鍥涜瘖杈╁吇褰昞鏅ㄥ鍥涜瘖杈╁吇褰?```

鍚庡彴/绠＄悊椤圭洰璺緞锛?
```text
D:\瀛︽牎姣旇禌椤圭洰\浜掕仈缃?\ISLab
```

鐩爣鏄細

1. app 涓嶅啀鍙緷璧栨湰鍦板亣鏁版嵁銆?2. app 鑳介€氳繃鐪熷疄鎺ュ彛鑾峰彇鍜屾彁浜ゆ暟鎹€?3. 鍚庡彴绠＄悊绔兘鐪嬪埌鍜岀淮鎶?app 浣跨敤鐨勬暟鎹€?4. 鐧诲綍閴存潈銆佹暟鎹ā鍨嬨€佹帴鍙ｈ鑼冮€愭缁熶竴銆?5. 淇濈暀鐜版湁椤甸潰鍜屼氦浜掞紝鍏堟渶灏忔帴閫氾紝鍐嶉€愭鏀归€犮€?
## 2. 褰撳墠椤圭洰鍒嗘瀽缁撴灉

### app

app 鏄?`uni-app + Vue3` 椤圭洰銆?
涓昏鏂囦欢锛?
```text
main.js
App.vue
pages.json
manifest.json
vite.config.js
pages/
common/
static/
```

鏍稿績涓氬姟椤甸潰锛?
```text
pages/Login/Login.vue              鐧诲綍
pages/register/register.vue        娉ㄥ唽
pages/guide/guide.vue              鍋ュ悍淇℃伅閲囬泦
pages/report/report.vue            鍋ュ悍鎶ュ憡
pages/health-plan/health-plan.vue  鍋ュ悍璁″垝
pages/expo/expo.vue                鍋ュ悍鍗氳/鐭ヨ瘑搴撳垪琛?pages/details/details.vue          鐭ヨ瘑搴撹鎯?pages/photo/photo.vue              鑸岃瘖/闈㈣瘖閲囬泦
pages/voiceprint/voiceprint.vue    澹扮汗閲囬泦
pages/result/result.vue            缁撴灉灞曠ず
pages/my/my.vue                    涓汉涓績
```

app 褰撳墠闂锛?
1. 鐧诲綍娉ㄥ唽鏄湰鍦版ā鎷燂紝浣跨敤 `uniStorage` 淇濆瓨 `users/loginType/userInfo`銆?2. 鍋ュ悍閲囬泦銆佹姤鍛娿€佽鍒掔瓑鏍稿績鏁版嵁瀛樺湪鏈湴锛歚userGuide/faceData/hishory`銆?3. 椤甸潰涓洿鎺ュ啓 `uni.request` 鍜?`uni.uploadFile`锛岀己缁熶竴璇锋眰灞傘€?4. 澶氬鍐欐鏃у叕缃戝湴鍧€锛?
   ```text
   http://your-server-host:8203/system
   http://your-server-host:8204/prod-api
   http://your-server-host:8089/faceAi
   http://your-server-host:8089/iseC
   ```

5. mock 鏁版嵁鍜岀湡瀹炴帴鍙ｆ贩鍦ㄤ竴璧枫€?
### ISLab

`D:\瀛︽牎姣旇禌椤圭洰\浜掕仈缃?\ISLab` 褰撳墠璇诲埌鐨勬槸 `Vue3 + Vite + Element Plus + Pinia` 绠＄悊绔墠绔」鐩紝涓嶆槸瀹屾暣鍚庣婧愮爜銆?
涓昏鏂囦欢锛?
```text
package.json
vite.config.js
src/main.js
src/router/index.js
src/utils/request.js
src/views/
test-backend.ps1
```

ISLab 璇锋眰灏佽锛?
```js
baseURL: '/api/system'
```

ISLab Vite 浠ｇ悊锛?
```js
'/api' -> 'http://127.0.0.1:8080'
```

娌℃湁鎵惧埌锛?
```text
pom.xml
Controller.java
Service.java
Mapper.java
Mapper.xml
*.sql
Swagger/OpenAPI
```

鎵€浠ョ洰鍓嶄笉鑳界洿鎺ヤ慨鏀瑰悗绔?controller/service/mapper锛屽彧鑳芥牴鎹?ISLab 鍓嶇浣跨敤鐨勬帴鍙ｅ绾﹁繘琛?app 鎺ュ叆銆?
## 3. 宸茬煡鍚庣鎺ュ彛濂戠害

浠?ISLab 鍓嶇鍜?`test-backend.ps1` 鐪嬪埌杩欎簺鎺ュ彛锛?
### 鐭ヨ瘑搴撴帴鍙?
```text
GET /system/article/list
GET /system/article/{id}

GET /system/zhonogyaoshipu/list
GET /system/zhonogyaoshipu/{id}

GET /system/commondiseases/list
GET /system/commondiseases/{id}

GET /system/prescriptionofdrugs/list
GET /system/prescriptionofdrugs/{id}

GET /system/video/list
GET /system/video/{id}
```

app 鍘熶唬鐮佷腑杩樻湁锛?
```text
GET /system/chinesemedicine/list
GET /system/chinesemedicine/{id}
```

杩欓噷瀛樺湪鎺ュ彛鍛藉悕涓嶄竴鑷达紝闇€瑕佺‘璁や互鍚庣鍝釜鎺ュ彛涓哄噯銆?
### 鐥呬緥/鍥涜瘖璁粌鎺ュ彛

```text
GET  /system/case/list
POST /system/case

GET  /system/question/list

POST /system/records/addPlus
POST /system/detail/addList
POST /system/records/tcmCaseRecordsResult

GET  /system/records/recordReult
GET  /system/records/getCharts
```

### AI 鍒嗘瀽鎺ュ彛绾跨储

ISLab 涓湁锛?
```text
GET /system/ai/faceAiPlus
```

app 涓洰鍓嶇洿鎺ヨ皟鐢ㄥ閮ㄦ湇鍔★細

```text
POST http://your-server-host:8089/faceAi
POST http://your-server-host:8089/iseC
```

寤鸿鍚庣画鐢卞悗绔唬鐞?AI 鏈嶅姟锛屼笉璁?app 鐩磋繛澶栭儴 AI 鏈嶅姟銆?
## 4. 宸茬粡鍋氳繃鐨勬渶灏忔帴鍏ユ敼鍔?
涓轰簡鍏堣窇閫氣€渁pp 鍋ュ悍鍗氳璇诲彇鍚庡彴鐭ヨ瘑搴撯€濊繖鏉℃渶灏忛摼璺紝宸插湪 app 椤圭洰閲屾柊澧?淇敼濡備笅鏂囦欢銆?
鏂板锛?
```text
common/config.js
common/request.js
common/api.js
```

浣滅敤锛?
1. `common/config.js`锛氱粺涓€ API 鍜岃祫婧?baseURL銆?2. `common/request.js`锛氱粺涓€ `uni.request`锛岃嚜鍔ㄦ敞鍏?token锛屽鐞?401銆?3. `common/api.js`锛氬皝瑁呯煡璇嗗簱鍒楄〃/璇︽儏鎺ュ彛锛屽鐞嗚祫婧?URL銆?
淇敼锛?
```text
vite.config.js
pages/expo/expo.vue
pages/details/details.vue
```

浣滅敤锛?
1. `vite.config.js` 澧炲姞 H5 浠ｇ悊锛?
   ```js
   '/api' -> 'http://127.0.0.1:8080'
   ```

2. `pages/expo/expo.vue` 涓嶅啀浣跨敤鏃х殑 `your-server-host:8203/8204` 閰嶇疆锛屾敼鎴愮粺涓€ baseURL銆?3. `pages/details/details.vue` 璇︽儏鎺ュ彛鏀逛负璧扮粺涓€ API 灞傘€?
宸茬粡楠岃瘉锛?
```powershell
npm run build:h5
```

缁撴灉锛?
```text
Build complete.
```

娉ㄦ剰锛氬綋鍓嶆湰鏈?`127.0.0.1:8080` 鍚庣娌℃湁鍚姩锛屾墍浠ュ彧瀹屾垚浜嗙紪璇戦獙璇侊紝灏氭湭瀹屾垚鐪熷疄鎺ュ彛鑱旇皟銆?
## 5. 鎺ㄨ崘鎬讳綋鏋舵瀯

寤鸿鍒嗘垚涓夌被鎺ュ彛锛?
```text
/system/*
```

鍚庡彴绠＄悊绔帴鍙ｏ紝渚?ISLab 浣跨敤锛屼繚鐣?RuoYi 椋庢牸銆?
```text
/app/*
```

涓氬姟绔?app 鎺ュ彛锛岄潰鍚戠Щ鍔ㄧ锛岄伩鍏?app 琚悗鍙扮鐞嗙粨鏋勭粦姝汇€?
```text
/common/*
```

鍏叡鎺ュ彛锛屼緥濡傜櫥褰曘€佷笂浼犮€佸瓧鍏搞€佹枃浠惰闂€?
鎺ㄨ崘鏁版嵁娴侊細

```text
ISLab 鍚庡彴缁存姢鐭ヨ瘑搴?鐥呬緥/鎶ュ憡
  -> 鍚庣淇濆瓨鏁版嵁搴?  -> app 璇诲彇灞曠ず

app 閲囬泦鐢ㄦ埛鍋ュ悍淇℃伅/鍥剧墖/闊抽
  -> 鍚庣淇濆瓨閲囬泦璁板綍
  -> 鍚庣璋冪敤 AI
  -> 鍚庣淇濆瓨鎶ュ憡
  -> app 鏌ョ湅鎶ュ憡鍜岃鍒?  -> ISLab 鍚庡彴鏌ョ湅涓庣鐞?```

## 6. 鍒嗛樁娈佃鍒?
### P0锛氭懜娓呯粨鏋勫拰鎺ュ叆鐐?
宸插畬鎴愩€?
缁撹锛?
1. app 鏄?uni-app銆?2. ISLab 褰撳墠鏄鐞嗙鍓嶇銆?3. 娌℃湁鍙戠幇鍚庣婧愮爜銆?4. 宸叉壘鍒版帴鍙ｅ绾﹀拰 app 鐨勬湰鍦?mock 鏁版嵁鐐广€?
### P1锛氭渶灏忎笟鍔￠棴鐜?
鐩爣锛?
app 鍋ュ悍鍗氳璇诲彇鍚庡彴鐭ヨ瘑搴撱€?
宸插仛锛?
1. 鏂板 app 璇锋眰灞傘€?2. 鏂板 app API adapter銆?3. H5 澧炲姞 `/api` 浠ｇ悊銆?4. 鍋ュ悍鍗氳鍜岃鎯呭紑濮嬫帴鍏ョ粺涓€鎺ュ彛銆?
鍚庣画闇€瑕侊細

鍚姩 `127.0.0.1:8080` 鍚庣锛岄獙璇侊細

```text
GET /system/article/list
GET /system/zhonogyaoshipu/list
GET /system/commondiseases/list
GET /system/video/list
```

### P2锛氱粺涓€鐧诲綍閴存潈

鐩爣锛?
app 涓嶅啀鐢ㄦ湰鍦?`users/loginType`銆?
寤鸿鎺ュ彛锛?
```text
POST /app/auth/login
POST /app/auth/register
GET  /app/user/profile
POST /app/auth/logout
```

濡傛灉鐩存帴澶嶇敤 RuoYi锛?
```text
POST /login
GET  /getInfo
```

app token 淇濆瓨锛?
```text
token
userInfo
```

璇锋眰澶达細

```text
Authorization: Bearer <token>
```

### P3锛氳縼绉绘牳蹇冧笟鍔℃暟鎹?
鐩爣锛?
鍋ュ悍閲囬泦銆佹姤鍛娿€佽鍒掑叆搴撱€?
寤鸿鎺ュ彛锛?
```text
POST /app/assessment
POST /app/assessment/{id}/upload
POST /app/assessment/{id}/analyze
GET  /app/report/latest
GET  /app/report/{id}
GET  /app/plan/current
PUT  /app/plan/item/{id}/complete
```

闇€瑕佹敼锛?
```text
pages/guide/guide.vue
pages/report/report.vue
pages/photo/photo.vue
pages/voiceprint/voiceprint.vue
pages/result/result.vue
pages/health-plan/health-plan.vue
common/api.js
```

### P4锛氬悗鍙扮鐞嗗姛鑳借ˉ榻?
鐩爣锛?
ISLab 鍚庡彴鍙互鐪嬪拰绠?app 鏁版嵁銆?
寤鸿鏂板锛?
```text
app 鐢ㄦ埛绠＄悊
鍋ュ悍閲囬泦璁板綍绠＄悊
鍋ュ悍鎶ュ憡绠＄悊
鍋ュ悍璁″垝绠＄悊
AI 璋冪敤鏃ュ織
鏂囦欢涓婁紶璁板綍
```

濡傛灉鍚庣鏄?RuoYi锛?
1. 绠＄悊绔帴鍙ｆ斁 `/system/app/*`銆?2. app 鎺ュ彛鏀?`/app/*`銆?3. 澶嶇敤 RuoYi 鐢ㄦ埛銆佽鑹层€佽彍鍗曘€佸瓧鍏搞€佷笂浼犮€佹搷浣滄棩蹇椼€?
### P5锛氫笂绾垮噯澶?
闇€瑕佽ˉ锛?
1. dev/test/prod 鐜閰嶇疆銆?2. H5 鍜?App 鐪熸満 baseURL 鍖哄垎銆?3. CORS 鍜?Nginx 鍙嶄唬銆?4. token 杩囨湡澶勭悊銆?5. 涓婁紶澶у皬鍜岀被鍨嬮檺鍒躲€?6. 閿欒鐮佸拰杩斿洖缁撴瀯缁熶竴銆?7. 鏃堕棿鏍煎紡缁熶竴銆?8. 瀛楁鍛藉悕缁熶竴銆?9. 鏃ュ織瀹¤鍜岄殣绉佹暟鎹繚鎶ゃ€?
## 7. 闇€瑕?GPT 甯繖鐨勯噸鐐?
璇峰熀浜庝互涓婇」鐩幇鐘讹紝甯垜杩涗竴姝ヤ骇鍑轰竴浠藉彲鎵ц鐨勫悗缁帴鍏ユ彁绀鸿瘝/鏂规锛岄噸鐐瑰洖绛旓細

1. 鍦ㄧ己灏戝悗绔簮鐮佺殑鎯呭喌涓嬶紝濡備綍鏈€绋冲Ε鍦版帹杩?app 涓?ISLab 鐨勮仈璋冿紵
2. 濡傛灉鍚庣瀹為檯鏄?RuoYi锛屽簲璇ュ浣曡璁?app 涓撶敤鎺ュ彛灞傦紝閬垮厤 app 琚?`/system/*` 绠＄悊绔帴鍙ｇ粦姝伙紵
3. app 鐢ㄦ埛鍒板簳搴旇澶嶇敤 `sys_user`锛岃繕鏄崟鐙缓 `app_user`锛?4. 鍋ュ悍閲囬泦銆丄I 鎶ュ憡銆佸仴搴疯鍒掑簲璇ュ浣曡璁¤〃缁撴瀯鍜?DTO/VO锛?5. app 渚у簲璇ュ浣曠户缁敼閫?`common/request.js`銆乣common/api.js`锛岃鍚庣画椤甸潰閫愭鏇挎崲鏁版嵁鏉ユ簮锛?6. P2 鐧诲綍閴存潈搴旇浼樺厛鎺?`/login/getInfo`锛岃繕鏄柊寤?`/app/auth/login`锛?7. AI 鏈嶅姟 `faceAi/iseC` 搴旇濡備綍鐢卞悗绔唬鐞嗭紝閬垮厤 app 鐩磋繛澶栭儴鏈嶅姟锛?8. 缁欏嚭涓嬩竴姝ユ渶灏忎唬鐮佹敼閫犱换鍔℃竻鍗曪紝瑕佹眰鍙洖閫€銆佷綆椋庨櫓銆?
## 8. 绾︽潫

璇烽伒瀹堣繖浜涘師鍒欙細

1. 鍏堝吋瀹瑰啀閲嶆瀯銆?2. 涓嶆帹缈荤幇鏈夐〉闈㈠拰浜や簰銆?3. 鍏堟浛鎹㈡暟鎹潵婧愶紝鍐嶉噸鏋勭粨鏋勩€?4. 鏂板 adapter/service/api 灞傦紝閬垮厤椤甸潰缁х画鍐欐鎺ュ彛銆?5. 绠＄悊绔帴鍙ｃ€乤pp 鐢ㄦ埛鎺ュ彛銆佸叕鍏辨帴鍙ｆ槑纭尯鍒嗐€?6. 鍚庣杩斿洖缁撴瀯缁熶竴澶勭悊锛屼笉瑕佹瘡涓〉闈㈤噸澶嶈В鏋愩€?7. token 娉ㄥ叆銆?01銆侀敊璇彁绀虹粺涓€澶勭悊銆?8. 鏀瑰姩鍙洖閫€锛屽厛璺戦€氫竴涓棴鐜啀鎵╁睍銆?

