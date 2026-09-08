<template>
    <div class="contact-container" :style="themeColor">
        <!-- 搜索框 -->
        <view class="inputDiv">
            <uv-input v-model.trim="nickName" :customStyle="{ 'border-width': '1px !important' }" placeholder="输入名称进行搜索"
                prefixIcon="search" placeholderStyle="font-size: 28rpx;color: #acacac;"
                prefixIconStyle="font-size: 20px;color: #7b7b7b;" @input="handleSearch">
                <template v-slot:suffix>
                    <uv-icon v-if="nickName" @click="clearInput" name="close-circle-fill" color="#ACACAC" size="18" />
                </template>
            </uv-input>
        </view>
        <scroll-view scroll-y class="section-wrap">
            <!-- 第一块内容区域：组织结构管理和角色管理 -->
            <view class="management-section">
                <view class="management-item" @click="handleOrgManagement">
                    <view>
                        <uv-icon name="depart" custom-prefix="custom-icon" size="20" color="#555555"></uv-icon>
                        <text style="margin-left: 12px;">组织结构</text>
                    </view>
                    <view class="right-wrap">
                        <text class="selected-text" v-if="deptSelect">{{ deptSelect.name }}</text>
                        <uv-icon name="arrow-right" size="16" color="#7b7b7b"></uv-icon>
                    </view>
                </view>
                <view class="separator"></view>
                <view class="management-item" @click="handleRoleManagement">
                    <view>
                        <uv-icon name="role" custom-prefix="custom-icon" size="20" color="#555555"></uv-icon>
                        <text style="margin-left: 12px;">角色管理</text>
                    </view>
                    <view class="right-wrap">
                        <text class="selected-text" v-if="roleSelected">{{ roleSelected.postName }}</text>
                        <uv-icon name="arrow-right" size="16" color="#7b7b7b"></uv-icon>
                    </view>
                </view>
            </view>
            <!-- 第二块内容区域：全部成员列表 -->
            <GlobalLoading :loading="isLoading">
                <view class="member-list-section">
                    <view class="title">全部成员</view>
                    <view class="list-wrap">
                        <view class="empty-wrap" v-if="memberList.length === 0 && !isLoading">
                            <uv-empty text="没有找到相关成员"></uv-empty>
                        </view>
                        <view v-for="member in memberList" :key="member.userId" class="member-item"
                            @click="changeUser(member.userId)">
                            <image v-if="member.avatar" class="icon-image" mode="aspectFill" :src="member.avatar" />
                            <view v-if="!member.avatar" class="icon-circle">
                                <uv-icon name="geren" custom-prefix="custom-icon" size="36rpx" color="#fff" />
                            </view>
                            <view class="text-box">
                                <text class="name-view">
                                    <OpenData type="userName" :openid="member.nickName || member.userName || ''" />
                                </text>
                                <uv-tags v-if="member.adminUser" :bgColor="activeBgColor"
                                    :color="themeColor['--primary-color']" :borderColor="themeColor['--primary-color']"
                                    text="组织创建人">
                                </uv-tags>
                                <uv-tags v-if="!member.adminUser && member?.roleIdList?.includes(107)"
                                    :bgColor="activeBgColor" :color="themeColor['--primary-color']"
                                    :borderColor="themeColor['--primary-color']" text="管理员">
                                </uv-tags>
                            </view>
                        </view>
                    </view>
                </view>
            </GlobalLoading>
        </scroll-view>
        <uv-popup ref="popup" mode="bottom" :round="10">
            <view class="popupinfo">
                <text class="popupinfotext">基本信息</text>
                <uv-list v-if="userData">
                    <uv-list-item>
                        <view class="popupAvatar">
                            <text>头像</text>
                            <image v-if="userData.avatar" :src="userData.avatar" mode="aspectFill"
                                style="width: 24px; height: 24px;border-radius: 50%;" />
                            <view v-else class="icon-circle">
                                <uv-icon name="geren" custom-prefix="custom-icon" size="24rpx" color="#fff" />
                            </view>
                        </view>
                    </uv-list-item>
                    <uv-list-item title="姓名">
                        <template #footer>
                            <OpenData type="userName" :openid="userData.nickName || userData.userName || ''" />
                        </template>
                    </uv-list-item>
                    <uv-list-item title="邮箱" :rightText="userData.email" />
                    <uv-list-item title="性别" :rightText="userData?.sex === 0 ? '男' : userData?.sex === 1 ? '女' : '未知'">
                    </uv-list-item>
                    <uv-list-item>
                        <view class="popupAvatar">
                            <text>手机号</text>
                            <view class="popupPhone" @click="callPhone(userData.phonenumber)">
                                <text>{{ userData.phonenumber }}</text>
                                <uv-icon name="dianhua" custom-prefix="custom-icon" size="18"
                                    :color="themeColor['--primary-color']" style="margin-left: 10px;" />
                            </view>
                        </view>
                    </uv-list-item>
                    <uv-list-item title="角色"
                        :rightText="userData.adminUser ? '组织创建人' : (userData.roleIdList && userData.roleIdList.includes(107) ? '管理员' : (userData.roleName || '普通成员'))" />
                </uv-list>
            </view>
        </uv-popup>
        <uv-popup ref="popupDepartment" mode="right" :round="10">
            <view class="popup-wrap">
                <view class="popup-title" style="padding: 0 32rpx;">
                    <text>组织结构</text>
                </view>
                <GlobalLoading :loading="departLoading">
                    <scroll-view scroll-y style="height: calc(100% - 72rpx);">
                        <Menu :menu-data="departList" :value="deptSelect?.id" @onSelect="onSelelct"></Menu>
                        <view class="empty-wrap" v-if="departList.length === 0 && !departLoading">
                            <uv-empty text="没有找到相关组织"></uv-empty>
                        </view>
                    </scroll-view>
                </GlobalLoading>
            </view>
        </uv-popup>
        <uv-popup ref="popupRole" mode="right">
            <view class="popup-wrap">
                <view class="popup-title" style="padding: 0 32rpx;">
                    <text>角色管理</text>
                    <view v-if="roleSelected" @click="clearRoleSelect">取消选中</view>
                </view>
                <view class="search-box">
                    <view class="input-box">
                        <uv-input v-model.trim="searchText" @input="handleSearchRole" placeholder="输入名称进行搜索"
                            prefixIcon="search" border="none" placeholderStyle="color: #ACACAC;font-size: 28rpx"
                            prefixIconStyle="font-size: 20px;color: #7B7B7B">
                            <template v-slot:suffix>
                                <uv-icon v-if="searchText" @click="() => {
                                    searchText = '';
                                    handleSearchRole('');
                                }" name="close-circle-fill" color="#ACACAC" size="18" />
                            </template>
                        </uv-input>
                    </view>
                </view>
                <GlobalLoading :loading="roleLoading">
                    <scroll-view scroll-y style="height: calc(100% - 180rpx); padding: 0 32rpx;">
                        <uv-list>
                            <uv-list-item v-for="role in roleList" :key="role.postId">
                                <view @click="changeRole(role)" class="roleItem">
                                    <view class="role-name">
                                        <rich-text :nodes="handleName(role.postName)"></rich-text>
                                    </view>
                                    <uv-icon v-if="roleSelected?.postId === role.postId" name="checkmark-circle-fill"
                                        size="20" :color="themeColor['--primary-color']" />
                                </view>
                            </uv-list-item>
                        </uv-list>
                        <view class="empty-wrap" v-if="roleList.length === 0 && !roleLoading">
                            <uv-empty text="没有找到相关角色"></uv-empty>
                        </view>
                    </scroll-view>
                </GlobalLoading>
            </view>
        </uv-popup>
        <!-- 底部导航栏 -->
        <CustomTabBar />
    </div>
