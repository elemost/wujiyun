<template>
    <view class="personal-center-container" :style="themeColor">
        <!-- 基本信息模块 -->
        <view class="info-section">
            <view class="section-title">基本信息</view>
            <view class="info-item">
                <text class="info-label">头像</text>
                <view class="right-content" @click="chooseImage()">
                    <image v-if="userInfo.avatar" :src="userInfo.avatar" class="avatar" mode="aspectFill" />
                    <image class="avatar" v-else src="/static/img/default-head.jpg" mode="aspectFill" />
                    <uni-icons type="right" class="card-icon-right"></uni-icons>
                </view>
            </view>
            <view class="divider"></view>
            <view v-for="(field, index) in basicInfoFields" :key="index" @click="showEditModal(field)">
                <view class="info-item">
                    <text class="info-label">{{ field.label }}</text>
                    <view class="right-content">
                        <text v-if="field.key === 'nickName'">
                            <OpenData type="userName" :openid="userInfo?.nickName || ''" />
                        </text>
                        <text v-else class="info-value">
                            {{
                                field.key === 'sex' && field.label === '性别'
                                    ? getGender()
                                    : field.key === 'birthday'
                                        ? getBirthday()
                                        : userInfo[field.key] || '-'
                            }}
                        </text>
                        <uni-icons type="right" class="card-icon-right"></uni-icons>
                    </view>
                </view>
                <view v-if="index < basicInfoFields.length - 1" class="divider"></view>
            </view>
        </view>

        <!-- 工作信息模块 -->
        <view class="info-section" style="margin-top: 12px;">
            <view class="section-title">工作信息</view>
            <view v-for="(field, index) in workInfoFields" :key="index">
                <view class="info-item">
                    <text class="info-label">{{ field.label }}</text>
                    <view class="right-content">
                        <text class="info-value">{{ userInfo[field.key] || '-' }}</text>
                    </view>
                </view>
                <view v-if="index < workInfoFields.length - 1" class="divider"></view>
            </view>
        </view>

        <!-- 其他信息模块 -->
        <view class="info-section" style="margin-top: 12px;">
            <view class="section-title">其他信息</view>
            <view v-for="(field, index) in otherInfoFields" :key="index" @click="showEditModal(field)">
                <view class="info-item">
                    <text class=" info-label">{{ field.label }}</text>
                    <view class="right-content">
                        <text class="info-value">{{ getOtherInfo(field.key) }}</text>
                        <uni-icons type="right" class="card-icon-right"></uni-icons>
                    </view>
                </view>
                <view v-if="index < otherInfoFields.length - 1" class="divider"></view>
            </view>
        </view>

        <!-- uv-modal 弹窗 -->
        <uv-modal ref="modal" title="上传头像">
            <view style="align-items: center;">
                <uv-upload ref="uploader" :action="uploadUrl" :auto-upload="false" :file-list="fileList" :max-count="1"
                    :size-type="['compressed']" :source-type="['album', 'camera']" @delete="handleDelete"
                    @afterRead="startUpload">
                </uv-upload>
            </view>
        </uv-modal>
    </view>

    <!-- 新增编辑弹窗 -->
    <uv-modal ref="editModal" :title="editField?.label || '编辑信息'" showCancelButton @confirm="confirmEdit">
        <template v-if="editField">
            <view v-if="editField.key === 'sex'">
                <uv-radio-group v-model="editValue">
                    <uv-radio :customStyle="{ margin: '8px' }" name="1">女</uv-radio>
                    <uv-radio name="0">男</uv-radio>
                </uv-radio-group>
            </view>
            <view v-else-if="editField.key === 'birthday'">
                <!-- -->
            </view>
            <view v-else>
                <uv-input :customStyle="{'border-width':'1px !important'}" v-model="editValue" :placeholder="'请输入' + editField.label"></uv-input>
            </view>
        </template>
    </uv-modal>
    <uv-datetime-picker ref="datetimePicker" v-model="editValue" mode="date"
        @confirm="handleDateConfirm"></uv-datetime-picker>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useUserStore } from '@/store/user';
import { useGlobalTheme } from '@/utils/theme';
import { getToken } from '@/utils/auth';
import { appConfig } from '@/config/app';
import OpenData from '@/components/open-data/index.vue'
const { themeColor } = useGlobalTheme();
const userStore = useUserStore();
const userInfo = computed(() => userStore.userInfo || {});
const modal = ref(null);
const datetimePicker = ref(null);
const editModal = ref(null);
const editField = ref(null);
const editValue = ref('');
const uploader = ref(null);
const uploading = ref(false);
const fileList = ref([]);

const showEditModal = (field) => {
    editField.value = field;
    if (field.key === 'sex' || field.key === 'birthday' || field.key === 'idCard' || field.key === 'ed' || field.key === 'graduateSchool' || field.key === 'profession') {
        const item = userInfo.value.userInfoList.find(item => item.infoKey === field.key);
        editValue.value = item ? item.infoValue : '';
    } else {
        editValue.value = userInfo.value[field.key] || '';
    }
    if (field.key === 'birthday') {
        datetimePicker.value.open();
    } else {
        editModal.value.open();

    }
};

// 删除文件
const handleDelete = () => {
    fileList.value = [];
};

