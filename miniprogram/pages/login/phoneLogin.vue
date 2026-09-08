<template>
    <view class="container" :style="themeColor">
        <uv-picker ref="picker" :confirmColor="themeColor['--primary-color']" :columns="[columns[pickerType]]"
            @confirm="confirm" />
        <!-- 添加 toast 组件 -->
        <uv-toast ref="toast"></uv-toast>
        <uv-code ref="code" :seconds="60" @start="codeStart" @end="codeEnd" @change="codeChange"></uv-code>
        <!-- 当 showForm 为 false 时显示手机号和验证码输入部分 -->
        <view v-if="!userStore.showForm">
            <!-- 手机号输入框 -->
            <view class="input-group">
                <input v-model="phoneNumber" type="number" placeholder="请输入手机号" class="input" />
            </view>
            <!-- 验证码输入框 -->
            <view class="input-group-with-btn">
                <input v-model="verificationCode" type="number" placeholder="请输入验证码" class="input-with-btn" />
                <view class="vertical-line"></view>
                <uv-button type="text" @tap="sendVerificationCode" :disabled="isSend" :text="tips"
                    :customStyle="{ width: '200rpx' }" :customTextStyle="{ color: themeColor['--primary-color'] }" />
            </view>
            <!-- 我已阅读并同意 -->
            <view class="agreement">
                <uv-checkbox-group size="14" v-model="checkboxValue" :activeColor="themeColor['--primary-color']"
                    :customStyle="{ width: '100%' }" shape="circle">
                    <uv-checkbox name="agree">
                        <view class="checkbox-label-wrap">
                            <text> 我已阅读并同意 </text>
                            <text @click.stop="openProtocol(1)"
                                :style="{ color: themeColor['--primary-color'], cursor: 'pointer' }"
                                @mouseenter="hoverText = 1" @mouseleave="hoverText = 0">《使用协议》</text>
                            <text> 和 </text>
                            <text @click.stop="openProtocol(2)"
                                :style="{ color: themeColor['--primary-color'], cursor: 'pointer', }"
                                @mouseenter="hoverText = 2" @mouseleave="hoverText = 0">《隐私协议》</text>
                        </view>
                    </uv-checkbox>
                </uv-checkbox-group>
            </view>
            <!-- 登录/注册按钮 -->
            <button :loading="loading" @click="loginOrRegister" class="login-btn">登录/注册</button>
        </view>

        <!-- 表单页面 -->
        <view v-if="userStore.showForm">
            <!-- 使用新的 uni-forms 写法 -->
            <uni-forms ref="form" :modelValue="formData" :rules="rules" labelPosition="top">
                <!-- 第一页 -->
                <view v-if="currentStep === 1">
                    <view class="information">创建您的组织/企业</view>
                    <uni-forms-item label="企业名称" name="companyName" required>
                        <uni-easyinput v-model="formData.companyName" placeholder="请输入企业名称" />
                    </uni-forms-item>
                    <uni-forms-item label="您的需求" name="purpose" required>
                        <!-- <picker :range="purposeOptions" :value="purpose" @change="bindPurposeChange">
                            <uni-easyinput v-model="formData.purpose" placeholder="请选择您的用途"></uni-easyinput>
                        </picker> -->
                        <view class="picker-box" @click="(pickerOpen('purpose'))">
                            <view class="picker-mask"></view>
                            <uni-easyinput :clearable="false" v-model="formData.purpose"
                                placeholder="请选择您的用途"></uni-easyinput>
                        </view>
                    </uni-forms-item>
                    <uni-forms-item label="所属行业" name="industry" required>
                        <view class="picker-box" @click="(pickerOpen('industry'))">
                            <view class="picker-mask"></view>
                            <uni-easyinput :clearable="false" v-model="formData.industry"
                                placeholder="请选择所属行业"></uni-easyinput>
                        </view>
                    </uni-forms-item>
                    <uni-forms-item label="目前有哪些企业管理需求" name="manageDemand" required>
                        <uv-checkbox-group v-model="formData.manageDemand" :activeColor="themeColor['--primary-color']">
                            <uv-checkbox :customStyle="{ marginBottom: '8px', marginRight: '8px' }"
                                v-for="(option, index) in manageDemandOptions" :key="index" :name="option">
                                {{ option }}
                            </uv-checkbox>
                        </uv-checkbox-group>
                    </uni-forms-item>
                    <button @click="nextStep" :loading="loading" class="login-btn" formtype="button"
                        style="margin-top: 60rpx;">下一步</button>
                </view>
                <!-- 第二页 -->
                <view v-if="currentStep === 2">
                    <view class="information">怎么称呼您</view>
                    <uni-forms-item label="姓名" name="nickName" required>
                        <uni-easyinput v-model="formData.nickName" placeholder="您的姓名" />
                    </uni-forms-item>
                    <uni-forms-item label="您的角色" name="companyRole" required>
                        <view class="picker-box" @click="(pickerOpen('companyRole'))">
                            <view class="picker-mask"></view>
                            <uni-easyinput :clearable="false" v-model="formData.companyRole"
                                placeholder="请选择您的角色"></uni-easyinput>
                        </view>
                    </uni-forms-item>
                    <uni-forms-item label="您在哪里了解到五极云" name="understandWay" required>
                        <uv-checkbox-group v-model="formData.understandWay"
                            :activeColor="themeColor['--primary-color']">
                            <uv-checkbox :customStyle="{ marginBottom: '8px', marginRight: '8px' }"
                                v-for="(option, index) in understandWayList" :key="index" :name="option">
                                {{ option }}
                            </uv-checkbox>
                        </uv-checkbox-group>
                    </uni-forms-item>
                    <uni-forms-item label="您是否接触过零代码/低代码平台产品" name="useLowcode" required>
                        <uv-radio-group @change="radioChange">
                            <uv-radio :customStyle="{ margin: '16rpx' }" :activeColor="themeColor['--primary-color']"
                                v-for="(item, index) in radiolist" :key="index" :label="item.label" :name="item.name">
                            </uv-radio>
                        </uv-radio-group>
                    </uni-forms-item>
                    <button @click="prevStep" class="login-btn" formtype="button"
                        style="margin-top: 60rpx;">上一步</button>
                    <button :loading="loading" type="submit" @click="submitForm" class="login-btn">完成</button>
                </view>
            </uni-forms>
        </view>
    </view>
