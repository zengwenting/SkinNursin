from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import mm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.cidfonts import UnicodeCIDFont
from reportlab.platypus import Paragraph, SimpleDocTemplate, Spacer, Table, TableStyle, PageBreak

output_path = r"E:\毕业设计\code\tacomall\output\pdf\毕业设计项目说明.pdf"

pdfmetrics.registerFont(UnicodeCIDFont("STSong-Light"))

styles = getSampleStyleSheet()
styles.add(ParagraphStyle(
    name="TitleCN",
    parent=styles["Title"],
    fontName="STSong-Light",
    fontSize=20,
    leading=26,
    alignment=1,
    spaceAfter=10,
))
styles.add(ParagraphStyle(
    name="SubTitleCN",
    parent=styles["Heading2"],
    fontName="STSong-Light",
    fontSize=14,
    leading=20,
    spaceBefore=10,
    spaceAfter=6,
))
styles.add(ParagraphStyle(
    name="BodyCN",
    parent=styles["BodyText"],
    fontName="STSong-Light",
    fontSize=11,
    leading=18,
    spaceAfter=4,
))
styles.add(ParagraphStyle(
    name="SmallCN",
    parent=styles["BodyText"],
    fontName="STSong-Light",
    fontSize=9,
    leading=14,
    textColor=colors.HexColor("#555555"),
))

doc = SimpleDocTemplate(
    output_path,
    pagesize=A4,
    leftMargin=18 * mm,
    rightMargin=18 * mm,
    topMargin=16 * mm,
    bottomMargin=14 * mm,
)

story = []

story.append(Paragraph("毕业设计项目说明文档", styles["TitleCN"]))
story.append(Paragraph("项目范围：仅基于当前仓库中与你毕业设计相关的代码实现（ma 小程序与 server/api/ma 后端）进行描述。", styles["SmallCN"]))
story.append(Paragraph("生成时间：2026-03-22", styles["SmallCN"]))
story.append(Spacer(1, 8))

story.append(Paragraph("一、功能模块", styles["SubTitleCN"]))
module_lines = [
    "1. 用户与个人资料模块：支持用户资料读取与更新（昵称、头像、账户、性别、肤质目标等），并在小程序本地维护登录态、个人皮肤档案、提醒设置。",
    "2. 护肤品管理模块：支持按分类（护肤/彩妆）查询产品、新增产品、软删除产品，包含生产日期、过期日期、成分、适用时段等字段。",
    "3. 每日打卡模块：支持选择当日使用产品并提交打卡，记录皮肤状态与分数；支持按月历查看已打卡日期、连续天数、累计天数及当日明细。",
    "4. 肤质分析模块：前端通过拍照入口触发分析流程，后端保存肤质测试记录并返回分值、建议；前端展示趋势与历史报告。",
    "5. AI 助手模块：提供对话问答、成分解读、护理流程推荐三个接口，当前实现为可联调的 Mock 结果，并预留后续接入真实模型的位置。",
    "6. 页面导航与设置模块：主页快捷入口、个人中心、账号详情、闹钟提醒配置、公众号页等辅助能力，形成闭环使用路径。",
]
for line in module_lines:
    story.append(Paragraph(line, styles["BodyCN"]))

story.append(Spacer(1, 4))
story.append(Paragraph("模块对应代码范围", styles["BodyCN"]))
story.append(Paragraph("- 前端：ma/src/pages/*、ma/src/store/app.js、ma/src/utils/api.js、ma/src/config/index.js", styles["BodyCN"]))
story.append(Paragraph("- 后端：server/api/ma/src/main/java/com/tacomall/apima/{controller,service,entity,dto,vo,mapper}", styles["BodyCN"]))
story.append(Paragraph("- 数据脚本：server/api/ma/src/main/resources/schema.sql、data.sql", styles["BodyCN"]))