</template>

<script setup>
import { useGlobalTheme } from '@/utils/theme';
import { ref, watch, computed, } from 'vue';
import { userList, userInfo, postList, deptList } from '@/service/user';
import CustomTabBar from '@/components/custom-tab-bar/index.vue';
import Menu from '@/components/menu/index.vue';
import OpenData from '@/components/open-data/index.vue';
import { useUserStore } from '@/store/user';
import { getFormIcon } from '@/utils/common';
import { onPullDownRefresh } from '@dcloudio/uni-app';

const userStore = useUserStore();
const companyId = computed(() => userStore.getCompanyId());
const { themeColor } = useGlobalTheme();
const total = ref(0);
const nickName = ref('')
const memberList = ref([]);
const isLoading = ref(false);
const popup = ref(null);
const popupDepartment = ref(null);
const popupRole = ref(null);

const userData = ref();
const roleLoading = ref(false);
const roleList = ref([]);
const originRoleList = ref([]);
const departLoading = ref(false);
const departList = ref([]);
// 列表搜索
const deptSelect = ref(null);
const roleSelected = ref(null);
// 搜索框内容
const searchText = ref('');


const activeBgColor = uni.$uv.colorToRgba(themeColor.value['--primary-color'], 0.15)

// 获取用户组织列表
const fetchDeptList = async () => {
    departLoading.value = true;
    try {
        const res = await deptList();
        const list = res || [];
        // 递归处理菜单数据
        const processMenu = (menu, level = 0) => {
            const children = menu.children || [];
            return {
                id: menu.deptId,
                name: menu.deptName,
                level: level,
                icon: {
                    name: getFormIcon('DEPT', 'icon'),
                    color: themeColor.value['--primary-color'],
                },
                children: children.map((el) => processMenu(el, level + 1))
            };
        };
        departList.value = list.map((item) => processMenu(item));
        deptSelect.value = departList.value[0];
    } finally {
        departLoading.value = false;
    }
}

