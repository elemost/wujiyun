import {
	defineStore
} from 'pinia';
import {
	loginApi
} from '@/service/login';
import {
	getUserCompanyListApi,
	getCompanyInfoApi,
	switchCompanyApi,
	getUserInfoApi,
	rosterApi
} from '@/service/user';
import {
	cryptoEncode
} from '@/utils/crypto';
import {
	getToken,
	setToken,
	logout as userLogout,
} from '@/utils/auth';

export const useUserStore = defineStore('user', {
	state: () => ({
		userInfo: null, // 用户信息
		showForm: false,
		userCompany: null, // 公司信息
		companyList: [], // 公司列表
		externalCompany: null, // 当前外部公司
		initOpenData: false, // 初始化企业微信sdk
	}),
	actions: {
		setShowForm(value) {
			this.showForm = value;
		},
		// 登录方法
		login(userData) {
			return Promise.resolve(
				loginApi({
					...userData,
					password: cryptoEncode(userData.password) // 密码加密处理...	
				}).then((res) => {
					if (res) {
						setToken(res)
						this.getUserInfo()
					}
					return res
				}))
		},
		// 退出登录
		logout() {
			this.name = '';
			userLogout()
		},
		// 获取用户信息
		getUserInfo() {
			if (!getToken()) return;
			// 调用接口获取用户信息
			getUserInfoApi().then((res) => {
				this.userInfo = res;
				this.externalCompany = res?.corpCompanyList?.[0] || false;
			})
		},
		// 获取用户公司列表
		async getUserCompanyList() {
			if (!getToken()) return;
			const res = await getUserCompanyListApi();
			this.companyList = res || [];
		},
		// 获取公司信息
		async getCompanyInfo() {
			if (!getToken()) return;
			const res = await getCompanyInfoApi();
			this.userCompany = res;
			return res; // 添加这行返回结果
		},
		// 切换公司
		switchCompany(companyId) {
			uni.showLoading({
				title: '正在切换',
				mask: true
			})
			switchCompanyApi({
				companyId
			}).then((res) => {
				setToken(res);
				this.getUserInfo();
				this.getCompanyInfo();
			}).finally(() => {
				uni.hideLoading();
			})
		},
		// 切换外部公司
		switchExternalCompany(company) {
			this.externalCompany = company;
		},

		getCompanyId() {
			return this.userCompany?.companyId;
		},
		clearUserInfo() {
			this.userInfo = null;
			this.userCompany = null;
			this.companyList = [];
		},
		postRoster(params) {
			return Promise.resolve(
				rosterApi(params).then(() => {
					this.getUserInfo()
				}))
		},
		// 初始化企业微信sdk
		setInitOpenData(value) {
			this.initOpenData = value;
		},
	}
})