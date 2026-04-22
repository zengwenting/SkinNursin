const cloud = require('wx-server-sdk')
cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
})
const db = cloud.database()

const TEMPLATE_ID = 'tgWzJiNGFoA0HQLc4TdjLzf3c9rHCcuSeskHMx2RbFI'//我当前的模板ID

exports.main = async (event, context) => {
  try {
    // 1. 获取当前时间，格式化为 HH:mm
    const now = new Date()
    // 这里的 getHours 和 getMinutes 获取的是服务器时间（通常是北京时间）
    // 如果你的服务器时间不对，可能需要手动 +8 小时
    const currentHour = now.getHours().toString().padStart(2, '0')
    const currentMin = now.getMinutes().toString().padStart(2, '0')
    const timeStr = `${currentHour}:${currentMin}`

    console.log('当前云函数触发时间：', timeStr)

    // 2. 查询数据库
    // 查找条件：开关开启(onclock: true) 并且 提醒时间等于现在
    const _ = db.command
    const res = await db.collection('user') // 这里对应你的本地表名 user
      .where({
        onclock: true,           // 必须开启
        remindTime: timeStr      // 时间必须匹配 (数据库中存储的格式必须是 "HH:mm")
      })
      .get()

    const userList = res.data
    console.log(`找到 ${userList.length} 位需要提醒的用户`, userList)

    // 3. 循环发送消息
    if (userList.length > 0) {
      // 使用 Promise.all 并发发送，加快速度
      await Promise.all(userList.map(async (user) => {
        try {
          // 只有当用户有 openid 时才发送
          if (!user._openid) return

          await cloud.openapi.subscribeMessage.send({
            touser: user._openid,
            page: 'pages/index/index', // 点击消息跳转的页面
            lang: 'zh_CN',
            data: {
              // ⚠️ 根据你提供的截图，key 是 thing4 和 time13
              thing4: {
                value: user.name || '每日打卡' // 读取数据库里的名称，没有则默认
              },
              time13: {
                value: timeStr // 发送当前时间
              }
            },
            templateId: TEMPLATE_ID
          })
          console.log(`发送成功: ${user._openid}`)
        } catch (err) {
          console.error(`发送失败: ${user._openid}`, err)
        }
      }))
    }

    return { success: true, msg: `处理完成，共发送 ${userList.length} 条` }

  } catch (err) {
    console.error(err)
    return err
  }
}