</template>

<script setup>
import { ref, computed } from 'vue';
import { onLoad, onUnload } from '@dcloudio/uni-app';
import { useGlobalTheme } from '@/utils/theme';
import { getCodeApi, loginApi, registerUser, checkCompanyName, switchCompany } from '@/service/login';
import { useUserStore } from '@/store/user';
import { setToken } from '@/utils/auth';
import { openProtocol } from '@/utils/common';
import { cryptoEncode } from '@/utils/crypto';
import {
    industryOptions,
    manageDemandOptions,
    purposeOptions,
    companyRoleList,
    understandWayList
} from '@/config/loginOptions';
import { tabbarPages } from '@/utils/constant';

const userStore = useUserStore();
const { themeColor } = useGlobalTheme();
const toast = ref(null);
const hoverText = ref(0);
const code = ref(null);
const picker = ref(null);
const pickerType = ref(null);
// 定义 checkboxValue 并初始化
const checkboxValue = ref([]);
const isAgreed = computed(() => checkboxValue.value.includes('agree'));
const redirectUrl = ref('');
// 开启验证码登录
const isSend = ref(false);
// 验证码
const tips = ref('获取验证码');
const loading = ref(false);

const phoneNumber = ref('');
const verificationCode = ref('');
// 移除原有的 showForm 引用
// const showForm = ref(false); 
const currentStep = ref(1);
// 获取表单实例
const form = ref(null);
// 定义 formData 和 rules
const formData = ref({
    companyName: '',
    industry: '',
    nickName: '',
    purpose: '',
    manageDemand: [],
    companyRole: '',
    understandWay: [],
    useLowcode: ''
});
const radiolist = ref([
    { label: '接触过', name: 'yes' },
    { label: '没接触过', name: 'no' }
])
// 验证码组件开始事件
const codeStart = () => {
    isSend.value = true;
}