story.append(PageBreak())
story.append(Paragraph("二、系统架构", styles["SubTitleCN"]))
arch_lines = [
    "1. 总体形态：采用前后端分离架构。小程序端（uni-app + Vue3 + Pinia）通过 HTTP 调用 Spring Boot 后端（api-ma）。",
    "2. 后端分层：Controller 接口层 -> Service 业务层 -> Mapper 持久层（MyBatis-Plus BaseMapper） -> MySQL 数据库。",
    "3. 数据与缓存：application.yml 中配置了 MySQL 与 Redis；当前毕业设计核心功能主要依赖 MySQL 持久化，Redis 为框架能力预留。",
    "4. 多模块工程：server 为 Gradle 多模块项目，当前毕业设计核心后端位于 :api-ma，公共能力由 :common 提供。",
    "5. 数据一致性：打卡创建使用事务（@Transactional），并通过 (user_id, checkin_date) 唯一约束 + 业务判断保证“一天一次打卡”。",
    "6. 统一返回：接口统一使用 ApiResponse<T>，成功 code=0，失败 code=1，便于前端统一处理。",
]
for line in arch_lines:
    story.append(Paragraph(line, styles["BodyCN"]))

story.append(Spacer(1, 8))
story.append(Paragraph("毕业设计相关数据模型（核心表）", styles["BodyCN"]))
model_table = Table([
    ["数据表", "核心字段", "用途"],
    ["user", "nickname, avatar, skin_type, skin_goal", "用户基础信息与肤质目标"],
    ["cosmetic_product", "category, production_date, expire_date", "个人护肤/彩妆产品管理"],
    ["checkin", "checkin_date, skin_status, hydration_score", "每日打卡主记录"],
    ["checkin_item", "checkin_id, product_id, step_order", "打卡关联产品明细"],
    ["skin_test", "test_date, skin_type, advice", "肤质分析历史记录"],
], colWidths=[32*mm, 62*mm, 62*mm])
model_table.setStyle(TableStyle([
    ("FONTNAME", (0, 0), (-1, -1), "STSong-Light"),
    ("FONTSIZE", (0, 0), (-1, -1), 10),
    ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#EEF3FF")),
    ("GRID", (0, 0), (-1, -1), 0.5, colors.HexColor("#BFCBEA")),
    ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
    ("LEFTPADDING", (0, 0), (-1, -1), 5),
    ("RIGHTPADDING", (0, 0), (-1, -1), 5),
]))
story.append(model_table)

story.append(PageBreak())
story.append(Paragraph("三、接口实现", styles["SubTitleCN"]))
story.append(Paragraph("接口前缀：前端 API_PREFIX = http://localhost:4002/api", styles["BodyCN"]))
story.append(Paragraph("后端控制器目录：server/api/ma/.../controller/Api*Controller.java", styles["BodyCN"]))

api_table = Table([
    ["模块", "方法", "路径", "实现说明"],
    ["用户", "GET", "/api/user/info", "按 userId 查询用户资料，空值默认 userId=1"],
    ["用户", "POST", "/api/user/update", "更新用户档案字段并返回最新数据"],
    ["产品", "GET", "/api/product/list", "按 userId 与 category 查询，按创建时间倒序"],
    ["产品", "POST", "/api/product/add", "新增产品并写入生产/过期/成分等信息"],
    ["产品", "POST", "/api/product/delete", "软删除：is_delete 置为 1"],
    ["打卡", "POST", "/api/checkin/create", "创建当日打卡与打卡明细，重复提交会拒绝"],
    ["打卡", "GET", "/api/checkin/calendar", "返回已打卡日期集合"],
    ["打卡", "GET", "/api/checkin/detail", "按 id 或 date 获取当日明细"],
    ["打卡", "GET", "/api/checkin/today", "返回今日是否打卡及详情"],
    ["肤质", "POST", "/api/skin/test", "写入一次测试记录并返回报告"],
    ["肤质", "GET", "/api/skin/history", "查询历史测试记录"],
    ["AI", "POST", "/api/ai/chat", "对话接口，当前返回 Mock 建议"],
    ["AI", "GET", "/api/ai/ingredient", "成分解析接口，当前为 Mock"],
    ["AI", "GET", "/api/ai/recommend", "护肤流程推荐接口，当前为 Mock"],
], colWidths=[20*mm, 14*mm, 44*mm, 78*mm])
api_table.setStyle(TableStyle([
    ("FONTNAME", (0, 0), (-1, -1), "STSong-Light"),
    ("FONTSIZE", (0, 0), (-1, -1), 9.5),
    ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#F3F7FF")),
    ("GRID", (0, 0), (-1, -1), 0.5, colors.HexColor("#BFCCE7")),
    ("VALIGN", (0, 0), (-1, -1), "TOP"),
    ("LEFTPADDING", (0, 0), (-1, -1), 4),
    ("RIGHTPADDING", (0, 0), (-1, -1), 4),
]))
story.append(api_table)

