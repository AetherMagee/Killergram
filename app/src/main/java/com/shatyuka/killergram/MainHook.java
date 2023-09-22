package com.shatyuka.killergram;

import java.util.Arrays;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            // Working with MessageController - sponsored messages and forwarding
            Class<?> messagesControllerClass = XposedHelpers.findClassIfExists("org.telegram.messenger.MessagesController", lpparam.classLoader);
            if (messagesControllerClass != null) {
                XposedBridge.hookAllMethods(messagesControllerClass, "getSponsoredMessages", XC_MethodReplacement.returnConstant(null));
                XposedBridge.hookAllMethods(messagesControllerClass, "isChatNoForwards", XC_MethodReplacement.returnConstant(false));
            } else {
                XposedBridge.log("[Killergram] messagesControllerClass not found");
            }

            // Working with ChatActivity - sponsored messages
            Class<?> chatUIActivityClass = XposedHelpers.findClassIfExists("org.telegram.ui.ChatActivity", lpparam.classLoader);
            if (chatUIActivityClass != null) {
                XposedBridge.hookAllMethods(chatUIActivityClass, "addSponsoredMessages", XC_MethodReplacement.returnConstant(null));
            } else {
                XposedBridge.log("[Killergram] chatUIActivityClass not found");
            }

            // Working with UserConfig - acc count
            Class<?> UserConfigClass = XposedHelpers.findClassIfExists("org.telegram.messenger.UserConfig", lpparam.classLoader);
            if (UserConfigClass != null) {
                XposedBridge.hookAllMethods(UserConfigClass, "getMaxAccountCount", XC_MethodReplacement.returnConstant(999));
                XposedBridge.hookAllMethods(UserConfigClass, "hasPremiumOnAccounts", XC_MethodReplacement.returnConstant(true));
            } else {
                XposedBridge.log("[Killergram] UserConfigClass not found");
            }
            // Working with SharedConfig - performance class
//                Class<?> SharedConfigClass = XposedHelpers.findClassIfExists("org.telegram.messenger.SharedConfig", lpparam.classLoader);
//                if (SharedConfigClass != null) {
//                    XposedBridge.hookAllMethods(SharedConfigClass, "getDevicePerformanceClass", XC_MethodReplacement.returnConstant(2));
//                } else {
//                    XposedBridge.log("[Killergram] SharedConfigClass not found");
//                }


            XposedBridge.log("[Killergram] Hooked successfully for " + lpparam.packageName);
        } catch (Throwable error) {
            XposedBridge.log("[Killergram] Unable to hook " + lpparam.packageName);
            XposedBridge.log(Arrays.toString(error.getStackTrace()));
        }
    }
}