// 验证码组件结束事件
const codeEnd = () => {
    isSend.value = false;
}

// 验证码组件状态改变事件
const codeChange = (text) => {
    tips.value = text;
}

// 调整 rules 校验规则
const rules = ref({
    companyName: [
        { required: true, errorMessage: '请输入注册企业名称' }
    ],
    purpose: [
        { required: true, errorMessage: '请选择您的用途' }
    ],
    industry: [
        { required: true, errorMessage: '请选择所属行业' }
    ],
    manageDemand: [
        { required: true, errorMessage: '请选择目前有哪些企业管理需求' }
    ],
    nickName: [
        { required: true, errorMessage: '请输入姓名' }
    ],
    companyRole: [
        { required: true, errorMessage: '请选择您的角色' }
    ],
    understandWay: [
        { required: true, errorMessage: '请选择您在哪里了解到五极云' }
    ],
    useLowcode: [
        { required: true, errorMessage: '请选择您是否接触过零代码/低代码平台产品' }
    ]

});

const returnBeforeLoginPage = () => {
    if (redirectUrl.value && tabbarPages.includes(redirectUrl.value)) {
        uni.switchTab({
            url: redirectUrl.value
        });
    } else if (redirectUrl.value && !tabbarPages.includes(redirectUrl.value)) {
        uni.reLaunch({
            url: redirectUrl.value
        });
    } else {
        uni.switchTab({
            url: '/pages/index/index'
        });
    }
}

// 修改 nextStep 方法，确保正确处理异步验证
const nextStep = async () => {
    if (loading.value) {
        return;
    }
    try {
        // 定义第一页需要校验的字段
        const fieldsToValidate = ['companyName', 'purpose', 'industry', 'manageDemand'];
        let isValid = true;
        let errorMessage = '';

        for (const field of fieldsToValidate) {
            const valid = await form.value.validateField(field);
            if (!valid || !valid[field]) {
                isValid = false;
                errorMessage = rules.value[field][0].errorMessage;
                break;
            }
        }
        const companyName = formData.value.companyName;
        if (!companyName) {
            toast.value.show({
                position: 'top',
                icon: false,
                type: 'error',
                message: '请输入企业名称',
            });
            return;
        }

        // 调用 checkCompanyName 接口
        loading.value = true;
        const response = await checkCompanyName({ companyName: companyName });
        loading.value = false;
        if (response.code !== '0000') {
            toast.value.show({
                position: 'top',
                icon: false,
                type: 'error',
                message: '该企业名称已被注册，请更换',
            });
            return;
        }

        if (isValid) {
            currentStep.value = 2;
        } else {
            toast.value.show({
                position: 'top',
                icon: false,
                type: 'error',
                message: errorMessage
            });
        }
    } catch (error) {
		loading.value = false;
        toast.value.show({
            position: 'top',
            icon: false,
            type: 'error',
            message: error.message || '表单验证失败，请检查输入'
        });
    }
};

