<template>
	<template v-if="isWwEnv()">
		<ww-open-data v-if="initOpenData" :type="type" :openid="openid" />
		<text v-else>获取中...</text>
	</template>
	<template v-else>
		<text>{{ openid }}</text>
	</template>
</template>

<script setup>
import { isWwEnv } from '@/utils/common'
import { useUserStore } from '@/store/user';
import { computed } from 'vue';

const props = defineProps({
	type: {
		type: String,
		default: 'userName'
	},
	openid: {
		type: String,
		default: ''
	}
})

const initOpenData = computed(() => {
	return useUserStore().initOpenData;
})

</script>