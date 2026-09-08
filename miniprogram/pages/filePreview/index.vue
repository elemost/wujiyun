<template>
    <view class="file-preview-wrap" :style="themeColor">
        <uv-icon v-if="fileLoadStatus === 1" name="checkmark-circle-fill" size="100" color="#42b983" />
        <uv-icon v-if="fileLoadStatus === 2 || supportFileErr" name="close-circle-fill" size="100" color="#f56c6c" />
        <text v-if="fileLoadStatus > 0">{{ fileLoadStatus === 1 ? '文件预览完成' : '文件加载失败' }}</text>
        <text v-if="supportFileErr">{{ supportFileErr }}</text>
        <view class="btns" v-if="fileLoadStatus === 1">
            <uv-button @click="goBack" type="primary" :customStyle="{ border: 0 }">返回详情</uv-button>
            <uv-button :plain="true" :hairline="true" @click="openDocument(fileUrl)" type="primary">重新查看</uv-button>
        </view>
    </view>
</template>
<script setup>
import { onLoad } from '@dcloudio/uni-app';
import { ref } from 'vue'
import { useGlobalTheme } from '@/utils/theme';
const { themeColor } = useGlobalTheme();
const fileUrl = ref('');
const fileLoadStatus = ref(0)
const supportFileType = ['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'pdf', 'png', 'jpg', 'jpeg', 'gif', 'bmp']
const supportFileErr = ref('')

const goBack = () => {
    uni.navigateBack({
        delta: 1
    })
}

const openDocument = (url) => {
    const fileType = url?.split('.').pop()
    if (!supportFileType.includes(fileType)) {
        supportFileErr.value = `小程序暂不支持预览${fileType}文件类型`
        return;
    }
    supportFileErr.value = ''
    if(['png', 'jpg', 'jpeg', 'gif', 'bmp'].includes(fileType)) {
        uni.showLoading({
            title: '加载中',
        })
        uni.previewImage({
            urls: [url],
            success: () => {
                fileLoadStatus.value = 1
            },
            fail: () => {
                fileLoadStatus.value = 2
            },
            complete: () => {
                uni.hideLoading();
            }
        })
        return;
    }
    
    uni.showLoading({
        title: '加载中',
    })
    uni.downloadFile({
        url,
        success: function (res) {
            var filePath = res.tempFilePath;
            uni.openDocument({
                filePath: filePath,
                showMenu: true,
                success: function (res) {
                    fileLoadStatus.value = 1
                },
                fail: function (res) {
                    fileLoadStatus.value = 2
                },
                complete: function () {
                    uni.hideLoading();
                }
            });
        }
    });
}

// 正确获取参数的方式
onLoad((options) => {
    if (options.fileUrl) {
        fileUrl.value = decodeURIComponent(options.fileUrl);
        openDocument(fileUrl.value)
    } else {
        fileLoadStatus.value = 2
    }
});
</script>
<style lang="scss">
.file-preview-wrap {
    display: flex;
    align-items: center;
    // justify-content: center;
    width: 100%;
    height: 100%;
    flex-direction: column;
    gap: 30rpx;
    padding-top: 20%;

    text {
        font-size: 36rpx;
        margin-bottom: 20rpx;
    }

    .btns {
        display: flex;
        gap: 20rpx;
    }
}
</style>