// 修改 submitForm 方法
const submitForm = async () => {
    if (loading.value) {
        return;
    }
    try {
        // 定义第二页需要校验的字段
        const fieldsToValidate = ['nickName', 'companyRole', 'understandWay', 'useLowcode'];
        let isValid = true;
        let errorMessage = '';

        for (const field of fieldsToValidate) {
            const valid = await form.value.validateField(field);
            if (!valid || !valid[field]) {
                isValid = false;
                errorMessage = rules.value[field][0].errorMessage;
                break;
            }
        }

        if (isValid) {
            // 这里可以添加提交表单的逻辑
            const manageDemandArray = Array.isArray(formData.value.manageDemand) ? formData.value.manageDemand : [formData.value.manageDemand];
            const understandWayArray = Array.isArray(formData.value.understandWay) ? formData.value.understandWay : [formData.value.understandWay];
            // 处理 useLowcode
            const useLowcodeText = formData.value.useLowcode === 'yes' ? '接触过' : '没接触过';
            const params = {
                ...formData.value,
                manageDemand: JSON.stringify(manageDemandArray),
                understandWay: JSON.stringify(understandWayArray),
                useLowcode: useLowcodeText,
                mobile: phoneNumber.value
            }
            loading.value = true;
            registerUser(params).then(res => {
                if (res) {
                    switchCompany({ companyId: res }).then(res => {
                        if (res) {
                            setToken(res);
                            uni.$emit('loginSuccess');
                            returnBeforeLoginPage()
                        }
                    }).catch(err => {
                        toast.value.show({
                            position: 'top',
                            icon: false,
                            type: 'error',
                            message: err.message
                        });
                    }).finally(() => {
                        loading.value = false;
                    })
                }
            }).catch(err => {
                loading.value = false;
                toast.value.show({
                    position: 'top',
                    icon: false,
                    type: 'error',
                    message: err.message
                });
            })
        } else {
            toast.value.show({
                position: 'top',
                icon: false,
                type: 'error',
                message: errorMessage
            });
        }
    } catch (error) {
        toast.value.show({
            position: 'top',
            icon: false,
            type: 'error',
            message: error.message || '表单验证失败，请检查输入'
        });
    }
};

const columns = {
    industry: industryOptions,
    purpose: purposeOptions,
    companyRole: companyRoleList
}

const pickerOpen = (name) => {
    pickerType.value = name;
    let activeIndex = 0;
    if (name === 'industry') {
        activeIndex = columns.industry.findIndex(item => item === formData.value.industry);
    } else if (name === 'purpose') {
        activeIndex = columns.purpose.findIndex(item => item === formData.value.purpose);
    } else if (name === 'companyRole') {
        activeIndex = columns.companyRole.findIndex(item => item === formData.value.companyRole);
    }
    picker.value.setIndexs([activeIndex < 0 ? 0 : activeIndex], true);
    picker.value.open();
}

const confirm = (e) => {
    if (pickerType.value === 'industry') {
        formData.value.industry = e?.value?.[0];
    } else if (pickerType.value === 'purpose') {
        formData.value.purpose = e?.value?.[0];
    } else if (pickerType.value === 'companyRole') {
        formData.value.companyRole = e?.value?.[0];
    }
}

// 单选框变化事件
const radioChange = (value) => {
    formData.value.useLowcode = value;
};
// 手机号校验函数
const validatePhoneNumber = (phone) => {
    const reg = /^1[3-9]\d{9}$/;
    return reg.test(phone);
};

// 发送验证码
const sendVerificationCode = () => {
    if (isSend.value) {
        return;
    }
    if (!isAgreed.value) {
        uni.$uv.toast('请先阅读并同意相关协议');
        return;
    }
    if (!phoneNumber.value) {
        toast.value.show({
            position: 'top',
            icon: false,
            type: 'error',
            message: '请输入手机号',
        });
        return;
    }
    if (!validatePhoneNumber(phoneNumber.value)) {
        toast.value.show({
            position: 'top',
            icon: false,
            type: 'error',
            message: '请输入有效的手机号',
        });
        return;
    }
    getCodeApi(phoneNumber.value).then(res => {
        if (res.code === "0000") {
            code.value.start();
            toast.value.show({
                position: 'top',
                type: 'success',
                message: '验证码发送成功',
            })
            return
        } else {
            toast.value.show({
                position: 'top',
                type: 'error',
                icon: false,
                message: res.message || '验证码发送失败',
            })
        }
    })
};

