// 应用全局配置
export const appConfig = {
	baseUrl: "https://cloud.elemost.com/api",
	webViewUrl: 'https://cloud.elemost.com'
}

// tabbar
export const tabbarList = [
	{
		pagePath: "/pages/index/index",
		text: "工作台",
		iconPath: "platform-line",
		selectedIconPath: "platform"
	},
	{
		pagePath: "/pages/message/message",
		text: "消息中心",
		iconPath: "message-line",
		selectedIconPath: "message"
	},
	{
		pagePath: "/pages/market/market",
		text: "应用市场",
		iconPath: "market-line",
		selectedIconPath: "market"
	},
	{
		pagePath: "/pages/contact/contact",
		text: "通讯录",
		iconPath: "contact-line",
		selectedIconPath: "contact"
	},
	{
		pagePath: "/pages/my/my",
		text: "我的",
		iconPath: "my-line",
		selectedIconPath: "my"
	}
];