const fetchRoleList = async () => {
    roleLoading.value = true;
    postList({ pageNum: 1, pageSize: 999999 }).then(res => {
        roleList.value = res.list || [];
        originRoleList.value = res.list || [];
    }).finally(() => {
        roleLoading.value = false;
    })
}

// 组织管理
const handleOrgManagement = () => {
    popupDepartment.value.open();
};

// 角色管理
const handleRoleManagement = () => {
    popupRole.value.open();
};
const onSelelct = (node) => {
    if (deptSelect?.value?.id !== node.id) {
        deptSelect.value = node;
        popupDepartment.value.close()
        fetchUserList();
    }
};
// 切换角色
const changeRole = (roleItem) => {
    if (roleSelected.value?.postId === roleItem.postId) {
        roleSelected.value = null;
    } else {
        roleSelected.value = roleItem;
    }
    popupRole.value.close();
    fetchUserList();
}
// 清空选中的角色
const clearRoleSelect = () => {
    roleSelected.value = null;
    popupRole.value.close();
    fetchUserList();
}
// 搜索角色
const handleSearchRole = (value) => {
    const val = value.trim();
    if (!val) {
        roleList.value = originRoleList.value;
    }
    roleList.value = originRoleList.value.filter(item => item.postName.includes(val));
}
// 清空搜索框内容
const clearInput = () => {
    nickName.value = '';
    handleSearch();
}

// 搜索
const handleSearch = () => {
    uni.$uv.debounce(() => {
        fetchUserList();
    }, 500)
};
// 处理数据的名称，关键字高亮
const handleName = (name) => {
    const reg = new RegExp(searchText.value, 'g');
    const str = name.replace(reg, `<span style="color: var(--primary-color)">${searchText.value}</span>`);
    return `<div>${str}</div>`
}
// 定义拨打电话的方法
const callPhone = (phoneNumber) => {
    if (phoneNumber) {
        wx.makePhoneCall({
            phoneNumber: phoneNumber,
            success: () => {
                console.log('拨打电话成功');
            },
            fail: (error) => {
                console.error('拨打电话失败', error);
            }
        });
    }
};
const fetchUserList = async () => {
    isLoading.value = true; // 开始加载
    const params = {
        pageNum: 1,
        pageSize: 999999,
        departmentId: deptSelect?.value?.id,
        nickName: nickName.value,
        postId: roleSelected.value?.postId,
    }
    try {
        const response = await userList(params)
        total.value = response.total;
        memberList.value = response?.list || [];
    } finally {
        isLoading.value = false;
        uni.stopPullDownRefresh();
        uni.hideLoading();
    }
}
const changeUser = (userId) => {
    userInfo(userId).then(res => {
        userData.value = res;
        popup.value.open();

    })
}

// 下拉刷新
onPullDownRefresh(() => {
    uni.showLoading({
        title: '刷新中...',
    })
    setTimeout(() => {
        fetchUserList();
        fetchDeptList();
        fetchRoleList();
    }, 500);
})

watch(companyId, (newVal) => {
    if (newVal) {
        fetchUserList();
        fetchDeptList();
        fetchRoleList();
    }
}, { immediate: true })

</script>

<style lang="scss" scoped>
.contact-container {
    position: relative;
    height: 100%;
    background-color: #F6F6F8;
}