// 登录/注册
const loginOrRegister = () => {
    if (loading.value) {
        return;
    }
    if (!isAgreed.value) {
        uni.$uv.toast('请先阅读并同意相关协议');
        return;
    }
    if (!phoneNumber.value) {
        toast.value.show({
            position: 'top',
            icon: false,
            type: 'error',
            message: '请输入手机号',
        });
        return;
    }
    if (!validatePhoneNumber(phoneNumber.value)) {
        toast.value.show({
            position: 'top',
            icon: false,
            type: 'error',
            message: '请输入有效的手机号',
        });
        return;
    }
    if (!verificationCode.value) {
        toast.value.show({
            position: 'top',
            icon: false,
            type: 'error',
            message: '请输入验证码',
        });
        return;
    }
    loading.value = true;

    // loginApi({ userName: phoneNumber.value, password: cryptoEncode(verificationCode.value), loginType: "password" }).then(res => {
    //     setToken(res.data)
    //     if (res.otherData.newUser) {
    //         userStore.setShowForm(true);
    //     } else {
    //         uni.$emit('loginSuccess');
    //         returnBeforeLoginPage()
    //     }
    // }).finally(() => {
    //     loading.value = false;
    // })

    loginApi({ mobile: phoneNumber.value, code: verificationCode.value, loginType: "message" }).then(res => {
        setToken(res.data)
        if (res.otherData.newUser) {
            userStore.setShowForm(true);
        } else {
            uni.$emit('loginSuccess');
            returnBeforeLoginPage()
        }
    }).finally(() => {
        loading.value = false;
    })
};

// 上一步
const prevStep = () => {
    currentStep.value = 1;
};

onLoad((option) => {
    const url = option?.redirectUrl ? decodeURIComponent(option?.redirectUrl) : ''
    redirectUrl.value = url
})

onUnload(() => {
    userStore.setShowForm(false)
})

</script>

<style scoped lang="scss">
.container {
    padding: 60rpx 66rpx 60rpx;
}

.picker-box {
    position: relative;
    width: 100%;
    height: 100%;

    .picker-mask {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: transparent;
        z-index: 10;
    }
}

.input-group {
    // 修改为垂直排列
    flex-direction: column;
    // 让元素左对齐
    align-items: flex-start;
    margin-bottom: 40rpx;
}

.input-label {
    // 添加底部边距，让 label 和输入框有间距
    margin-bottom: 20rpx;
    font-size: 28rpx;
    display: flex;
    align-items: center;
}

.required-star {
    width: 16rpx;
    height: 16rpx;
    margin-left: 4rpx;
}

.input {
    flex: 1;
    height: 90rpx;
    border: 1rpx solid #ccc;
    border-radius: 8rpx;
    padding: 0 20rpx;
    font-size: 28rpx;
}

.input-group-with-btn {
    display: flex;
    align-items: center;
    border: 1rpx solid #ccc;
    border-radius: 8rpx;
    margin-bottom: 40rpx;
}

.input-with-btn {
    flex: 1;
    height: 90rpx;
    padding: 0 20rpx;
    font-size: 28rpx;
    border: none;
    outline: none;
}

.vertical-line {
    width: 1rpx;
    height: 40rpx;
    background-color: #ccc;
}

.login-btn {
    width: 100%;
    height: 80rpx;
    background-color: var(--primary-color);
    color: white;
    border-radius: 8rpx;
    font-size: 32rpx;
    margin-bottom: 20rpx;
}

.agreement {
    display: flex;
    align-items: center;
    margin-top: 120rpx;
    margin-bottom: 40rpx;
    font-size: 12px;
    text-align: center;

    .checkbox-label-wrap {
        font-size: 12px; // 字体大小;
        line-height: 14px;
    }
}

.information {
    font-size: 34rpx;
    margin-bottom: 48rpx;
    font-weight: 600;
}

/* 提高选择器优先级，确保样式生效 */
</style>
