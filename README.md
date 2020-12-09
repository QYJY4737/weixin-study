# weixin-study
<p>
	微信公众号相关开发demo
</p>
公众号配置
1.服务器部署微信相关服务及配置
1.1配置域名
1.2配置微信回调地址
例:域名/weserver/v1/auth/unauth（回调地址需支持80或443端口）
1.3微信web配置后端访问地址：后端网关地址
注：此处配置需注意微信web必须走node
1.4数据库sys_unauth_url_config表添加\S*weserver/v1/auth/unauth\S*回调地址
1.5服务配置中心配置公众号相关参数
1.6网关地址
2.公众平台配置
2.1登陆微信公众平台：
https://mp.weixin.qq.com/cgi-bin/home
2.2开发菜单->基本配置
公众号开发信息->ip白名单：服务公网地址
填写服务器配置
2.3设置菜单->公众号配置->公众号设置
下载MP_verify_gazDWG9nSdkEpJ2I.txt文件上传至填写域名或路径指向的web服务器
配置业务域名、JS接口安全域名、网页授权域名
2.4配置公众号菜单
点击菜单开发者工具->在线调试接口->获取access_token->复制access_token
点击菜单开发者工具->在线调试接口->自定义菜单
菜单body例：
{
  "button": [{
    "type": "view",
    "name": "医院主页",
    "url": "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx5a3880cc5b174340&redirect_uri=http%3A%2F%2Fairhospital.jdyfy.com%2F%23%2Fhome&response_type=code&scope=snsapi_base&state=123#wechat_redirect",
    "sub_button": []
  }, {
    "name": "健康中心",
    "sub_button": [{
      "type": "view",
      "name": "预约挂号",
      "url": "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx5a3880cc5b174340&redirect_uri=http%3A%2F%2Fairhospital.jdyfy.com%2F%23%2Fappointment%2FdepTree%3FshowFooter%3Dfalse&response_type=code&scope=snsapi_base&state=123#wechat_redirect",
      "sub_button": []
    },{
      "type": "view",
      "name": "东院预约挂号",
      "url": "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx5a3880cc5b174340&redirect_uri=http%3A%2F%2Fairhospital.jdyfy.com%2F%23%2Fcourtyard%2FdepIntro%2FdeptList&response_type=code&scope=snsapi_base&state=123#wechat_redirect",
      "sub_button": []
    },{
      "type": "view",
      "name": "智能导诊",
      "url": "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx5a3880cc5b174340&redirect_uri=http%3A%2F%2Fairhospital.jdyfy.com%2F%23%2FhosGuide%2FguideBody&response_type=code&scope=snsapi_base&state=123#wechat_redirect",
      "sub_button": []
    }, {
      "type": "view",
      "name": "智慧好医院",
      "url": "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx5a3880cc5b174340&redirect_uri=http%3A%2F%2Fairhospital.jdyfy.com%2F%23%2Fdownload&response_type=code&scope=snsapi_base&state=123#wechat_redirect",
      "sub_button": []
    }]
  }, {
    "name": "个人中心",
    "sub_button": [{
      "type": "view",
      "name": "我的资料",
      "url": "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx5a3880cc5b174340&redirect_uri=http%3A%2F%2Fairhospital.jdyfy.com%2F%23%2Fuser%2Fpersonal%2Fprofile&response_type=code&scope=snsapi_base&state=123#wechat_redirect",
      "sub_button": []
    }, {
      "type": "view",
      "name": "缴费记录",
      "url": "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx5a3880cc5b174340&redirect_uri=http%3A%2F%2Fairhospital.jdyfy.com%2F%23%2Fpayment%2Fquery%3Fnext%3Dcategory%26showFooter%3Dfalse&response_type=code&scope=snsapi_base&state=123#wechat_redirect",
      "sub_button": []
    }, {
      "type": "view",
      "name": "挂号记录",
      "url": "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx5a3880cc5b174340&redirect_uri=http%3A%2F%2Fairhospital.jdyfy.com%2F%23%2Fuser%2Fpersonal%2FregisterRecord&response_type=code&scope=snsapi_base&state=123#wechat_redirect",
      "sub_button": []
    },{
      "type": "view", 
      "name": "器官捐献", 
      "url": "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx5a3880cc5b174340&redirect_uri=http%3A%2F%2Fairhospital.jdyfy.com%2F%23%2Forgan%2Fintroduce&response_type=code&scope=snsapi_base&state=123#wechat_redirect", 
      "sub_button": []
        }]
  }]
} 
 
微信支付账号相关开通
1.商户号
查询入口：
微信商户平台-账户中心-商户信息-微信支付商户号
2.微信商户号绑定应用APPID
申请入口：
微信商户平台-产品中心-APPID授权管理
点击
绑定公众号APPID（wx2c1d77a1bf6feefa），注意如果是待确认状态，需要登录微信开放平台授权
3.商户秘钥key
申请入口：
微信商户平台-账户设置-API安全-密钥设置
4.API证书
申请入口：
微信商户平台(pay.weixin.qq.com)-账户中心-账户设置-API安全 （apiclient_cert.p12文件）
微信支付接口中，涉及资金回滚的接口会使用到API证书，包括退款、撤销接口。商家在申请微信支付成功后，收到的相应邮件后，可以按照指引下载API证书，也可以按照以下路径下载：微信商户平台(pay.weixin.qq.com)-->账户中心-->账户设置-->API安全 （apiclient_cert.p12文件）。
公众号和小程序支付
1.秘钥AppSecret
查询入口：
登录微信公众平台-基本配置-查看开发者密码（AppSecret）
未使用过，点击重置即可配置，之前使用过公众号开发，需找到原来使用过的对应的AppSecret
2.支付授权目录
申请入口：
微信商户平台-产品中心-开发配置-支付授权目录
对于公众号或者小程序支付需要配置支付授权的目录

公众号配置导航地址
第一步骤，关联小程序
1. 进入公众号后台管理，点击小程序管理。
2. 点击添加
3. 点击关联小程序。用管理员扫描二维码。
4. 添加要关联的小程序 ，输入“亚米导航服务”小程序名称搜索。
5. 搜索到之后选定点击下一步，第三方小程序需要管理员扫描授权。
6. 关联完成
第二步，新增自定义菜单
1. 进入公众号，点击自定义菜单。
2．添加子菜单。
3. 填写菜单名称为“院内导航”。
4. 子菜单内容选择“跳转小程序”，选择已绑定好的“亚米导航服务”。
5. 填写小程序路径。pages/index/index?bdgId=医院id
6. 填写h5备用链接
https://daohang.yamimap.com/wechatpro/#/index?bdgId=医院id
 