.inputDiv {
    width: 100%;
    height: 108rpx;
    background-color: #ffffff;
    padding: 16rpx 34rpx;
    box-sizing: border-box;
}

.section-wrap {
    // #ifdef MP-WEIXIN
    height: calc(100% - 108rpx - 68px);
    // #endif
    // #ifdef H5
    height: calc(100% - 68px);
    // #endif
    padding: 16rpx 34rpx;
    box-sizing: border-box;
}

input {
    width: 100%;
    height: 100%;
    border: 1px solid #ccc;
    border-radius: 8px;
    box-sizing: border-box;
}

.management-section {
    // 修改为垂直布局
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: #ffffff;
    border-radius: 16rpx;
}

.management-item {
    height: 112rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    font-size: 34rpx;
    color: #181818;
    padding: 0 40rpx;
    box-sizing: border-box;

    .right-wrap {
        max-width: 360rpx;

        .selected-text {
            flex: 1;
            font-size: 30rpx;
            margin-right: 12rpx;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            color: #7B7B7B;
        }
    }
}

.management-item>view {
    display: flex;
    align-items: center;
}

.card-icon-left {
    margin-right: 10px;
}

.card-icon-right {
    color: #909399;
}

.separator {
    height: 1rpx;
    width: calc(100% - 32px - 40rpx);
    background-color: #EEEFEF;
    margin-left: calc(32px + 40rpx);
}

.member-list-section {
    position: relative;
    padding: 16rpx 32rpx;
    background-color: #ffffff;
    border-radius: 16rpx;
    margin-top: 26rpx;
    min-height: calc(100vh - 376rpx - 68px - 26rpx);
    box-sizing: border-box; // 确保 padding 不会超出父元素;

    .title {
        color: #7b7b7b;
        font-size: 28rpx;
        line-height: 42rpx;
    }

    .loading-wrap {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 100%;
        height: calc(100vh - 376rpx - 68px - 26rpx - 42rpx);
    }
}

.member-item {
    display: flex;
    align-items: center;
    font-size: 34rpx;

    .icon-circle {
        width: 48rpx;
        height: 48rpx;
        border-radius: 50%;
        background-color: var(--primary-color);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 24rpx;
    }

    .icon-image {
        width: 48rpx;
        height: 48rpx;
        border-radius: 50%;
        margin-right: 24rpx;
    }

    .text-box {
        display: flex;
        align-items: center;
        justify-content: space-between;
        flex: 1;
        height: 112rpx;
        border-bottom: 1rpx solid #EEEFEF;

        .name-view {
            flex: 1;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .member-extra-info {
            color: #909399;
        }
    }

    &:last-child {
        .text-box {
            border-bottom: none;
        }
    }
}

.popupinfo {
    padding: 8px 16px;

    .popupinfotext {
        font-size: 16px;
        height: 36px;
        line-height: 36px;
    }
}

.popupAvatar {
    padding-top: 10px;
    padding-left: 15px;
    padding-right: 15px;
    padding-bottom: 10px;
    display: flex;
    justify-content: space-between;

    .icon-circle {
        width: 36rpx;
        height: 36rpx;
        border-radius: 50%;
        background-color: var(--primary-color);
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .text {
        font-size: 14px;
        color: #3b4144;
        overflow: hidden;
    }

    .popupPhone {
        display: flex;
        color: var(--primary-color);
        gap: 10px; // 调整间距为 10px;
    }
}

.popup-wrap {
    position: relative;
    width: 600rpx;
    height: 100%;

    .popup-title {
        font-size: 28rpx;
        line-height: 72rpx;
        color: #7B7B7B;
        display: flex;
        justify-content: space-between;
        align-items: center;

        view {
            color: var(--primary-color);
        }
    }

    .search-box {
        padding: 16rpx 32rpx;
        box-sizing: border-box;
        width: 100%;

        .input-box {
            height: 76rpx;
            border-radius: 38rpx;
            background-color: #F5F6F8;
            display: flex;
            align-items: center;
            padding: 0 32rpx;
        }
    }

    .roleItem {
        height: 112rpx;
        display: flex;
        width: calc(100% - 66rpx);
        align-items: center;
        justify-content: space-between;
        border-bottom: 1rpx solid #EEEFEF;
        font-size: 34rpx;

        .role-name {
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            color: #181818;
            margin-right: 20rpx;
        }
    }
}

.empty-wrap {
    margin-top: 100rpx;
}
</style>