// 开始上传
const chooseImage = () => {
    editField.value = { key: 'avatar' };
    uni.chooseImage({
        count: 1,
        mediaType: ['image'],
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
            if (res.tempFiles.length > 0) {
                const tempFile = res.tempFiles[0];
                startUpload({ file: tempFile });
            }
        },
        fail: (err) => {
            if (err.errMsg !== "chooseImage:fail cancel"){
                uni.showToast({ title: '选择图片失败', icon: 'none' });
            }
        }
    })
};
const startUpload = async (event) => {
    const file = event.file;
    if (file.size > 5 * 1024 * 1024) {
        uni.showToast({ title: '图片大小不能超过5M', icon: 'none' });
        return;
    }
    const tempFilePath = file.path;
    // 假设从 userStore 中获取 token，根据实际情况修改
    const token = getToken()
    // 使用 uni.uploadFile 上传文件
    uni.uploadFile({
        url: appConfig.baseUrl + '/common/uploadFile', // 替换为实际的上传接口地址
        filePath: tempFilePath,
        name: 'file',
        formData: {
            fileType: 'FORM'
        },
        header: {
            'Authorization': token
        },
        success: (uploadRes) => {
            const data = JSON.parse(uploadRes.data);
            editValue.value = data.data.fileUrl;
            confirmEdit();
        },
        fail: (err) => {
            uni.showToast({ title: '上传失败', icon: 'none' });
        }
    });
};
// 获取性别
const getGender = () => {
    const sexItem = userInfo.value?.userInfoList?.find(item => item.infoKey === 'sex');
    if (sexItem) {
        return sexItem.infoValue === '1' ? '女' : '男';
    }
    return '-';
};

// 获取出生年月
const getBirthday = () => {
    const birthdayItem = userInfo.value?.userInfoList?.find(item => item.infoKey === 'birthday');
    if (birthdayItem) {
        return new Date(birthdayItem.infoValue).toLocaleDateString();
    }
    return '-';
};
const formatIdCard = (idCard) => {
    if (!idCard) return '-';
    return idCard.slice(0, 4) + '*'.repeat(idCard.length - 8) + idCard.slice(-4);
}
// 封装获取其他信息的方法
const getOtherInfo = (key) => {
    const item = userInfo.value?.userInfoList?.find(item => item.infoKey === key);
    if (item) {
        if (key === 'idCard') {
            return formatIdCard(item.infoValue);
        }
        return item.infoValue;
    }
    return '-';
};

const basicInfoFields = [
    { label: '姓名', key: 'nickName' },
    { label: '邮箱', key: 'email' },
    { label: '性别', key: 'sex' },
    { label: '出生年月', key: 'birthday' },
    { label: '电话号码', key: 'phonenumber' }
];

const workInfoFields = [
    { label: '部门名称', key: 'department' },
    { label: '职级', key: 'rank' },
    { label: '入职时间', key: 'joinDate' },
    { label: '职称', key: 'title' }
];

const otherInfoFields = [
    { label: '身份证号', key: 'idCard' },
    { label: '学历', key: 'ed' },
    { label: '毕业院校', key: 'graduateSchool' },
    { label: '专业', key: 'profession' }
];
const handleDateConfirm = (value) => {
    const date = new Date(value.value);
    const isoString = date.toISOString();
    editValue.value = isoString;
    confirmEdit();
};
const confirmEdit = () => {
    if (!editField.value) return;
    // 身份证号校验规则
    const idCardRegex = /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/;
    // 手机号校验规则
    const phoneRegex = /^1[3-9]\d{9}$/;
    // 邮箱校验规则
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (editField.value.key === 'idCard' && !idCardRegex.test(editValue.value)) {
        uni.showToast({ title: '请输入有效的身份证号', icon: 'none' });
        return;
    }

    if (editField.value.key === 'phonenumber' && !phoneRegex.test(editValue.value)) {
        uni.showToast({ title: '请输入有效的手机号', icon: 'none' });
        return;
    }
    if (editField.value.key === 'email' && !emailRegex.test(editValue.value)) {
        uni.showToast({ title: '请输入有效的邮箱地址', icon: 'none' });
        return;
    }
    // 提取公共属性
    const baseUserInfo = {
        userId: userInfo.value.userId
    };
    // 需要放入userInfoList的字段
    const specialFields = ['sex', 'birthday', 'idCard', 'ed', 'graduateSchool', 'profession'];
    let newUserInfo;
    if (specialFields.includes(editField.value.key)) {
        // 特殊字段处理
        newUserInfo = {
            ...baseUserInfo,
            userInfoList: [{
                infoKey: editField.value.key,
                infoValue: editValue.value
            }]
        };
    } else {
        newUserInfo = {
            ...baseUserInfo,
            [editField.value.key]: editValue.value
        };
    }
    userStore.postRoster(newUserInfo)
        .then(() => {
            // 仅在API调用成功时关闭弹窗
            editModal.value.close();
        })
        .catch(error => {
            console.error('保存失败:', error);
            uni.showToast({
                title: '保存失败，请重试',
                icon: 'none'
            });
        })
}; 
</script>

<style lang="scss" scoped>
.personal-center-container {
    padding: 20rpx;
}

.info-section {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    padding: 16px;
}

.section-title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 12px;
}

.info-item {
    display: flex;
    align-items: center;
    padding: 8px 0;
    height: 40px;
    /* 统一小模块高度 */
}

.info-label {
    width: 120px;
    color: #666;
}

.right-content {
    display: flex;
    align-items: center;
    flex: 1;
    justify-content: flex-end;
    gap: 20rpx;
}

.info-value {
    color: #333;
}

.avatar {
    width: 80rpx;
    height: 80rpx;
    border-radius: 50%;
	border-radius: 50%;
	border: 1px solid #EEEFEF;
	box-sizing: border-box;
	padding: 10rpx;
}

.divider {
    height: 1px;
    background-color: #eee;
}
</style>