story.append(Spacer(1, 8))
story.append(Paragraph("典型接口链路示例：打卡流程", styles["BodyCN"]))
story.append(Paragraph("前端 pages/checkin/index.vue 选择产品 -> 调用 api.createCheckin -> 后端 ApiCheckinController.create -> CheckinServiceImpl 事务写入 checkin/checkin_item -> 前端刷新今日状态并跳转详情页。", styles["BodyCN"]))

story.append(PageBreak())
story.append(Paragraph("四、页面设计", styles["SubTitleCN"]))
page_lines = [
    "1. 首页（pages/index/index）：提供打卡主按钮和两项快捷入口（肤质分析、皮肤数据），并根据今日打卡状态动态切换“打卡/查看详情”。",
    "2. 梳妆台（pages/gallery/index）：按“护肤品/彩妆品”分组展示，支持展开收起、管理模式、多选删除、新增跳转。",
    "3. 智慧助手（pages/cart/index）：聊天对话区 + 快捷问题按钮（成分查询/智能推荐）+ 文本输入发送，承接 AI 接口联调。",
    "4. 打卡页（pages/checkin/index）：以卡片网格选择当日使用产品，提交后写入打卡数据并跳转明细页。",
    "5. 打卡详情（pages/checkin-detail/index）：月历视图 + 连续/累计统计 + 指定日期护肤记录明细。",
    "6. 肤质分析（pages/skin-test/index）：调用摄像头拍照，展示测试分数与建议。",
    "7. 皮肤数据（pages/skin-data/index）：展示近几次补水评分趋势与历史报告列表。",
    "8. 我的（pages/me/index）：微信授权登录、个人信息入口、闹钟设置入口、公众号入口、退出登录。",
    "9. 我的信息（pages/user-info/index）：支持编辑肤质、年龄、敏感肌状态、敏感源、护理目标，保存到本地状态。",
    "10. 设置（pages/settings/index）：提醒开关与时间设置，存储于 Pinia + 本地缓存。",
    "11. 账号详情/公众号页（pages/account, pages/official）：补充账号展示与服务触达说明。",
]
for line in page_lines:
    story.append(Paragraph(line, styles["BodyCN"]))

story.append(Spacer(1, 8))
story.append(Paragraph("视觉风格总结", styles["BodyCN"]))
story.append(Paragraph("页面统一采用“healing-shell + glass-card + pill-button”设计体系，使用浅粉蓝渐变背景、圆角卡片与柔和阴影，形成轻疗愈风格；移动端布局以单列卡片和底部操作按钮为主，交互路径清晰。", styles["BodyCN"]))

story.append(Spacer(1, 10))
story.append(Paragraph("附注：本说明已排除与毕业设计无关的商城历史业务模块（如审批、物流、支付等）描述，仅聚焦当前护肤助手相关实现。", styles["SmallCN"]))


def add_page_number(canvas, doc_obj):
    canvas.setFont("STSong-Light", 9)
    canvas.setFillColor(colors.HexColor("#666666"))
    canvas.drawRightString(A4[0] - 18 * mm, 10 * mm, f"第 {doc_obj.page} 页")


doc.build(story, onFirstPage=add_page_number, onLaterPages=add_page_number)
print(output_path)
