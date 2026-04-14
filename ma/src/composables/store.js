import { storeToRefs } from "pinia";
import useAppStore from "@/store/app";

export const storeApp = () => {
  const { user, todayCheckin } = storeToRefs(useAppStore());
  const { refreshUser, refreshTodayCheckin } = useAppStore();
  return { user, todayCheckin, refreshUser, refreshTodayCheckin };
};
