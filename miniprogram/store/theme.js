import { defineStore } from "pinia";
import { ref } from "vue";
import { getToken } from "../utils/auth";
import { useUserStore } from '@/store/user';

export const useThemeStore = defineStore("theme", () => {
    //默认值
    const themeColor = ref({
        "--primary-color": "#14B59C",
    });

    // 获取主题色
    const getThemeColor = async () => {
        if (!getToken()) {
            themeColor.value = {
                "--primary-color": "#14B59C"
            };
            return themeColor.value["--primary-color"];
        }
        // 获取主题色
        const res = await useUserStore().getCompanyInfo();
        if (res?.subjectColor) {
            const subjectColor = JSON.parse(res.subjectColor);
            if (subjectColor?.color) {
                themeColor.value = {
                    "--primary-color": `#${subjectColor.color}`,
                };
            }
        }
        return themeColor.value["--primary-color"];
    };

    // 重置主题色
    const resetThemeColor = () => {
        themeColor.value = {
            "--primary-color": "#14B59C",
        };
    };

    return {
        themeColor,
        getThemeColor,
        resetThemeColor,
    };
});